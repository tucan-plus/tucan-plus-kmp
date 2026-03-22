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
                                            semester,
                                            m.id,
                                            m.name,
                                            m.coursedetailsUrl,
                                            m.examType,
                                            m.date,
                                            time,
                                            time
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
        //val removed: Boolean, // exams can't be removed?
    )

    fun MyExam.isContentEqual(other: MyExam): Boolean {
        return semester == other.semester && id == other.id && name == other.name &&
                coursedetailsUrl == other.coursedetailsUrl &&
                examType == other.examType &&
                date == other.date
    }

    @Dao
    interface MyExamsDao {
        @Query("SELECT *, MAX(validUntil) FROM MyExam GROUP BY id, semester_id, examType")
        suspend fun getAll(): List<MyExam>

        @Insert
        suspend fun insert(myExam: MyExam): Long
    }

    val comparator =
        compareByDescending<MyExam> { it.semester.id }.thenBy { it.id }.thenBy { it.examType }

    data class ExamKey(
        val id: String,
        val semesterId: Long
    )

    fun MyExam.key() = ExamKey(id, semester.id)

    data class ExamDiff(
        val added: List<MyExam>,
        val removed: List<MyExam>,
        val changed: List<Pair<MyExam, MyExam>>
    )

    fun diffExamsOptimized(
        oldList: List<MyExam>,
        newList: List<MyExam>
    ): ExamDiff {

        val oldMap = HashMap<ExamKey, MyExam>(oldList.size)
        for (exam in oldList) {
            oldMap[exam.key()] = exam
        }

        val added = mutableListOf<MyExam>()
        val changed = mutableListOf<Pair<MyExam, MyExam>>()

        for (newExam in newList) {
            val key = newExam.key()
            val oldExam = oldMap.remove(key)

            when {
                oldExam == null -> {
                    added += newExam
                }
                !oldExam.isContentEqual(newExam) -> {
                    changed += oldExam to newExam
                }
            }
        }

        val removed = oldMap.values.toList()

        return ExamDiff(added, removed, changed)
    }

    // only store all once
    suspend fun persist(
        database: AppDatabase,
        result: List<MyExam>
    ): List<MyExam> {
        return database.useWriterConnection {
            it.immediateTransaction {
                // assume exams can't be removed. that can probably happen with studienbüro, right?
                val oldExams = database.getMyExamsDao().getAll().sortedWith(comparator)
                val exams = result.sortedWith(comparator)
                val diff = diffExamsOptimized(oldExams, exams)

                // TODO

                //database.getMyExamsDao().insert(*exams.toTypedArray())
                exams
            }
        }
    }

    suspend fun getCached(database: AppDatabase): List<MyExam>? {
        val value = database.getMyExamsDao().getAll()
        if (value.isEmpty()) {
            return null
        }
        return value.sortedWith(comparator)
    }
}