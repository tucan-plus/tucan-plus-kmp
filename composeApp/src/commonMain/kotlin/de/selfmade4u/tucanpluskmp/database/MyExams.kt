package de.selfmade4u.tucanpluskmp.data

import androidx.datastore.core.DataStore
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Upsert
import androidx.room3.immediateTransaction
import androidx.room3.useWriterConnection
import de.selfmade4u.tucanpluskmp.AppDatabase
import de.selfmade4u.tucanpluskmp.Settings
import de.selfmade4u.tucanpluskmp.connector.AuthenticatedResponse
import de.selfmade4u.tucanpluskmp.connector.MyExamsConnector
import de.selfmade4u.tucanpluskmp.connector.Semesterauswahl
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

// try here to use one table with per-element history
object MyExams {

    // fetch for all semesters and store all at once.
    suspend fun refresh(
        credentialSettingsDataStore: DataStore<Settings?>,
        database: AppDatabase
    ): AuthenticatedResponse<List<MyExam>> {
        when (val response = MyExamsConnector.getUncached(credentialSettingsDataStore, null)) {
            is AuthenticatedResponse.Success<MyExamsConnector.MyExamsResponse> -> {
                val result = coroutineScope {
                    response.response.semesters.map { semester ->
                        async {
                            when (val response = MyExamsConnector.getUncached(
                                credentialSettingsDataStore,
                                semester.id.toString().padStart(15, '0')
                            )) {
                                is AuthenticatedResponse.Success<MyExamsConnector.MyExamsResponse> -> {
                                    val time = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                                    AuthenticatedResponse.Success(response.response.exams.map { m ->
                                        MyExam(
                                            m.id,
                                            m.name,
                                            m.coursedetailsUrl,
                                            semester,
                                            m.examType,
                                            m.date,
                                        )
                                    })
                                }
                                else -> response.map<List<MyExam>>()
                            }
                        }
                    }
                }.awaitAll()
                val agwef: AuthenticatedResponse<List<MyExam>> = result.reduce { acc, response ->
                    when (acc) {
                        is AuthenticatedResponse.Success<List<MyExam>> -> when (response) {
                            is AuthenticatedResponse.Success<List<MyExam>> -> AuthenticatedResponse.Success(acc.response + response.response)
                            else -> response
                        }
                        else -> acc
                    }
                }
                return when (agwef) {
                    is AuthenticatedResponse.Success<List<MyExam>> -> AuthenticatedResponse.Success(persist(database, agwef.response))
                    else -> agwef.map()
                }
            }
            else -> return response.map()
        }
    }

    // there can be multiple exams with different types for one course
    @Entity(primaryKeys = ["id", "semester_id", "examType"])
    data class MyExam(
        var id: String,
        val name: String,
        val examType: String,
        @Embedded(prefix = "semester_")
        var semester: Semesterauswahl,
        val coursedetailsUrl: String,
        val date: String,
    )

    @Dao
    interface MyExamsDao {
        @Query("SELECT * FROM MyExam")
        suspend fun getAll(): List<MyExam>

        @Delete
        suspend fun deleteAll()

        @Upsert
        suspend fun upsert(vararg exams: MyExam)
    }

    val comparator =
        compareByDescending<MyExam> { it.semester.id }.thenBy { it.id }.thenBy { it.examType }

    // only store all once
    suspend fun persist(
        database: AppDatabase,
        result: List<MyExam>
    ): List<MyExam> {
        return database.useWriterConnection {
            it.immediateTransaction {
                val oldExams = database.getMyExamsDao().getAll()
                val exams = result.sortedWith(comparator)

                database.getMyExamsDao().deleteAll()
                database.getMyExamsDao().upsert(*exams.toTypedArray())
                exams
            }
        }
    }

    suspend fun getCached(database: AppDatabase): List<MyExam>? {
        val value = database.getMyExamsDao().getAll()
        if (value.isEmpty()) {
            return null
        }
        return value
    }
}