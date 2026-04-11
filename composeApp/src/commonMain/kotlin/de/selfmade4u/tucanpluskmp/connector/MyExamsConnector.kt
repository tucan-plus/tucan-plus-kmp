package de.selfmade4u.tucanpluskmp.connector

import androidx.datastore.core.DataStore
import com.fleeksoft.ksoup.nodes.TextNode
import de.selfmade4u.tucanpluskmp.Localizer
import de.selfmade4u.tucanpluskmp.Root
import de.selfmade4u.tucanpluskmp.Settings
import de.selfmade4u.tucanpluskmp.TucanUrl
import de.selfmade4u.tucanpluskmp.a
import de.selfmade4u.tucanpluskmp.b
import de.selfmade4u.tucanpluskmp.br
import de.selfmade4u.tucanpluskmp.connector.Common.parseBase
import de.selfmade4u.tucanpluskmp.connector.Common.parseCommonHeaders
import de.selfmade4u.tucanpluskmp.data.MyExams
import de.selfmade4u.tucanpluskmp.div
import de.selfmade4u.tucanpluskmp.form
import de.selfmade4u.tucanpluskmp.h1
import de.selfmade4u.tucanpluskmp.input
import de.selfmade4u.tucanpluskmp.label
import de.selfmade4u.tucanpluskmp.option
import de.selfmade4u.tucanpluskmp.p
import de.selfmade4u.tucanpluskmp.peek
import de.selfmade4u.tucanpluskmp.peekAttribute
import de.selfmade4u.tucanpluskmp.response
import de.selfmade4u.tucanpluskmp.script
import de.selfmade4u.tucanpluskmp.select
import de.selfmade4u.tucanpluskmp.shouldIgnore
import de.selfmade4u.tucanpluskmp.style
import de.selfmade4u.tucanpluskmp.table
import de.selfmade4u.tucanpluskmp.tbody
import de.selfmade4u.tucanpluskmp.td
import de.selfmade4u.tucanpluskmp.th
import de.selfmade4u.tucanpluskmp.thead
import de.selfmade4u.tucanpluskmp.tr
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

// loop semester by semester because otherwise we can't really associate entries with their semester. maybe just not support the "all"?
object MyExamsConnector : Connector<String?, MyExamsConnector.MyExamsResponse> {

    data class MyExamsResponse(var selectedSemester: Semesterauswahl, var semesters: List<Semesterauswahl>, var exams: List<MyExams.MyExam>)

    override suspend fun getUncached(
        credentialSettingsDataStore: DataStore<Settings?>,
        input: String?
    ): AuthenticatedResponse<MyExamsResponse> {
        return fetchAuthenticatedWithReauthentication(
            credentialSettingsDataStore,
            { sessionId -> "https://www.tucan.tu-darmstadt.de/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=MYEXAMS&ARGUMENTS=-N$sessionId,-N000318,${if (input != null) { "-N$input" } else { "" }}" },
            parser = { sessionId, menuLocalizer, response -> parseHttpResponse("000318", sessionId, menuLocalizer, response) }
        )
    }

    override suspend fun parseHttpResponse(menuId: String, sessionId: String, menuLocalizer: Localizer, response: HttpResponse): ParserResponse<MyExamsResponse> {
        return response(response) {
            parseCommonHeaders()
            root {
                parse(menuId, sessionId, menuLocalizer)
            }
        }
    }

    override fun Root.parse(menuId: String, sessionId: String, menuLocalizer: Localizer): ParserResponse<MyExamsResponse> {
        val exams = mutableListOf<MyExams.MyExam>()
        val semesters = mutableListOf<Semesterauswahl>()
        var selectedSemester: Semesterauswahl? = null
        // menu id changes depending on language
        val response = parseBase(sessionId, menuLocalizer, menuId, {
            if (peek() != null) {
                style {
                    attribute("type", "text/css")
                    extractData()
                }
                style {
                    attribute("type", "text/css")
                    extractData()
                }
            } else {
                print("not the normal page")
            }
        }) { localizer: Localizer, pageType ->
            if (pageType == "timeout") {
                script {
                    attribute("type", "text/javascript")
                    // empty
                }
                h1 { text("Timeout!") }
                p {
                    b {
                        text("Es wurde seit den letzten 30 Minuten keine Abfrage mehr abgesetzt.")
                        br {}
                        text("Bitte melden Sie sich erneut an.")
                    }
                }
                return@parseBase ParserResponse.SessionTimeout()
            }
            check(pageType == "myexams") { pageType }
            script {
                attribute("type", "text/javascript")
                // empty
            }
            h1 { extractText() }
            div {
                attribute("class", "tb")

                form {
                    attribute("id", "semesterchange")
                    attribute("action", "/scripts/mgrqispi.dll")
                    attribute("method", "post")
                    attribute("class", "pageElementTop")

                    div {
                        div {
                            attribute("class", "tbhead")
                            text(localizer.exams)
                        }

                        div {
                            attribute("class", "tbsubhead")
                            text(localizer.choose_semester)
                        }

                        div {
                            attribute("class", "formRow")
                            div {
                                attribute("class", "inputFieldLabel long")
                                label {
                                    attribute("for", "semester")
                                    text(localizer.course_module_semester)
                                }
                                select {
                                    attribute("id", "semester")
                                    attribute("name", "semester")
                                    attribute(
                                        "onchange",
                                        "reloadpage.createUrlAndReload('/scripts/mgrqispi.dll','CampusNet','MYEXAMS','$sessionId','$menuId','-N'+this.value);"
                                    )
                                    attribute("class", "tabledata")

                                    // we can predict the value so we could use this at some places do directly get correct value
                                    // maybe do everywhere for consistency
                                    while (peek() != null) {
                                        val value: Long
                                        val selected: Boolean
                                        val semester: Semester
                                        val year: Int
                                        option {
                                            value = attributeValue("value").trimStart('0').toLong()
                                            selected = if (peekAttribute()?.key == "selected") {
                                                attribute("selected", "selected")
                                                true
                                            } else {
                                                false
                                            }
                                            val semesterName =
                                                extractText() // SoSe 2025; WiSe 2024/25
                                            if (semesterName == localizer.all) {
                                                return@option;
                                            }
                                            if (semesterName.startsWith(("SoSe "))) {
                                                year = semesterName.removePrefix("SoSe ").toInt()
                                                semester = Semester.Sommersemester
                                            } else {
                                                year = semesterName.removePrefix("WiSe ")
                                                    .substringBefore("/").toInt()
                                                semester = Semester.Wintersemester
                                            }
                                            if (selected) {
                                                selectedSemester = Semesterauswahl(
                                                    value,
                                                    year,
                                                    semester
                                                )
                                            }
                                            semesters.add(
                                                Semesterauswahl(
                                                    value,
                                                    year,
                                                    semester
                                                )
                                            )
                                        }
                                    }
                                }

                                input {
                                    attribute("name", "Refresh")
                                    attribute("type", "submit")
                                    attribute("value", localizer.refresh)
                                    attribute("class", "img img_arrowReload")
                                }
                            }
                        }

                        input {
                            attribute("name", "APPNAME"); attribute(
                            "type",
                            "hidden"
                        ); attribute("value", "CampusNet")
                        }
                        input {
                            attribute("name", "PRGNAME"); attribute(
                            "type",
                            "hidden"
                        ); attribute("value", "MYEXAMS")
                        }
                        input {
                            attribute("name", "ARGUMENTS"); attribute(
                            "type",
                            "hidden"
                        ); attribute("value", "sessionno,menuno,semester")
                        }
                        input {
                            attribute("name", "sessionno"); attribute("type", "hidden"); attribute(
                            "value",
                            sessionId
                        )
                        }
                        input {
                            attribute("name", "menuno"); attribute("type", "hidden"); attribute(
                            "value",
                            menuId
                        )
                        }
                    }
                }

                table {
                    attribute("class", "nb list")

                    thead {
                        tr {
                            attribute("class", "tbcontrol");
                            td {
                                attribute("colspan", "5")
                                a {
                                    attribute("href", "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=EXAMREGISTRATION&amp;ARGUMENTS=-N$sessionId,-N000318,-N${selectedSemester!!.id.toString().padStart(15, '0')}")
                                    attribute("class", "arrow")
                                    text(localizer.exam_registration)
                                }
                            }
                        }
                        tr {
                            th { attribute("scope", "col"); attribute("id", localizer.module_results_no); text(localizer.module_results_no) }
                            th { attribute("scope", "col"); attribute("id", "Course_event_module"); text(localizer.my_exams_course_or_module)}
                            th { attribute("scope", "col"); attribute("id", "Name"); text(localizer.my_exams_name) }
                            th { attribute("scope", "col"); attribute("id", "Date"); text(localizer.my_exams_date) }
                            th {
                            }
                        }
                    }

                    tbody {
                        while (peek()?.childNodes()?.filterNot(::shouldIgnore)?.first()
                                ?.normalName() == "td"
                        ) {
                            val id: String
                            val name: String
                            val coursedetailsOrModuleDetails: TucanUrl.CourseOrModuleDetails
                            val examType: String
                            val date: String
                            tr {
                                td {
                                    attribute("class", "tbdata");
                                    // id
                                    id = extractText()
                                }
                                td {
                                    attribute("class", "tbdata");
                                    a {
                                        attribute("class", "link");
                                        if (peekAttribute()?.key == "name") {
                                            attribute("name", "eventLink");
                                        }
                                        coursedetailsOrModuleDetails = TucanUrl.CourseOrModuleDetails.fromString(attributeValue("href"));
                                        // module title
                                        name = extractText()
                                    }
                                    if (peek() != null) {
                                        br { }
                                        if (peek() is TextNode) {
                                            // list of courses
                                            extractText()
                                        } else {
                                            // thesis
                                            b {
                                                text(localizer.thesis_subject)
                                            }
                                            val title = extractText()
                                            br {}
                                            val handedIn = extractText()
                                            br {}
                                        }
                                    }
                                }
                                td {
                                    attribute("class", "tbdata")
                                    a {
                                        attribute("class", "link");
                                        // examdetails
                                        attributeValue("href");
                                        /// type of exam
                                        examType = extractText()
                                    }
                                }
                                td {
                                    attribute("class", "tbdata")
                                    if (peek() is TextNode) {
                                        date = extractText()
                                    } else {
                                        a {
                                            attribute("class", "link");
                                            // courseprep date link
                                            attributeValue("href");
                                            // date text
                                            date = extractText()
                                        }
                                    }
                                }
                                td {
                                    attribute("class", "tbdata")
                                    if (peek() is TextNode) {
                                        extractText()
                                    } else {
                                        a {
                                            // EXAMUNREG link
                                            attributeValue("href");
                                            attribute("class", "img img_arrowLeftRed");
                                            text(localizer.unregister)
                                        }
                                    }
                                }
                            }
                            val exam = MyExams.MyExam(
                                id,
                                name,
                                examType,
                                selectedSemester!!,
                                coursedetailsOrModuleDetails as? TucanUrl.COURSEDETAILS,
                                coursedetailsOrModuleDetails as? TucanUrl.MODULEDETAILS,
                                date,
                            )
                            exams.add(exam)
                        }
                    }
                }
            }
            return@parseBase ParserResponse.Success(MyExamsResponse(selectedSemester!!, semesters, exams))
        }
        return response
    }

    override fun extractRelevantPages(credentialSettingsDataStore: DataStore<Settings?>): Flow<String?> {
        TODO("Not yet implemented")
    }
}