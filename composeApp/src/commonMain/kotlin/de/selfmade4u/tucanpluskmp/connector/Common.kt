package de.selfmade4u.tucanpluskmp.connector

import androidx.datastore.core.DataStore
import de.selfmade4u.tucanpluskmp.Body
import de.selfmade4u.tucanpluskmp.EnglishLocalizer
import de.selfmade4u.tucanpluskmp.GermanLocalizer
import de.selfmade4u.tucanpluskmp.Head
import de.selfmade4u.tucanpluskmp.Localizer
import de.selfmade4u.tucanpluskmp.Response
import de.selfmade4u.tucanpluskmp.Root
import de.selfmade4u.tucanpluskmp.Settings
import de.selfmade4u.tucanpluskmp.TextAndId
import de.selfmade4u.tucanpluskmp.connector.ModuleResultsConnector.ModuleResultsResponse
import de.selfmade4u.tucanpluskmp.connector.MyExamsConnector.MyExamsResponse
import de.selfmade4u.tucanpluskmp.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

interface Connector<I, O> {

    suspend fun getUncached(
        credentialSettingsDataStore: DataStore<Settings?>,
        input: I
    ): AuthenticatedResponse<O>

    suspend fun parseHttpResponse(
        menuId: String,
        sessionId: String,
        menuLocalizer: Localizer,
        response: HttpResponse
    ): ParserResponse<O>

    fun Root.parse(menuId: String, sessionId: String, menuLocalizer: Localizer): ParserResponse<O>

    /** Return pages that can be parsed by this connector while trying to make as little assumptions as possible. This is used to implement the datenspende and create the parser using AI. */
    fun extractRelevantPages(credentialSettingsDataStore: DataStore<Settings?>): Flow<I>
}

object Common {
    fun <T> Root.parseBase(
        sessionId: String,
        menuLocalizer: Localizer,
        menuId: String,
        headInit: HeadContentScope.() -> Unit,
        inner: BodyContentScope.(localizer: Localizer, pageType: String) -> T
    ): T {
        var sessionId = sessionId
        var menuId = menuId
        doctype.attributes {
            attribute("#doctype", "html")
            attribute("name", "html")
            attribute("publicId", "-//W3C//DTD XHTML 1.0 Strict//EN")
            attribute("systemId", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd")
            attribute("pubsyskey", "PUBLIC")
        }
        val localizer: Localizer
        return html.attributes {
            attribute("xmlns", "http://www.w3.org/1999/xhtml")
            val language = attributeValue("xml:lang")
            localizer = when (language) {
                "de" -> GermanLocalizer
                "en" -> EnglishLocalizer
                else -> throw IllegalStateException()
            }
            attribute("lang", localizer.language)
        }.content {
            head.content {
                title.content {
                    text("Technische Universität Darmstadt")
                }
                meta.attributes {
                    attribute("http-equiv", "X-UA-Compatible")
                    attribute("content", "IE=edge")
                }
                meta.attributes {
                    attribute("http-equiv", "cache-control")
                    attribute("content", "no-cache")
                }
                meta.attributes {
                    attribute("http-equiv", "expires")
                    attribute("content", "-1")
                }
                meta.attributes {
                    attribute("http-equiv", "pragma")
                    attribute("content", "no-cache")
                }
                meta.attributes {
                    attribute("http-equiv", "Content-Type")
                    attribute("content", "text/html; charset=utf-8")
                }
                meta.attributes {
                    attribute("http-equiv", "Content-Script-Type")
                    attribute("content", "text/javascript")
                }
                meta.attributes {
                    attribute("name", "referrer")
                    attribute("content", "origin")
                }
                meta.attributes {
                    attribute("name", "keywords")
                    attribute(
                        "content",
                        "Datenlotsen,Datenlotsen Informationssysteme GmbH,CampusNet,Campus Management"
                    )
                }
                link.attributes {
                    attribute("rel", "shortcut icon")
                    attribute("type", "image/x-icon")
                    attribute("href", "/gfx/tuda/icons/favicon.ico")
                }
                script.attributes {
                    attribute("src", "/js/jquery.js")
                    attribute("type", "text/javascript")
                }
                script.attributes {
                    attribute("src", "/js/checkDate.js")
                    attribute("type", "text/javascript")
                }
                script.attributes {
                    attribute("src", "/js/edittext.js")
                    attribute("type", "text/javascript")
                }
                script.attributes {
                    attribute("src", "/js/skripts.js")
                    attribute("type", "text/javascript")
                }
                script.attributes {
                    attribute("src", "/js/x.js")
                    attribute("type", "text/javascript")
                }
                script.attributes {
                    attribute("type", "text/javascript")
                }.content {
                    extractData()
                }
                link.attributes {
                    attribute("id", "defLayout")
                    attribute("href", "/css/_default/def_layout.css")
                    attribute("rel", "stylesheet")
                    attribute("type", "text/css")
                    attribute("media", "screen")
                }
                link.attributes {
                    attribute("id", "defMenu")
                    attribute("href", "/css/_default/def_menu.css")
                    attribute("rel", "stylesheet")
                    attribute("type", "text/css")
                    attribute("media", "screen")
                }
                link.attributes {
                    attribute("id", "defStyles")
                    attribute("href", "/css/_default/def_styles.css")
                    attribute("rel", "stylesheet")
                    attribute("type", "text/css")
                }
                link.attributes {
                    attribute("id", "pagePrint")
                    attribute("href", "/css/_default/def_print.css")
                    attribute("rel", "stylesheet")
                    attribute("type", "text/css")
                    attribute("media", "print")
                }
                link.attributes {
                    attribute("id", "pageStyle")
                    attribute("href", "/css/styles.css")
                    attribute("rel", "stylesheet")
                    attribute("type", "text/css")
                }
                link.attributes {
                    attribute("id", "pageColors")
                    attribute("href", "/css/colors.css")
                    attribute("rel", "stylesheet")
                    attribute("type", "text/css")
                    attribute("media", "screen")
                }
                headInit()
            }
            val pageType: String
            body.attributes {
                pageType = attributeValue("class")
                if (pageType == "timeout" || pageType == "access_denied") {
                    sessionId = "000000000000001"
                    menuId = "000000"
                }
            }.content {

                div.attributes {
                    attribute("id", "Cn-system-desc")
                }

                script.attributes {
                    attribute("type", "text/javascript")
                }.content {
                    val _unused = extractData()
                }

                div.attributes {
                    attribute("id", "acc_pageDescription")
                    attribute("class", "hidden")
                }.content {
                    a.attributes {
                        attribute("name", "keypadDescription")
                        attribute("class", "hidden")
                    }.content {
                        text("keypadDescription")
                    }
                    text(localizer.javascript_message)
                    a.attributes {
                        attribute("href", "#mainNavi"); attribute(
                        "accesskey",
                        "1"
                    );
                    }.content {
                        text("1 Hauptmenü")
                    }
                    a.attributes {
                        attribute("href", "#mainContent"); attribute(
                        "accesskey",
                        "2"
                    );
                    }.content {
                        text("2 Inhalt")
                    }
                    a.attributes {
                        attribute("href", "#keypadDescription"); attribute(
                        "accesskey",
                        "3"
                    );
                    }.content {
                        text("3 Zurück zu dieser Anleitung")
                    }
                }

                val result = div.attributes {
                    attribute("id", "pageContainer")
                    attribute("class", "pageElementTop")
                }.content {

                    div.attributes {
                        attribute("class", "invAnchor")
                    }.content {
                        a.attributes {
                            attribute(
                                "name",
                                "top"
                            ); attribute("class", "invAnchor")
                        }
                    }

                    div.attributes {
                        attribute("id", "pageHead")
                        attribute("class", "pageElementTop")
                    }.content {

                        div.attributes {
                            attribute("id", "pageHeadTop")
                            attribute("class", "pageElementTop")
                        }.content {
                            a.attributes {
                                attribute(
                                    "href",
                                    "?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N$menuId,-Aimprint"
                                ); attribute(
                                "class",
                                "img img_arrowImprint pageElementLeft"
                            );
                            }.content {
                                text(
                                    localizer.imprint
                                )
                            }
                            a.attributes {
                                attribute(
                                    "href",
                                    "?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N$menuId,-Acontact"
                                ); attribute(
                                "class",
                                "img img_arrowContact pageElementLeft"
                            );
                            }.content {
                                text(
                                    localizer.contact
                                )
                            }
                            a.attributes {
                                attribute("href", "#"); attribute(
                                "onclick",
                                "window.print();"
                            ); attribute(
                                "class",
                                "img img_arrowPrint pageElementLeft"
                            );
                            }.content {
                                text(localizer.print)
                            }
                            a.attributes {
                                attribute("href", "#bottom"); attribute(
                                "class",
                                "img img_arrowDown pageElementRight"
                            );
                            }.content {
                                text(localizer.move_to_bottom)
                            }
                        }

                        div.attributes {
                            attribute("id", "pageHeadCenter")
                            attribute("class", "pageElementTop")
                        }.content {
                            div.attributes {
                                attribute("id", "pageHeadLeft")
                                attribute("class", "pageElementLeft")
                            }.content {
                                a.attributes {
                                    attribute(
                                        "href",
                                        "http://www.tu-darmstadt.de"
                                    ); attribute("title", "extern http://www.tu-darmstadt.de")
                                }.content {
                                    img.attributes {
                                        attribute("id", "imagePageHeadLeft"); attribute(
                                        "src",
                                        "/gfx/tuda/logo.gif"
                                    ); attribute("alt", "Logo Technische Universität Darmstadt")
                                    }
                                }
                            }
                            div.attributes {
                                attribute("id", "pageHeadRight"); attribute(
                                "class",
                                "pageElementRight"
                            )
                            }
                        }

                        div.attributes {
                            attribute("id", "pageHeadBottom_1")
                            attribute("class", "pageElementTop")
                        }.content {
                            div.attributes {
                                attribute("id", "pageHeadControlsLeft")
                                attribute("class", "pageElementLeft")
                            }.content {
                                a.attributes {
                                    attribute("class", "img pageHeadLink"); attribute(
                                    "href",
                                    "#"
                                ); attribute("id", "extraNav_link1"); attribute(
                                    "target",
                                    "_blank"
                                );
                                }.content {
                                    text("Homepage")
                                }
                                a.attributes {
                                    attribute("class", "img pageHeadLink"); attribute(
                                    "href",
                                    "#"
                                ); attribute("id", "extraNav_link2"); attribute(
                                    "target",
                                    "_blank"
                                );
                                }.content {
                                    text("standardLink undef")
                                }
                            }
                            div.attributes {
                                attribute("id", "pageHeadControlsRight")
                                attribute("class", "pageElementRight")
                            }.content {
                                a.attributes {
                                    attribute("class", "img"); attribute(
                                    "href",
                                    "#"
                                ); attribute("id", "extraNav_link3"); attribute(
                                    "target",
                                    "_blank"
                                );
                                }.content {
                                    text("standardLink undef")
                                }
                                a.attributes {
                                    attribute("class", "img"); attribute(
                                    "href",
                                    "#"
                                ); attribute("id", "extraNav_link4"); attribute(
                                    "target",
                                    "_blank"
                                );
                                }.content {
                                    text("standardLink undef")
                                }
                                a.attributes {
                                    attribute("class", "img"); attribute(
                                    "href",
                                    "#"
                                ); attribute("id", "extraNav_link5"); attribute(
                                    "target",
                                    "_blank"
                                )
                                }
                            }
                        }

                        div.attributes {
                            attribute("id", "pageHeadBottom_2"); attribute(
                            "class",
                            "pageElementTop"
                        )
                        }.content {
                            div.attributes {
                                attribute("id", "pageHeadBottom_2sub_1"); attribute(
                                "class",
                                "pageElementTop"
                            )
                            }
                            div.attributes {
                                attribute("id", "pageHeadBottom_2sub_2"); attribute(
                                "class",
                                "pageElementTop"
                            )
                            }
                        }

                        div.attributes {
                            attribute("id", "pageTopNavi"); attribute("class", "pageElementTop")
                        }.content {
                            a.attributes { attribute("name", "mainNavi"); attribute("class", "hidden"); }
                            ul.attributes {
                                attribute("class", "nav depth_1 linkItemContainer")
                            }.content {

                                if (peek()?.attr("class")?.trim() == "intern depth_1 linkItem") {
                                    parseLoggedOutNavigation(menuLocalizer, localizer, sessionId)
                                } else {
                                    parseLoggedInNavigation(menuLocalizer, localizer, sessionId)
                                }
                            }
                        }

                        div.attributes {
                            attribute("id", "pageHeadBottom_3"); attribute(
                            "class",
                            "pageElementTop"
                        )
                        }.content {
                            div.attributes {
                                attribute("id", "pageHeadSwitchLang"); attribute(
                                "class",
                                "pageElementRight"
                            )
                            }.content {
                                a.attributes {
                                    attribute(
                                        "href",
                                        "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=CHANGELANGUAGE&ARGUMENTS=-N${if (sessionId == "000000000000001") "000000000000002" else sessionId},-N${localizer.other_language_id}"
                                    ); attribute(
                                    "class",
                                    "img ${localizer.other_language_css} pageElementLeft"
                                ); attribute(
                                    "title",
                                    localizer.other_language
                                );
                                }.content {
                                    text(localizer.other_language)
                                }

                                if (sessionId != "000000000000001") {
                                    a.attributes {
                                        attribute(
                                            "href",
                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=LOGOUT&ARGUMENTS=-N$sessionId,-N001"
                                        )
                                        attribute("id", "logoutButton")
                                        attribute("class", "img img_arrowLogout logout")
                                        attribute("title", localizer.logout)
                                    }.content {
                                        text(localizer.logout)
                                    }
                                }
                            }

                            if (sessionId == "000000000000001") {
                                div.attributes {
                                    attribute("id", "cn_loginForm")
                                }.content {
                                    div.content {
                                        a.attributes {
                                            attribute("id", "logIn_btn")
                                            attribute("class", "img img_arrowSubmit")
                                            attribute("title", "Anmelden")
                                            attribute(
                                                "href",
                                                "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/authorize?client_id=ClassicWeb&amp;scope=openid%20DSF%20email&amp;response_mode=query&amp;response_type=code&amp;ui_locales=de&amp;redirect_uri=https%3a%2f%2fwww.tucan.tu-darmstadt.de%2Fscripts%2Fmgrqispi.dll%3FAPPNAME%3DCampusNet%26PRGNAME%3DLOGINCHECK%26ARGUMENTS%3D-N000000000000001%2Cids_mode%26ids_mode%3DY"
                                            )
                                        }.content {
                                            text("Anmelden")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val result = div.attributes {
                        attribute("id", "pageContentContainer"); attribute(
                        "class",
                        "pageElementTop"
                    )
                    }.content {
                        div.attributes {
                            attribute("id", "pageLeft"); attribute(
                            "class",
                            "pageElementLeft"
                        );
                        }.content {
                            div.attributes { attribute("id", "pageLeftTop") }
                        }
                        val result = div.attributes {
                            attribute("id", "pageContent"); attribute(
                            "class",
                            "pageElementLeft"
                        )
                        }.content {
                            div.attributes { attribute("id", "featureBanner") }
                            a.attributes {
                                attribute("name", "mainContent"); attribute(
                                "class",
                                "hidden"
                            )
                            }
                            div.attributes {
                                attribute("id", "pageContentTop"); attribute(
                                "class",
                                "pageElementTop"
                            )
                            }.content {
                                if (sessionId != "000000000000001") {
                                    div.attributes {
                                        attribute("id", "loginData")
                                    }.content {
                                        span.attributes {
                                            attribute("class", "loginDataLoggedAs")
                                        }.content {
                                            b.content {
                                                text(localizer.youre_logged_in_as)
                                                span.attributes { attribute("class", "colon"); }.content { text(":") }
                                            }
                                        }
                                        span.attributes {
                                            attribute("class", "loginDataName")
                                            attribute("id", "loginDataName")
                                        }.content {
                                            b.content {
                                                text("Name")
                                                span.attributes { attribute("class", "colon"); }.content { text(":") }
                                            }
                                            extractText()
                                        }
                                        span.attributes {
                                            attribute("class", "loginDataDate")
                                        }.content {
                                            b.content {
                                                text(localizer.on)
                                                span.attributes {
                                                    attribute("class", "colon")
                                                }.content {
                                                    text(":")
                                                }
                                            }
                                            extractText()
                                        }
                                        span.attributes {
                                            attribute("class", "loginDataTime")
                                        }.content {
                                            b.content {
                                                text(localizer.at)
                                                span.attributes {
                                                    attribute("class", "colon time_colon")
                                                }.content {
                                                    text(":")
                                                }
                                            }
                                            extractText()
                                        }
                                    }
                                }
                            }
                            div.attributes {
                                attribute("id", "contentSpacer_IE"); attribute(
                                "class",
                                "pageElementTop"
                            )
                            }.content {
                                inner(localizer, pageType)
                            }
                        }
                        result
                    }

                    div.attributes {
                        attribute("id", "pageFoot"); attribute("class", "pageElementTop")
                    }.content {
                        div.attributes {
                            attribute("id", "pageFootControls"); attribute(
                            "class",
                            "pageElementTop"
                        )
                        }.content {
                            div.attributes {
                                attribute("id", "pageFootControlsLeft")
                            }.content {
                                a.attributes {
                                    attribute(
                                        "href",
                                        "?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N$menuId,-Aimprint"
                                    ); attribute(
                                    "class",
                                    "img img_arrowImprint pageElementLeft"
                                ); attribute("id", "pageFootControl_imp");
                                }.content {
                                    text(localizer.imprint)
                                }
                                a.attributes {
                                    attribute(
                                        "href",
                                        "?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N$menuId,-Acontact"
                                    ); attribute(
                                    "class",
                                    "img img_arrowContact pageElementLeft"
                                ); attribute("id", "pageFootControl_con");
                                }.content {
                                    text(localizer.contact)
                                }
                                a.attributes {
                                    attribute("href", "#"); attribute(
                                    "onclick",
                                    "window.print();"
                                ); attribute(
                                    "class",
                                    "img img_arrowPrint pageElementLeft"
                                ); attribute("id", "pageFootControl_pri");
                                }.content {
                                    text(localizer.print)
                                }
                            }
                            div.attributes {
                                attribute(
                                    "id",
                                    "pageFootControlsRight"
                                )
                            }.content {
                                a.attributes {
                                    attribute("href", "#top")
                                    attribute(
                                        "class",
                                        "img img_arrowUp pageElementRight"
                                    )
                                    attribute("id", "pageFootControl_up")
                                }
                            }
                        }
                    }
                    result
                }

                div.attributes { attribute("id", "IEdiv"); }
                div.attributes {
                    attribute("class", "invAnchor")
                }.content {
                    a.attributes {
                        attribute(
                            "name",
                            "bottom"
                        ); attribute("class", "invAnchor")
                    }
                }
                result
            }
        }
    }

    fun BodyContentScope.parseLoggedInNavigation(menuLocalizer: Localizer, contentLocalizer: Localizer, sessionId: String) {
        parseLiWithChildren(
            menuLocalizer.my_tucan.text,
            "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=MLSSTART&ARGUMENTS=-N$sessionId,-N${menuLocalizer.my_tucan.id6()},",
            menuLocalizer.my_tucan.id
        ) {
            parseLiHref(
                menuLocalizer.messages.text,
                menuLocalizer.messages.id
            )
        }

        parseLiWithChildrenHref(
            menuLocalizer.vorlesungsverzeichnis.text,
            menuLocalizer.vorlesungsverzeichnis.id
        ) {
            parseVV(
                menuLocalizer,
                sessionId,
                menuLocalizer.course_search.id,
                menuLocalizer.room_search.id,
                menuLocalizer.archive.id
            )
        }

        parseLiWithChildren(
            menuLocalizer.schedule.text,
            "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=SCHEDULER&ARGUMENTS=-N$sessionId,-N${menuLocalizer.schedule.id6()},-A,-A,-N1",
            menuLocalizer.schedule.id
        ) {
            parseLi(menuLocalizer.schedule_day) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=SCHEDULER&ARGUMENTS=-N$sessionId,-N$id6,-A,-A,-N0" }
            parseLi(menuLocalizer.schedule_week) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=SCHEDULER&ARGUMENTS=-N$sessionId,-N$id6,-A,-A,-N1" }
            parseLi(menuLocalizer.schedule_month) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=MONTH&ARGUMENTS=-N$sessionId,-N$id6,-A" }
            parseLi(menuLocalizer.schedule_export) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=SCHEDULER_EXPORT&ARGUMENTS=-N$sessionId,-N$id6," }
        }

        parseLiWithChildren(
            menuLocalizer.courses.text,
            "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N${menuLocalizer.courses.id6()},-A${menuLocalizer.courses_html}",
            menuLocalizer.courses.id
        ) {
            parseLi(menuLocalizer.my_modules) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=MYMODULES&ARGUMENTS=-N$sessionId,-N$id6," }
            parseLi(menuLocalizer.my_courses) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=PROFCOURSES&ARGUMENTS=-N$sessionId,-N$id6," }
            parseLi(menuLocalizer.my_elective_subjects) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=STUDENTCHOICECOURSES&ARGUMENTS=-N$sessionId,-N$id6," }
            parseLi(menuLocalizer.registration) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=REGISTRATION&ARGUMENTS=-N$sessionId,-N$id6,-A" }
            parseLi(menuLocalizer.my_current_registrations) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=MYREGISTRATIONS&ARGUMENTS=-N$sessionId,-N$id6,-N000000000000000" }
        }

        parseLiWithChildren(
            menuLocalizer.examinations.text,
            "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N${menuLocalizer.examinations.id6()},-A${menuLocalizer.examinations_html}",
            menuLocalizer.examinations.id
        ) {
            parseLi(menuLocalizer.my_examinations) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=MYEXAMS&ARGUMENTS=-N$sessionId,-N$id6," }
            parseLiWithChildren(
                menuLocalizer.my_examination_schedule.text,
                "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=SCPCHOICE&ARGUMENTS=-N$sessionId,-N${menuLocalizer.my_examination_schedule.id6()},",
                menuLocalizer.my_examination_schedule.id,
                depth = 2
            ) {
                parseLi(
                    menuLocalizer.my_examination_schedule_important_notes,
                    depth = 3
                ) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N$id6,-A${menuLocalizer.my_examination_schedule_important_notes_html}" }
            }
            parseLiWithChildren(
                menuLocalizer.semester_results.text,
                "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N${menuLocalizer.semester_results.id6()},-A${menuLocalizer.semester_results_html}",
                menuLocalizer.semester_results.id,
                depth = 2,
            ) {
                parseLi(
                    menuLocalizer.module_results,
                    depth = 3
                ) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=COURSERESULTS&ARGUMENTS=-N$sessionId,-N$id6," }
                parseLi(
                    menuLocalizer.examination_results,
                    depth = 3
                ) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXAMRESULTS&ARGUMENTS=-N$sessionId,-N$id6," }
            }
            parseLi(menuLocalizer.performance_record) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=STUDENT_RESULT&ARGUMENTS=-N$sessionId,-N$id6,-N0,-N000000000000000,-N000000000000000,-N000000000000000,-N0,-N000000000000000" }
        }

        parseLiWithChildren(
            menuLocalizer.service.text,
            "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N${menuLocalizer.service.id6()},-A${menuLocalizer.service_html}",
            menuLocalizer.service.id
        ) {
            parseLi(menuLocalizer.personal_data) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=PERSADDRESS&ARGUMENTS=-N$sessionId,-N$id6,-A" }
            parseLi(menuLocalizer.my_documents) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=CREATEDOCUMENT&ARGUMENTS=-N$sessionId,-N$id6," }
            parseLiHref(menuLocalizer.forms.text, menuLocalizer.forms.id)
            parseLi(menuLocalizer.hold_info) { id6 -> "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=HOLDINFO&ARGUMENTS=-N$sessionId,-N$id6," }
        }

        parseLiWithChildren(
            menuLocalizer.application.text,
            "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N${menuLocalizer.application.id6()},-A${menuLocalizer.application_html}",
            menuLocalizer.application.id
        ) {
            parseLi(menuLocalizer.application_welcome) { id6 ->
                "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N$id6,-A${menuLocalizer.application_html}"
            }
            parseLiHref(
                menuLocalizer.my_application.text,
                menuLocalizer.my_application.id
            )
            parseLi(menuLocalizer.application_my_documents) { id6 ->
                "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=CREATEDOCUMENT&ARGUMENTS=-N$sessionId,-N$id6,"
            }
        }

        parseLi(
            menuLocalizer.help,
            depth = 1
        ) { id6 ->
            "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N$id6,-A${menuLocalizer.help_html}"
        }
    }

    fun BodyContentScope.parseLoggedOutNavigation(menuLocalizer: Localizer, contentLocalizer: Localizer, sessionId: String) {
        li.attributes {
            attribute("class", "intern depth_1 linkItem")
            attribute("title", "Startseite")
            attribute("id", "link000344")
        }.content {
            a.attributes {
                attribute("class", "depth_1 link000344 navLink")
                attribute(
                    "href",
                    "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N000344,-Awelcome"
                )
            }.content {
                text("Startseite")
            }
        }

        li.attributes {
            attribute("class", "tree depth_1 linkItem branchLinkItem")
            attribute("title", "Vorlesungsverzeichnis (VV)")
            attribute("id", "link000334")
        }.content {
            a.attributes {
                attribute(
                    "class",
                    "depth_1 link000334 navLink branchLink"
                )
                // URL regularly changes
                val vvUrl = attributeValue(
                    "href",
                )
            }.content {
                text("Vorlesungsverzeichnis (VV)")
            }

            ul.attributes {
                attribute("class", "nav depth_2 linkItemContainer")
            }.content {

                parseVV(menuLocalizer, sessionId, 335, 385, 463)
            }
        }

        li.attributes {
            attribute("class", "tree depth_1 linkItem branchLinkItem")
            attribute("title", "TUCaN-Account")
            attribute("id", "link000410")
        }.content {
            a.attributes {
                attribute(
                    "class",
                    "depth_1 link000410 navLink branchLink"
                )
                attribute(
                    "href",
                    "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N000410,-Atucan%5Faccount%2Ehtml"
                )
            }.content {
                text("TUCaN-Account")
            }

            ul.attributes {
                attribute("class", "nav depth_2 linkItemContainer")
            }.content {

                parseLi(
                    "Account anlegen",
                    "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=CREATEACCOUNT&ARGUMENTS=-N$sessionId,-N000425,",
                    425
                )

                parseLi(
                    "Passwort vergessen (nur für Bewerber/innen!)",
                    "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=LOSTPASS&ARGUMENTS=-N$sessionId,-N000426,-A",
                    426
                )
            }
        }

        parseLi(
            "Hilfe",
            "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N000340,-Ahilfe%2Ehtml",
            340,
            1
        )
    }

    private fun BodyContentScope.parseVV(
        localizer: Localizer,
        sessionId: String,
        course_search_id: Int,
        room_search_id: Int,
        archive_id: Int
    ) {
        parseLiHref(
            localizer.course_search.text,
            course_search_id
        )

        parseLi(
            localizer.room_search.text,
            "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=SEARCHROOM&ARGUMENTS=-N$sessionId,-N000$room_search_id,",
            room_search_id
        )
        while (peek()?.attr("class")
                ?.trim() == "intern depth_2 linkItem"
        ) {
            li.attributes {
                attribute("class", "intern depth_2 linkItem")
                attributeValue("title")
                attributeValue("id")
            }.content {
                a.attributes {
                    attributeValue("class")
                    attributeValue("href")
                }.content {
                    extractText()
                }
            }
        }

        li.attributes {
            attribute(
                "class",
                "tree depth_2 linkItem branchLinkItem"
            )
            attribute("title", localizer.archive.text)
            attribute("id", "link000$archive_id")
        }.content {
            a.attributes {
                attribute(
                    "class",
                    "depth_2 link000$archive_id navLink branchLink"
                )
                attribute(
                    "href",
                    "/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N$sessionId,-N000$archive_id,-Avvarchivstart%2Ehtml"
                )
            }.content {
                text(localizer.archive.text)
            }

            ul.attributes {
                attribute(
                    "class",
                    "nav depth_3 linkItemContainer"
                )
            }.content {

                while (peek() != null) {
                    li.attributes {
                        attribute(
                            "class",
                            "intern depth_3 linkItem"
                        )
                        attributeValue("title")
                        attributeValue("id")
                    }.content {
                        a.attributes {
                            attributeValue(
                                "class",
                            )
                            attributeValue(
                                "href",
                            )
                        }.content {
                            extractText()
                        }
                    }
                }
            }
        }
    }

    private fun BodyContentScope.parseLi(name: String, url: String, id: Int, depth: Int = 2) {
        val link = "link${id.toString().padStart(6, '0')}"
        return li.attributes {
            attribute("class", "intern depth_$depth linkItem")
            attribute("title", name)
            attribute("id", link)
        }.content {
            a.attributes {
                attribute("class", "depth_$depth $link navLink")
                attribute("href", url)
            }.content {
                text(name)
            }
        }
    }

    private fun BodyContentScope.parseLi(name: TextAndId, depth: Int = 2, url: (id6: String) -> String) {
        parseLi(name.text, url(name.id6()), name.id, depth)
    }

    private fun BodyContentScope.parseLiHref(name: String, id: Int, depth: Int = 2): String {
        val link = "link${id.toString().padStart(6, '0')}"
        return li.attributes {
            attribute("class", "intern depth_$depth linkItem")
            attribute("title", name)
            attribute("id", link)
        }.content {
            val href: String
            a.attributes {
                attribute("class", "depth_$depth $link navLink")
                 href = attributeValue(
                    "href"
                )
            }.content {
                text(name)
                href
            }
        }
    }

    private fun BodyContentScope.parseLiWithChildrenHref(
        name: String,
        id: Int,
        depth: Int = 1,
        init: BodyContentScope.() -> Unit
    ): String {
        val link = "link${id.toString().padStart(6, '0')}"
        return li.attributes {
            attribute("class", "tree depth_$depth linkItem branchLinkItem")
            attribute("title", name)
            attribute("id", link)
        }.content {
            val href: String
            a.attributes {
                attribute("class", "depth_$depth $link navLink branchLink")
                href = attributeValue(
                    "href",
                )
            }.content {
                text(name)
            }
            ul.attributes {
                attribute("class", "nav depth_${depth + 1} linkItemContainer")
            }.content {
                init()
            }
            href
        }
    }


    private fun BodyContentScope.parseLiWithChildren(
        name: String,
        url: String,
        id: Int,
        depth: Int = 1,
        init: BodyContentScope.() -> Unit
    ) {
        val link = "link${id.toString().padStart(6, '0')}"
        li.attributes {
            attribute("class", "tree depth_$depth linkItem branchLinkItem")
            attribute("title", name)
            attribute("id", link)
        }.content {
            a.attributes {
                attribute("class", "depth_$depth $link navLink branchLink")
                attribute(
                    "href",
                    url
                )
            }.content {
                text(name)
            }
            ul.attributes {
                attribute("class", "nav depth_${depth + 1} linkItemContainer")
            }.content {
                init()
            }
        }
    }

    fun currentSemester(): Int {
        val test = Clock.System.now()
        val dateTime = test.toLocalDateTime(TimeZone.UTC)
        val year = dateTime.year
        val month = dateTime.month
        val offset = if (Month.APRIL <= month && month <= Month.OCTOBER) {
            // sose
            0
        } else {
            1
        }
        // 15186000
        // wise 2025
        return 15176000 + ((year - 2025) * 2 + offset) * 10000
    }

    fun Response.parseCommonHeaders() {
        status(HttpStatusCode.OK)
        header(
            "content-security-policy",
            "frame-src https://dsf.tucan.tu-darmstadt.de; frame-ancestors 'self' https://dsf.tucan.tu-darmstadt.de;"
        )
        header("content-type", "text/html")
        header("x-content-type-options", "nosniff")
        header("x-xss-protection", "1; mode=block")
        header("referrer-policy", "strict-origin")
        header("x-frame-options", "SAMEORIGIN")
        maybeHeader("x-powered-by", listOf("ASP.NET"))
        header("server", "Microsoft-IIS/10.0")
        header("strict-transport-security", "max-age=31536000; includeSubDomains")
        ignoreHeader("mgxpamiddlewarewaittime") // 0 or 16
        ignoreHeader("date")
        ignoreHeader("dl-served-by")
        maybeHeader("connection", listOf("close"))
        header("pragma", "no-cache")
        header("expires", "0")
        header("cache-control", "private, no-cache, no-store")
        maybeIgnoreHeader("x-firefox-spdy")
        maybeIgnoreHeader("vary")
        maybeIgnoreHeader("x-android-received-millis")
        maybeIgnoreHeader("x-android-received-millis")
        maybeIgnoreHeader("x-android-response-source")
        maybeIgnoreHeader("x-android-selected-protocol")
        maybeIgnoreHeader("x-android-sent-millis")
        maybeIgnoreHeader("content-length")
    }
}