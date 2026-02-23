package de.selfmade4u.tucanpluskmp.database

import androidx.room3.Dao
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Relation
import de.selfmade4u.tucanpluskmp.connector.ModuleGrade
import de.selfmade4u.tucanpluskmp.connector.Semesterauswahl

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

@Entity
data class ModuleResultsEntity(@PrimaryKey(autoGenerate = true) var id: Long)

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
    @Query("SELECT * FROM moduleresult")
    suspend fun getAll(): List<ModuleResultsEntity>

    @Query("SELECT * FROM moduleresult")
    suspend fun getModuleResultsWithModules(): List<ModuleResults>

    @Insert
    suspend fun insert(moduleResultsEntity: ModuleResultsEntity): Long

    @Query("SELECT * FROM moduleresult ORDER BY id DESC LIMIT 1")
    suspend fun getLast(): ModuleResults?
}

@Dao
interface ModuleResultDao {
    @Insert
    suspend fun insertAll(vararg modules: ModuleResultEntity): List<Long>

    @Query("SELECT * FROM ModuleResultModule WHERE moduleResultId = :moduleResultId")
    suspend fun getForModuleResult(moduleResultId: Long): List<ModuleResultEntity>
}
