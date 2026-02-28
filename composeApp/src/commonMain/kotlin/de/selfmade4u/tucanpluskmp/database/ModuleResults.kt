package de.selfmade4u.tucanpluskmp.database

import androidx.datastore.core.DataStore
import androidx.room3.Dao
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Relation
import androidx.room3.Transaction
import androidx.room3.immediateTransaction
import androidx.room3.useWriterConnection
import de.selfmade4u.tucanpluskmp.AppDatabase
import de.selfmade4u.tucanpluskmp.Settings
import de.selfmade4u.tucanpluskmp.connector.AuthenticatedResponse
import de.selfmade4u.tucanpluskmp.connector.ModuleGrade
import de.selfmade4u.tucanpluskmp.connector.ModuleResultsConnector
import de.selfmade4u.tucanpluskmp.connector.ModuleResultsConnector.getModuleResultsUncached
import de.selfmade4u.tucanpluskmp.connector.Semesterauswahl
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

suspend fun refreshModuleResults(
    credentialSettingsDataStore: DataStore<Settings?>,
    database: AppDatabase
): AuthenticatedResponse<ModuleResults> {
    when (val response = getModuleResultsUncached(credentialSettingsDataStore, null)) {
        is AuthenticatedResponse.Success<ModuleResultsConnector.ModuleResultsResponse> -> {
            val result = coroutineScope {
                response.response.semesters.map { semester ->
                    async {
                        when (val response = getModuleResultsUncached(
                            credentialSettingsDataStore,
                            semester.id.toString().padStart(15, '0')
                        )) {
                            is AuthenticatedResponse.Success<ModuleResultsConnector.ModuleResultsResponse> -> {
                                AuthenticatedResponse.Success(response.response.modules.map { m ->
                                    ModuleResultEntity(
                                        0,
                                        semester,
                                        m.id,
                                        m.name,
                                        m.grade,
                                        m.credits,
                                        m.resultdetailsUrl,
                                        m.gradeoverviewUrl
                                    )
                                })
                            }
                            else -> response.map<List<ModuleResultEntity>>()
                        }
                    }
                }
            }.awaitAll()
            val agwef: AuthenticatedResponse<List<ModuleResultEntity>> = result.reduce { acc, response ->
                when (acc) {
                    is AuthenticatedResponse.Success<List<ModuleResultEntity>> -> when (response) {
                        is AuthenticatedResponse.Success<List<ModuleResultEntity>> -> AuthenticatedResponse.Success(acc.response + response.response)
                        else -> response
                    }
                    else -> acc
                }
            }
            return when (agwef) {
                is AuthenticatedResponse.Success<List<ModuleResultEntity>> -> AuthenticatedResponse.Success(persist(database, agwef.response))
                else -> agwef.map()
            }
        }
        else -> return response.map()
    }
}

@Entity(primaryKeys = ["moduleResultsId", "id", "semester_id"])
data class ModuleResultEntity(
    var moduleResultsId: Long,
    @Embedded(prefix = "semester_")
    var semester: Semesterauswahl,
    // embedded module
    var id: String,
    val name: String,
    val grade: ModuleGrade?,
    val credits: Int,
    val resultdetailsUrl: String?,
    val gradeoverviewUrl: String?
)

/** Within the timeframe validSince to validUntil, it is guaranteed that it had the specified content (minus theoretical ABA problem) */
@Entity
data class ModuleResultsEntity(@PrimaryKey(autoGenerate = true) var id: Long, var validSince: LocalDateTime, var validUntil: LocalDateTime)

data class ModuleResults(
    @Embedded val moduleResultsEntity: ModuleResultsEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "moduleResultsId"
    )
    val moduleResults: List<ModuleResultEntity>
)

@Dao
interface ModuleResultsDao {
    @Query("SELECT * FROM ModuleResultsEntity")
    suspend fun getAll(): List<ModuleResultsEntity>

    @Transaction
    @Query("SELECT * FROM ModuleResultsEntity")
    suspend fun getModuleResultsWithModules(): List<ModuleResults>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(moduleResultsEntity: ModuleResultsEntity): Long

    @Transaction
    @Query("SELECT * FROM ModuleResultsEntity ORDER BY id DESC LIMIT 1")
    fun getLast(): Flow<ModuleResults?>
}

@Dao
interface ModuleResultDao {
    @Insert
    suspend fun insertAll(vararg modules: ModuleResultEntity): List<Long>

    @Query("SELECT * FROM ModuleResultEntity WHERE moduleResultsId = :moduleResultsId")
    suspend fun getForModuleResults(moduleResultsId: Long): List<ModuleResultEntity>
}

// only store all once
suspend fun persist(
    database: AppDatabase,
    result: List<ModuleResultEntity>
): ModuleResults {
    // TODO check whether there were changes?
    return database.useWriterConnection {
        it.immediateTransaction {
            val time = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            val last = database.getModuleResultsDao().getLast().first();
            if (last?.moduleResults == result) {
                // update
                database.getModuleResultsDao().insertOrReplace(last.moduleResultsEntity.copy(validUntil = time))
                last
            } else {
                // insert
                val moduleResultsId = database.getModuleResultsDao().insertOrReplace(ModuleResultsEntity(0, time, time))
                val modules = result.map { m -> m.copy(moduleResultsId = moduleResultsId) }.sortedWith(compareByDescending<ModuleResultEntity>{it.semester.id}.thenBy { it.id})
                database.getModuleResultDao().insertAll(*modules.toTypedArray())
                ModuleResults(ModuleResultsEntity(moduleResultsId, time, time), modules)
            }
        }
    }
}

fun getCached(database: AppDatabase): Flow<ModuleResults?> {
   return database.getModuleResultsDao().getLast().map {
       it?.let { value ->
           value.copy(moduleResults = value.moduleResults.sortedWith(compareByDescending<ModuleResultEntity> { it.semester.id }.thenBy { it.id }))
       }
   }
}