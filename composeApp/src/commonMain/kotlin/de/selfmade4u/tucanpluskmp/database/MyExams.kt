package de.selfmade4u.tucanpluskmp.data

import androidx.datastore.core.DataStore
import androidx.room3.Dao
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.Query
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
                                    AuthenticatedResponse.Success(response.response.exams.map { m ->
                                        MyExam(
                                            0,
                                            semester,
                                            m.id,
                                            m.name,
                                            m.coursedetailsUrl,
                                            m.examType,
                                            m.date
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

    // https://www.sqlite.org/lang_select.html#bare_columns_in_an_aggregate_query
    // there can be multiple exams with different types for one course
    @Entity(primaryKeys = ["myExamsId", "id", "semester_id", "examType"])
    data class MyExam(
        @Embedded(prefix = "semester_")
        var semester: Semesterauswahl,
        var id: String,
        val name: String,
        val coursedetailsUrl: String,
        val examType: String,
        val date: String,
        var validSince: LocalDateTime,
        var validUntil: LocalDateTime,
        val deleted: Boolean,
    )

    @Dao
    interface MyExamsDao {
        @Query("SELECT *, MAX(validUntil) FROM myexams GROUP BY id, semester_id, examType")
        suspend fun getAll(): List<MyExams>

        @Insert
        suspend fun insert(myExams: MyExams): Long
    }

    // only store all once
    suspend fun persist(
        database: AppDatabase,
        result: List<MyExam>
    ): List<MyExam> {
        // TODO check whether there were changes?
        return database.useWriterConnection {
            it.immediateTransaction {
                val myExamsId = database.myExamsDao().insert(MyExams(0))
                val exams = result.map { m -> m.copy(myExamsId = myExamsId) }.sortedWith(compareByDescending<MyExam>{it.semester.id}.thenBy { it.id})
                database.myExamsExamDao().insertAll(*exams.toTypedArray())
                MyExamsWithExams(MyExams(myExamsId), exams)
            }
        }
    }

    suspend fun getCached(database: AppDatabase): List<MyExam>? {
        val value = database.myExamsDao().getLast()
        return value?.let { value ->
            value.copy(exams = value.exams.sortedWith(compareByDescending<MyExam>{it.semester.id}.thenBy { it.id}))
        }
    }
}