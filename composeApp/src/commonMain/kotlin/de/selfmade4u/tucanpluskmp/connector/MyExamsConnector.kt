package de.selfmade4u.tucanpluskmp.connector

import androidx.datastore.core.DataStore
import com.fleeksoft.ksoup.nodes.TextNode
import de.selfmade4u.tucanpluskmp.Localizer
import de.selfmade4u.tucanpluskmp.Root
import de.selfmade4u.tucanpluskmp.Settings
import de.selfmade4u.tucanpluskmp.TucanUrl
import de.selfmade4u.tucanpluskmp.connector.Common.parseBase
import de.selfmade4u.tucanpluskmp.connector.Common.parseCommonHeaders
import de.selfmade4u.tucanpluskmp.data.MyExams
import de.selfmade4u.tucanpluskmp.response
import de.selfmade4u.tucanpluskmp.shouldIgnore
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import de.selfmade4u.tucanpluskmp.*

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
                style.attributes {
                    attribute("type", "text/css")
                }.content {
                    extractData()
                }
                style.attributes {
                    attribute("type", "text/css")
                }.content {
                    extractData()
                }
            } else {
                print("not the normal page")
            }
        }) { localizer: Localizer, pageType ->
            if (pageType == "timeout") {
                script.attributes {
                    attribute("type", "text/javascript")
                    // empty
                }
                h1.content { text("Timeout!") }
                p.content {
                    b.content {
                        text("Es wurde seit den letzten 30 Minuten keine Abfrage mehr abgesetzt.")
                        br.content {}
                        text("Bitte melden Sie sich erneut an.")
                    }
                }
                return@parseBase ParserResponse.SessionTimeout()
            }
            check(pageType == "myexams") { pageType }
            script.attributes {
                attribute("type", "text/javascript")
                // empty
            }
            h1.content { extractText() }
            div.attributes {
                attribute("class", "tb")
            }.content {

                form.attributes {
                    attribute("id", "semesterchange")
                    attribute("action", "/scripts/mgrqispi.dll")
                    attribute("method", "post")
                    attribute("class", "pageElementTop")
                }.content {

                    div.content {
                        div.attributes {
                            attribute("class", "tbhead")
                        }.content {
                            text(localizer.exams)
                        }

                        div.attributes {
                            attribute("class", "tbsubhead")
                        }.content {
                            text(localizer.choose_semester)
                        }

                        div.attributes {
                            attribute("class", "formRow")
                        }.content {
                            div.attributes {
                                attribute("class", "inputFieldLabel long")
                            }.content {
                                label.attributes {
                                    attribute("for", "semester")
                                }.content {
                                    text(localizer.course_module_semester)
                                }
                                select.attributes {
                                    attribute("id", "semester")
                                    attribute("name", "semester")
                                    attribute(
                                        "onchange",
                                        "reloadpage.createUrlAndReload('/scripts/mgrqispi.dll','CampusNet','MYEXAMS','$sessionId','$menuId','-N'+this.value);"
                                    )
                                    attribute("class", "tabledata")
                                }.content {

                                    // we can predict the value so we could use this at some places do directly get correct value
                                    // maybe do everywhere for consistency
                                    while (peek() != null) {
                                        val value: Long
                                        val selected: Boolean
                                        val semester: Semester
                                        val year: Int
                                        option.attributes {
                                            value = attributeValue("value").trimStart('0').toLong()
                                            selected = if (peekAttribute()?.key == "selected") {
                                                attribute("selected", "selected")
                                                true
                                            } else {
                                                false
                                            }
                                        }.content {
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

                                input.attributes {
                                    attribute("name", "Refresh")
                                    attribute("type", "submit")
                                    attribute("value", localizer.refresh)
                                    attribute("class", "img img_arrowReload")
                                }
                            }
                        }

                        input.attributes {
                            attribute("name", "APPNAME"); attribute(
                            "type",
                            "hidden"
                        ); attribute("value", "CampusNet")
                        }
                        input.attributes {
                            attribute("name", "PRGNAME"); attribute(
                            "type",
                            "hidden"
                        ); attribute("value", "MYEXAMS")
                        }
                        input.attributes {
                            attribute("name", "ARGUMENTS"); attribute(
                            "type",
                            "hidden"
                        ); attribute("value", "sessionno,menuno,semester")
                        }
                        input.attributes {
                            attribute("name", "sessionno"); attribute("type", "hidden"); attribute(
                            "value",
                            sessionId
                        )
                        }
                        input.attributes {
                            attribute("name", "menuno"); attribute("type", "hidden"); attribute(
                            "value",
                            menuId
                        )
                        }
                    }
                }

                table.attributes {
                    attribute("class", "nb list")
                }.content {

                    thead.content {
                        tr.attributes {
                            attribute("class", "tbcontrol");
                        }.content {
                            td.attributes {
                                attribute("colspan", "5")
                            }.content {
                                a.attributes {
                                    attribute(
                                        "href",
                                        "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=EXAMREGISTRATION&amp;ARGUMENTS=-N$sessionId,-N000318,-N${
                                            selectedSemester!!.id.toString().padStart(15, '0')
                                        }"
                                    )
                                    attribute("class", "arrow")
                                }.content {
                                    text(localizer.exam_registration)
                                }
                            }
                        }
                        tr.content {
                            th.attributes { attribute("scope", "col"); attribute("id", localizer.module_results_no); }.content { text(localizer.module_results_no) }
                            th.attributes { attribute("scope", "col"); attribute("id", "Course_event_module"); }.content { text(localizer.my_exams_course_or_module)}
                            th.attributes { attribute("scope", "col"); attribute("id", "Name"); }.content { text(localizer.my_exams_name) }
                            th.attributes { attribute("scope", "col"); attribute("id", "Date"); }.content { text(localizer.my_exams_date) }
                            th.content {
                            }
                        }
                    }

                    tbody.content {
                        while (peek()?.firstChild()?.normalName() == "td") {
                            val id: String
                            val name: String
                            val coursedetailsOrModuleDetails: TucanUrl.CourseOrModuleDetails
                            val examType: String
                            val date: String
                            tr.content {
                                td.attributes {
                                    attribute("class", "tbdata");
                                }.content {
                                    // id
                                    id = extractText()
                                }
                                td.attributes {
                                    attribute("class", "tbdata");
                                }.content {
                                    a.attributes {
                                        attribute("class", "link");
                                        if (peekAttribute()?.key == "name") {
                                            attribute("name", "eventLink");
                                        }
                                        coursedetailsOrModuleDetails =
                                            TucanUrl.CourseOrModuleDetails.fromString(attributeValue("href"));
                                    }.content {
                                        // module title
                                        name = extractText()
                                    }
                                    if (peek() != null) {
                                        br.content { }
                                        if (peek() is TextNode) {
                                            // list of courses
                                            extractText()
                                        } else {
                                            // thesis
                                            b.content {
                                                text(localizer.thesis_subject)
                                            }
                                            val title = extractText()
                                            br.content {}
                                            val handedIn = extractText()
                                            br.content {}
                                        }
                                    }
                                }
                                td.attributes {
                                    attribute("class", "tbdata")
                                }.content {
                                    a.attributes {
                                        attribute("class", "link");
                                        // examdetails
                                        attributeValue("href");
                                    }.content {
                                        /// type of exam
                                        examType = extractText()
                                    }
                                }
                                td.attributes {
                                    attribute("class", "tbdata")
                                }.content {
                                    if (peek() is TextNode) {
                                        date = extractText()
                                    } else {
                                        a.attributes {
                                            attribute("class", "link");
                                            // courseprep date link
                                            attributeValue("href");
                                        }.content {
                                            // date text
                                            date = extractText()
                                        }
                                    }
                                }
                                td.attributes {
                                    attribute("class", "tbdata")
                                }.content {
                                    if (peek() is TextNode) {
                                        extractText()
                                    } else {
                                        a.attributes {
                                            // EXAMUNREG link
                                            attributeValue("href");
                                            attribute("class", "img img_arrowLeftRed");
                                        }.content {
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