package de.selfmade4u.tucanpluskmp

object Test {
    @HtmlFromResources("html")
    fun RootContentScope.someFun(sessionId: String, menuId: String) {
        val abc = 42;
        var sessionIdTmp = sessionId
        var menuIdTmp = menuId
        html.attributes {
            attribute("xmlns", "http://www.w3.org/1999/xhtml")
            attribute("xml:lang", "de")
            attribute("lang", "de")
        }.content {
            head.content {
                title.content {
                    extractText()
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
                    attribute("content", "Datenlotsen,Datenlotsen Informationssysteme GmbH,CampusNet,Campus Management")
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
                    extractText()
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
                style.attributes {
                    attribute("type", "text/css")
                }.content {
                    extractText()
                }
            }
            body.attributes {
                attribute("class", "registration_auditor")
            }.content {
                div.attributes {
                    attribute("id", "Cn-system-desc")
                }
                script.attributes {
                    attribute("type", "text/javascript")
                }.content {
                    extractText()
                }
                div.attributes {
                    attribute("id", "acc_pageDescription")
                    attribute("class", "hidden")
                }.content {
                    a.attributes {
                        attribute("name", "keypadDescription")
                        attribute("class", "hidden")
                    }.content {
                        extractText()
                    }
                    extractText()
                    a.attributes {
                        attribute("href", "#mainNavi")
                        attribute("accesskey", "1")
                    }.content {
                        extractText()
                    }
                    a.attributes {
                        attribute("href", "#mainContent")
                        attribute("accesskey", "2")
                    }.content {
                        extractText()
                    }
                    a.attributes {
                        attribute("href", "#keypadDescription")
                        attribute("accesskey", "3")
                    }.content {
                        extractText()
                    }
                }
                div.attributes {
                    attribute("id", "pageContainer")
                    attribute("class", "pageElementTop")
                }.content {
                    div.attributes {
                        attribute("class", "invAnchor")
                    }.content {
                        a.attributes {
                            attribute("name", "top")
                            attribute("class", "invAnchor")
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
                                    "?APPNAME=CampusNet&amp;PRGNAME=EXTERNALPAGES&amp;ARGUMENTS=-N000000000000001,-N000334,-Aimprint"
                                )
                                attribute("class", "img img_arrowImprint pageElementLeft")
                            }.content {
                                extractText()
                            }
                            a.attributes {
                                attribute(
                                    "href",
                                    "?APPNAME=CampusNet&amp;PRGNAME=EXTERNALPAGES&amp;ARGUMENTS=-N000000000000001,-N000334,-Acontact"
                                )
                                attribute("class", "img img_arrowContact pageElementLeft")
                            }.content {
                                extractText()
                            }
                            a.attributes {
                                attribute("href", "#")
                                attribute("onclick", "window.print();")
                                attribute("class", "img img_arrowPrint pageElementLeft")
                            }.content {
                                extractText()
                            }
                            a.attributes {
                                attribute("href", "#bottom")
                                attribute("class", "img img_arrowDown pageElementRight")
                            }.content {
                                extractText()
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
                                    attribute("href", "http://www.tu-darmstadt.de")
                                    attribute("title", "extern http://www.tu-darmstadt.de")
                                }.content {
                                    img.attributes {
                                        attribute("id", "imagePageHeadLeft")
                                        attribute("src", "/gfx/tuda/logo.gif")
                                        attribute("alt", "Logo Technische Universität Darmstadt")
                                    }
                                }
                            }
                            div.attributes {
                                attribute("id", "pageHeadRight")
                                attribute("class", "pageElementRight")
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
                                    attribute("class", "img pageHeadLink")
                                    attribute("href", "#")
                                    attribute("id", "extraNav_link1")
                                    attribute("target", "_blank")
                                }.content {
                                    extractText()
                                }
                                a.attributes {
                                    attribute("class", "img pageHeadLink")
                                    attribute("href", "#")
                                    attribute("id", "extraNav_link2")
                                    attribute("target", "_blank")
                                }.content {
                                    extractText()
                                }
                            }
                            div.attributes {
                                attribute("id", "pageHeadControlsRight")
                                attribute("class", "pageElementRight")
                            }.content {
                                a.attributes {
                                    attribute("class", "img")
                                    attribute("href", "#")
                                    attribute("id", "extraNav_link3")
                                    attribute("target", "_blank")
                                }.content {
                                    extractText()
                                }
                                a.attributes {
                                    attribute("class", "img")
                                    attribute("href", "#")
                                    attribute("id", "extraNav_link4")
                                    attribute("target", "_blank")
                                }.content {
                                    extractText()
                                }
                                a.attributes {
                                    attribute("class", "img")
                                    attribute("href", "#")
                                    attribute("id", "extraNav_link5")
                                    attribute("target", "_blank")
                                }
                            }
                        }
                        div.attributes {
                            attribute("id", "pageHeadBottom_2")
                            attribute("class", "pageElementTop")
                        }.content {
                            div.attributes {
                                attribute("id", "pageHeadBottom_2sub_1")
                                attribute("class", "pageElementTop")
                            }
                            div.attributes {
                                attribute("id", "pageHeadBottom_2sub_2")
                                attribute("class", "pageElementTop")
                            }
                        }
                        div.attributes {
                            attribute("id", "pageTopNavi")
                            attribute("class", "pageElementTop")
                        }.content {
                            a.attributes {
                                attribute("name", "mainNavi")
                                attribute("class", "hidden")
                            }
                            ul.attributes {
                                attribute("class", "nav depth_1 linkItemContainer")
                            }.content {
                                li.attributes {
                                    attribute("class", "intern depth_1 linkItem ")
                                    attribute("title", "Startseite")
                                    attribute("id", "link000344")
                                }.content {
                                    a.attributes {
                                        attribute("class", "depth_1 link000344 navLink ")
                                        attribute(
                                            "href",
                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=EXTERNALPAGES&amp;ARGUMENTS=-N000000000000001,-N000344,-Awelcome"
                                        )
                                    }.content {
                                        extractText()
                                    }
                                }
                                li.attributes {
                                    attribute("class", "tree depth_1 linkItem branchLinkItem ")
                                    attribute("title", "Vorlesungsverzeichnis (VV)")
                                    attribute("id", "link000334")
                                }.content {
                                    a.attributes {
                                        attribute("class", "depth_1 link000334 navLink branchLink ")
                                        attribute(
                                            "href",
                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AJhtseh3aPfEXfNXoz4rVW244HcAuscvUsg1Cy2-Gg4GfhKeFNRp~397k5A2lHFuuBAve-SmnJQFaKCKxinP2sWrKSVhIUoN0xj8D6zY4sKpWVkzh912h8FGdD5ytSjR0OK9t4EZ-5Fqt9slQLzvfuuJVK6D54MGvnzbdf4GQ8prgK1w2Ooa8~F2IZQ__"
                                        )
                                    }.content {
                                        extractText()
                                    }
                                    ul.attributes {
                                        attribute("class", "nav depth_2 linkItemContainer")
                                    }.content {
                                        li.attributes {
                                            attribute("class", "intern depth_2 linkItem ")
                                            attribute("title", "Lehrveranstaltungssuche")
                                            attribute("id", "link000335")
                                        }.content {
                                            a.attributes {
                                                attribute("class", "depth_2 link000335 navLink ")
                                                attribute(
                                                    "href",
                                                    "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-A5ZG06Fyu4xaOocGQ9tboEKYxT9oWlCCF8jB-CmV9EDBZGrEkdCnWcP82yWmJaWtmbWIK-cea0jUoUhmYinsEFuAugbV1rzkr9pUUZWrY9-uaWjYkHnqH1HllLKRUlcm81rH-6Gzj9dlpFWcKiDQi5flV1O~e"
                                                )
                                            }.content {
                                                extractText()
                                            }
                                        }
                                        li.attributes {
                                            attribute("class", "intern depth_2 linkItem ")
                                            attribute("title", "Raumsuche")
                                            attribute("id", "link000385")
                                        }.content {
                                            a.attributes {
                                                attribute("class", "depth_2 link000385 navLink ")
                                                attribute(
                                                    "href",
                                                    "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=SEARCHROOM&amp;ARGUMENTS=-N000000000000001,-N000385,"
                                                )
                                            }.content {
                                                extractText()
                                            }
                                        }
                                        li.attributes {
                                            attribute("class", "intern depth_2 linkItem ")
                                            attribute("title", "Aktuell - Sommersemester 2026")
                                            attribute("id", "link000721")
                                        }.content {
                                            a.attributes {
                                                attribute("class", "depth_2 link000721 navLink ")
                                                attribute(
                                                    "href",
                                                    "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-A-DtOtgj89xB5tuVgv-noz9eJXdGws4S-87-UiJBXIHQ4zV~6eSrCXI1GGJNQ8fVKXvr-zzSSbONe7Jq98jMwgbJmERP4wCr4cf~LpvibO9He6vMyxmn1UwfIcnHjlOkBJ8SBIcTR4ZAxMW64rKFhhlmyEOwabH26JNoyuBBU~0aWL9yaSG9y9~AxDw__"
                                                )
                                            }.content {
                                                extractText()
                                            }
                                        }
                                        li.attributes {
                                            attribute("class", "intern depth_2 linkItem ")
                                            attribute("title", "Vorlesungsverzeichnis des WiSe 2025/26")
                                            attribute("id", "link000713")
                                        }.content {
                                            a.attributes {
                                                attribute("class", "depth_2 link000713 navLink ")
                                                attribute(
                                                    "href",
                                                    "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AlEyK37ks7vGirjq8YNiRbczb1jO0jXGSzJxErBEC~YF1aae541IvGJ9NMilbqU3UVVOuBfXmIgY0ckN08oY5pmCUpDiDXoG1mZ7cv65A0CyWddELSTwcLU4ApEtN93vCKBS0DuV93CYrd-PV8SgReM1Hn0Xa~G174xBnbUmA3XovY2NhCmVoA9nqPQ__"
                                                )
                                            }.content {
                                                extractText()
                                            }
                                        }
                                        li.attributes {
                                            attribute("class", "tree depth_2 linkItem branchLinkItem ")
                                            attribute("title", "Archiv")
                                            attribute("id", "link000463")
                                        }.content {
                                            a.attributes {
                                                attribute("class", "depth_2 link000463 navLink branchLink ")
                                                attribute(
                                                    "href",
                                                    "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=EXTERNALPAGES&amp;ARGUMENTS=-N000000000000001,-N000463,-Avvarchivstart%2Ehtml"
                                                )
                                            }.content {
                                                extractText()
                                            }
                                            ul.attributes {
                                                attribute("class", "nav depth_3 linkItemContainer")
                                            }.content {
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2025")
                                                    attribute("id", "link000724")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000724 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AvtJE~MHzx4472z7CvxJkvuHP-N8L1hvCTiMDlbYuG2atsp4iurn6U181zefczZNcATBffwlJb9CEdFT78NO8fxmUzcRRaNbzxu~hbjQKtj~1sWIGVS1tmX-ffdu~JYAvNWE88fnECosyxVPUpVNQ18UiU2ukVEwD0kp7Q0l7DkpukPiMtRnrM9gkHA__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2024/25")
                                                    attribute("id", "link000723")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000723 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-A02t-dvgUyPxgDGIrVHHsigv5oyVQ9GSazbWmUGa3Smi7GGOlIEtQUE~ijfivTZTsO4HNMc327ewaDB3rbIzTfM0aSqcgpMYjn~m0mPUFgi7IWGnnxBfxK8qGO28IA4glHhfJnwziDu8NHDwgAHFACIjRnwL79Udgl0vceuXAoni~j2wPZKQw3prUHA__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2024")
                                                    attribute("id", "link000714")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000714 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AQhfZW8qUeDsEfaTcXhYoSKxD0M2MHGKWLPiwPzwoSwSXdch5oyrw1CTihT6d0777bgVm2Q2AC8FkdHgddym87v-4~x6DFyBZLMl9Hua3vMQxrNhBl422FpD2jG2wWJZyaCakkq0ZTjDEi~rJYM-L7orfba~AbbML4KIEKd4bIinCbtN9Yiocu~f-6g__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2023/24")
                                                    attribute("id", "link000705")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000705 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AuEr3EJGqp0sTWEqx2O4QVOeJRZELFYGh7WIXPh3Cej2myIL7zswtka5MZWbgbO5DuxO9TmC-Wdoma-kO9YFd9A1VTDzfZdoJYSGZMDhEyDJQnNNRsCg-qekfAVZcvjyLAP4~RMpg5QKpnmrIi88nv6OdgJ1PMk1~dpcb43hE2PpFFAKrNdU7YuE5eA__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2023")
                                                    attribute("id", "link000697")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000697 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-Atg2b4iaPgVb2ERLfCzePegMU5H51kj2lXfs9TMpMOWCEgQTtQnKFqyMHpYAIrxUY71PkGkv1QZaCwtkJv6roeStBiH-wiG1qLjDXKvoc4sckof8kbdshKWTHOW4ESa83slWVr~ozPekDcUtbyznGsIijsXE8aYnTPhBvQTjCAZMtMt0m5XnnZKlC0w__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2022/23")
                                                    attribute("id", "link000689")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000689 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-ABymNpmLzF2VqAZJxHdb3wj-opU8nRau1w49mYmcrigWPQMGEBuYncnnlm~OZcZR~r7qkvBOCZXy3YnEwPCevAfw2HbQFPo~t9Pt1tYNkyEZ4~2xS7ukrfDKvvLjDOhtKJZEMh6sDCadxW1WU0rc91k3ofarPO2b6fuRa1ZFh2LOld0M~ySm~RpgitA__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2022")
                                                    attribute("id", "link000682")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000682 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AkugrgjaX31G7cWWuddgoe~q-pfY5oR1z~hs5BNk93UZPk-Tvj8kGOGU6m2Ni2cgJ4QlxYz2QNtDnEue2ctPpZd9SXCWRBgfHrXjnAWKCKDWq3B9oRn26n3t1qiGi7GIbEheZmrc5WYeNeuVoTRJGN9wb7G8VJdLQgAaV1-SEi7CENGDMK3XG1Yh3Zg__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2021/22")
                                                    attribute("id", "link000670")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000670 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-ACTI4cCCBZY0~DB5rV0gyfS5zTWAVhLx9Ao5-aHCw-WE20l9gARGrLRNgkJAo56NluAySSpGa2T~Ox5mM0bIsbgN26DVC~khi-QFVb~x3dHro29JPYX56Xb7NyUlOCoWIVoxkrYeIrXJwYeVMWHGlfuXhW-hrpufj-qftLtFAS7W-M0Y-IdmcPCJ8Qw__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2021")
                                                    attribute("id", "link000663")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000663 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AouYRMGYn~8CSo9AAV~6wptj4WaqUuJ8iYSHjr3usGIHp-paxbAkW7EDAPJ5~KIcsAzx4hbDomJ~OI3mdRGdxwTi7ePy8pInW3OpDZRqCUYkbw4S7Dhy5iKSGsDjZGZ86KHBLg9X60ewoottLOUlfaXlubUg4usMV1nqr5Dl6qOr6L0Ipbcdj~1xm9A__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2020/21")
                                                    attribute("id", "link000655")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000655 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-A54eRg9SweMXSlmk1vdqr33Bz5kBqPhGm2sED92AETBYg0zJkm3GljmZlu2ZY5sNFHgamhguuyb76bxJt8Xggt611M1pmrFx-4ZgOP7nFpMbjkigOSnfeWTDAjzOSfn4tC~6HwTbqKPVPlABwKoqJGRwdYY5iQYdvEs24S47RanW6vHEZYuoVCXhxJA__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2020")
                                                    attribute("id", "link000635")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000635 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-A5-OjFSYuiK9k10CBzQkMHUP8XeRPPed0cBMCWPqPwfe15afpVcJ9vnfqFETImNMpbg9KIWUdddAbKk1vD7bLKL2XTws-PIl2yJErdOxLOoWE3Iq2bl7PgqhJ5vqCtSnQsLpB4l8PIRQnNpA-GFVU0A~bpn0nS3z7Dw~QRz6JFuWgOEetxuwqgqCx3g__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2019/20")
                                                    attribute("id", "link000627")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000627 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AdB3Ii7rjR002S1GBTCiun36Dse-Q5yhTemxfjLwtP6azP1xzOhsNZluTI~~xA7M8XOVjK94Se6HDt6p7RXD1D06IVAMczi1VCofFtHI0Udon4HFr5gK35a6ljBjN3WCd6QwUXwVfBraTFfYlElpP0RNoUd4izVq9duW5Wzw10cEC2sITpnIqEN1vRw__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2019")
                                                    attribute("id", "link000622")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000622 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-ApRU00GZyU-gzF9DxVVnaSs3d6376N4okmqEqvD~rSxaZg4Q6q66TfoXpJzNEpEwAnso8B0poGKn3kduInojJQLMhIgaxV1QtPAM8tTnoR4MMLI~gcje96DJfGHBRFYw0DAz92tub7Wa3aKWvZJW-0LQ5d9v4JSnu815jQvt9jeINabQXFglXaF29tQ__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2018/19")
                                                    attribute("id", "link000614")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000614 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AeHEMrl~wWiiNaZsykHkXQqcTz5X7j8Gq6pQIBkiWwBqoId6xJGVoL4suxWoqp2-jOWAoXfoNCSmeOCTHidrWk2DcR7MZozHKvlLdIGvgR23hXL487R9jAcrdDr67M6rQoDNY6iOPTkMR3DfPtPICVxS37a1SHdpvxUQFtYxzUT~fSzYx-1cWJPN82w__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2018")
                                                    attribute("id", "link000605")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000605 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AvQYDI-MqYpbL-p-z8dmL4QmBfBxfyVwwLIyaa-O-ksYV5JdIgC30NYqCSLV6zw5-rwGClRb9TRikIUBTmdCBbGKGVWtMLTMtpNWRjSF45UHf48MbF55kqHSsP-9V-PSE4ZjZC8Ak24vW67wMV9EWk80ALiCOEhpIQw2uzne6n92bKCQ5pGGZPSXlyw__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 17/18")
                                                    attribute("id", "link000595")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000595 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-A~YB1Ym1mSRCqC9zaMDToBBjNm0UfOUfSTtIXeXS-OKVA9caIP7fkNz~YxeUcHotPr2Trjll4TmpVTftet4ZouIw0ANMBY7YT7BuCkjU9T35om5W4~e4oIHtOUAw4ws9YKowPNDTZSMDUkqD8xIxQy~-xmIMhX2nRq1SnPaAyemt5DvJUJah70Mkh2w__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2017")
                                                    attribute("id", "link000583")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000583 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AJ3HpsDZbI98zW9zL2x5dPykvJr0RmgS5b1Gk4sJd0-fpqWVnKBjnnzmqj1FWKfzQogDCwQQCfhO22axPhi7SPwHZXRJW0EYmcknyT5CIb0v9xMuT-uW4~kGvPPitbDJXlugGpozBRLpZzWOrHcM1P2i4uqGT1sVnsEpgYm0~5riq7RKfgn89aJPJGQ__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2016/17")
                                                    attribute("id", "link000576")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000576 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AbNvkIdoR84Rwl~jo5ykMQURH~QfS2Ucb0R01xb0dK572UIUGYeypUQRCb5338tuRV~w2IuHB8QyySjrtt11MsCc51a2ktzEHuOuUhxJGCrI6yAUCYlQpRdgTPpnMLCJGV37GOvQJW6vRzgmsqxCuufCSUv37ez34jrSGVDEHCS1hKCbgQWc0kHdK0g__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2015/16")
                                                    attribute("id", "link000560")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000560 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-APgR1m2cdmwaU99Tl8PlV5ZU0M0ReHCPXrJ5O3ElSbL9q~QKw6AIUn5y7z1vFheCXmUHjqeuLkMQ3BX-luGzzqfiWUFS8v5UdHjCZLttxFzrsXdcT3mJvD5d2tU6q3C2XjDihzGWdD2yFq09JhQJf0SeWyIzIIk3PhCSrGWPWfGuBPQVx8pgtySx3XA__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2016")
                                                    attribute("id", "link000567")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000567 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AuNzIQCJOouud8a-LzGdxaSBcs4BanpkbLpS9gVBu1~IYs47tRq7-HiR94EwZsHQZ4Vsp2SmJcTYv~G~iAHjXuhIdYrptt0FU6kqNHPzg8ve4Ya~vrQPSHjRaa8YgywbDA-CRXIZHXf0U9MAp1G~L3~IBzlZoaklv4V3fZR1D7IHSDXk3zspO4IKDdQ__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2015")
                                                    attribute("id", "link000549")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000549 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-Ao6b1nSDFwjYpXX2R7Y1Wc4LpeY1Ikgzmux1yUoinMK3dpt1mLlTHFoEMkk~nqn1SAO6h1q1K2LLn2YtT~-An6Mxu0cTfdBSDDXIZY--SKhzZoJ9ymSy970PjsN0OA3vmXj1kPBcJ4mqgReY40O3r21H~y6dpU6F6gRBHVDZbqlRiUkuonM8UQ8yndw__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2014/15")
                                                    attribute("id", "link000547")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000547 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AynSTMAPmJ9OQ7Ije5GcPWZSHPLBTrUh3HtlvlPOrI5icD5guVUE7kfGxT3~A3extue8BqZGn3OMbQaOSyInMfoDdqq35t02UatOVNxndxaqlVzSSD-ZHOKKw0tGueq~y5HEHHaOCr5d23GRGJiSS4GWfMOjXC3i9rRDSSX6pUTgJrhAHu5ejPBkElw__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2014")
                                                    attribute("id", "link000530")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000530 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-ACyd0QcDRFGOPY-gDNyRbdoCFS1fNpmCQESLRmY3vfYhTfvlTa3n8hKYkNVT7KCGmU-B4WdIBn4RVZgVLKRJyZLBb5lXCyZMv1NdT-CVnTHJ1lG4UoSEJnPE1~nuIaQu33BvsHYIh8PFFwlc7k7fp-TUvUvPEHsi~nd0uI924emH7uKYY~p7EIFWnoA__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2013/14")
                                                    attribute("id", "link000504")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000504 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AZB7m5xik9STAWTV5dJ9Z8KaQIs7MzJluq9qiyGcL2fOWsNmnoyRPLv4cuJTPO~pE5ZTBOSP0cV4ectxPc6vK~qu5TFYIauEKi0kzCDJ3SBm6aGsWGlIICs7xer36Ljy3D7qNRxUVwI3BKoK2Xu7vD~oTQWcRv5TFY2Vq8kC8Jm~5MQ1GpaOwv0ccLA__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2013")
                                                    attribute("id", "link000491")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000491 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-A7fy7VmOWlymJkpgw82234KjVs1Y1dNgUe3-A-Zy418sdYBt-ryPe2enbGCiO7aLXT-X192vlNbvHpknrD9kn98y5rbYkbmn0e0PXjomee7ihXyI-mRImOUfkiHGMDa5nYZMOomxLXiLbriPCcMBb4WplFW6DHOjHdiDDtNfor8ATVRXTtbMIryK~Pw__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2012/13")
                                                    attribute("id", "link000467")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000467 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AH273aoY4B2fhvJQx3BIp-mMK2QEpjWOhzTncU0d5-4-zMTXzD3i5U1hUvmkXM8TWPYYjrjAYjFWoD~1mPvGL7fRbASOCwpmo0hmyXoKsZ8xzWDSrXr-hUxX5Teb-Lbh6KxmuTQRi9in3QF25aFIL6a-QPyJh30Oif4aNIIHpVPMtCOeHU7iKKHjqJA__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2011/12")
                                                    attribute("id", "link000469")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000469 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-As5CbGXhj8rld~-tMjyyfeqTcnxi6ujxKIV9YfAT1z9sqRqGSNEY7K0DyhoRjo3PHrsDmXb0yBq~hsIQ31R7aNNQRpfhzqjJF-p6krm6~PoajNeoR4SL7tRTMSyvN7ZAoH6ug9I3yJUcOmd8smrYGaY1NGqLcwx7IacrP-xmJvkP4Mc~H0SaUcrqExw__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2012")
                                                    attribute("id", "link000468")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000468 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AP3gFOqJqmHxU7J858y74IEsILZ97xsPMgMChtU39ij7Zy7LDrfiLOVgpf5HZe1V1Nfn2x~c5LbpwlPQb8aVEWayNyENs6F-DunI2yYq4~eOCmKoyU4xdExzjQcznTHeAHFIujTx23NdiorghGhTiXWEEsH8PIaG5LV2Z6ZDOQw1dcAJG09DxTwqkJw__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Sommersemester 2011")
                                                    attribute("id", "link000470")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000470 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AP5~bXXr9r2LbPpQQODUMOQD15WQ4KqS8-8eAoQx0bdc61xV3WwivfDg8a3E95urc9OU4blJUBgZ5E2Aep~iHFRv~SQ2IxWZFmeLEEzzFf77odjn1CYYv0U4~iBb1bumc-nkPWvJe94qf6Mn3PZfvIgBIVp7MerfIzwYATXSdPMiMuUtCuVfvw0~hhQ__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Wintersemester 2010/11")
                                                    attribute("id", "link000471")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000471 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AyrU3SQrF5N0vbJsGLxzKh-7S24UmaUKCHzSFSQsG~-y5NVMR78f9IDk94aHmSPglXHGWw2Aoj3eyO2Ie9eQhOdqodlb7CvvAoTguTMFZbN3FMFItwVwXEX~7Dd6jEgHwaylOndl0khACzbzWBO0AM8bZ9BxcvsDbFLTSe6WqcMzJdS0cz2iWA43wLQ__"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                                li.attributes {
                                                    attribute("class", "intern depth_3 linkItem ")
                                                    attribute("title", "Altsysteme")
                                                    attribute("id", "link000481")
                                                }.content {
                                                    a.attributes {
                                                        attribute("class", "depth_3 link000481 navLink ")
                                                        attribute(
                                                            "href",
                                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=EXTERNALPAGES&amp;ARGUMENTS=-N000000000000001,-N000481,-Avvarchivaltsysteme%2Ehtml"
                                                        )
                                                    }.content {
                                                        extractText()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                li.attributes {
                                    attribute("class", "tree depth_1 linkItem branchLinkItem ")
                                    attribute("title", "TUCaN-Account")
                                    attribute("id", "link000410")
                                }.content {
                                    a.attributes {
                                        attribute("class", "depth_1 link000410 navLink branchLink ")
                                        attribute(
                                            "href",
                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=EXTERNALPAGES&amp;ARGUMENTS=-N000000000000001,-N000410,-Atucan%5Faccount%2Ehtml"
                                        )
                                    }.content {
                                        extractText()
                                    }
                                    ul.attributes {
                                        attribute("class", "nav depth_2 linkItemContainer")
                                    }.content {
                                        li.attributes {
                                            attribute("class", "intern depth_2 linkItem ")
                                            attribute("title", "Account anlegen")
                                            attribute("id", "link000425")
                                        }.content {
                                            a.attributes {
                                                attribute("class", "depth_2 link000425 navLink ")
                                                attribute(
                                                    "href",
                                                    "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=CREATEACCOUNT&amp;ARGUMENTS=-N000000000000001,-N000425,"
                                                )
                                            }.content {
                                                extractText()
                                            }
                                        }
                                        li.attributes {
                                            attribute("class", "intern depth_2 linkItem ")
                                            attribute("title", "Passwort vergessen (nur für Bewerber/innen!)")
                                            attribute("id", "link000426")
                                        }.content {
                                            a.attributes {
                                                attribute("class", "depth_2 link000426 navLink ")
                                                attribute(
                                                    "href",
                                                    "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=LOSTPASS&amp;ARGUMENTS=-N000000000000001,-N000426,-A"
                                                )
                                            }.content {
                                                extractText()
                                            }
                                        }
                                    }
                                }
                                li.attributes {
                                    attribute("class", "intern depth_1 linkItem ")
                                    attribute("title", "Hilfe")
                                    attribute("id", "link000340")
                                }.content {
                                    a.attributes {
                                        attribute("class", "depth_1 link000340 navLink ")
                                        attribute(
                                            "href",
                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=EXTERNALPAGES&amp;ARGUMENTS=-N000000000000001,-N000340,-Ahilfe%2Ehtml"
                                        )
                                    }.content {
                                        extractText()
                                    }
                                }
                            }
                        }
                        div.attributes {
                            attribute("id", "pageHeadBottom_3")
                            attribute("class", "pageElementTop")
                        }.content {
                            div.attributes {
                                attribute("id", "pageHeadSwitchLang")
                                attribute("class", "pageElementRight")
                            }.content {
                                a.attributes {
                                    attribute(
                                        "href",
                                        "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=CHANGELANGUAGE&amp;ARGUMENTS=-N000000000000002,-N002"
                                    )
                                    attribute("class", "img img_LangEnglish pageElementLeft")
                                    attribute("title", "English")
                                }.content {
                                    extractText()
                                }
                            }
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
                                            "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/authorize?client_id=ClassicWeb&scope=openid%20DSF%20email&response_mode=query&response_type=code&ui_locales=de&redirect_uri=https%3a%2f%2fwww.tucan.tu-darmstadt.de%2Fscripts%2Fmgrqispi.dll%3FAPPNAME%3DCampusNet%26PRGNAME%3DLOGINCHECK%26ARGUMENTS%3D-N000000000000001%2Cids_mode%26ids_mode%3DY"
                                        )
                                    }.content {
                                        extractText()
                                    }
                                }
                            }
                        }
                    }
                    div.attributes {
                        attribute("id", "pageContentContainer")
                        attribute("class", "pageElementTop")
                    }.content {
                        div.attributes {
                            attribute("id", "pageLeft")
                            attribute("class", "pageElementLeft")
                        }.content {
                            div.attributes {
                                attribute("id", "pageLeftTop")
                            }
                        }
                        div.attributes {
                            attribute("id", "pageContent")
                            attribute("class", "pageElementLeft")
                        }.content {
                            div.attributes {
                                attribute("id", "featureBanner")
                            }
                            a.attributes {
                                attribute("name", "mainContent")
                                attribute("class", "hidden")
                            }
                            div.attributes {
                                attribute("id", "pageContentTop")
                                attribute("class", "pageElementTop")
                            }
                            div.attributes {
                                attribute("id", "contentSpacer_IE")
                                attribute("class", "pageElementTop")
                            }.content {
                                script.attributes {
                                    attribute("type", "text/javascript")
                                }
                                h1.content {
                                    extractText()
                                }
                                h2.content {
                                    a.attributes {
                                        attribute(
                                            "href",
                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-ATOEKOu02Xbx7rRfY56Oe76y-GSqlw0MpB65gdAu9d1sLxj1M4yEaQBnmo3nzpH8vDckCybZGtPo1Xu8xxtvp6CPLz3SVvMm~jA3CFbPt0YwD1xJCyzGQjeZd7xyUFy5wJcUbYpAK~~oEJ9v~LQEhPQj1D3KnNQoQ7wY9mu6kLSQ3hPA5iT80CEW91t2jEDsur0Gr~NmB~2q4bxs_"
                                        )
                                    }.content {
                                        extractText()
                                    }
                                    a.attributes {
                                        attribute(
                                            "href",
                                            "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AjMq9-~0USAhkADNV~IjLtXvnAQyqzulhchjvY83pDUhs1BGaJFNYtjyrJ~ah5UYrK57gQ3lCBcnzMNM9l64fb~MtM50h2uwp7-33ayDAcBKQYTayXzvv-Md3FO1bEMlUOsc5Z9aZR42O6GWvceJgk6VAMDu7xajWBIlW6ok3XBVEg1Nxi-dIQcRS5ETZIgJRBWRiZ1RiWLTGjws_"
                                        )
                                    }
                                }
                                div.attributes {
                                    attribute("class", "tb nb")
                                }.content {
                                    strong.content {
                                        extractText()
                                    }
                                    br.content {}
                                    br.content {}
                                    extractText()
                                    br.content {}
                                    extractText()
                                    a.attributes {
                                        attribute(
                                            "href",
                                            "http://www.tu-darmstadt.de/universitaet/fachbereiche/index.de.jsp"
                                        )
                                    }.content {
                                        extractText()
                                    }
                                    extractText()
                                    br.content {}
                                    br.content {}
                                    extractText()
                                    strong.content {
                                        extractText()
                                    }
                                    br.content {}
                                    strong.content {
                                        extractText()
                                    }
                                    extractText()
                                    strong.content {
                                        extractText()
                                    }
                                    extractText()
                                    br.content {}
                                    br.content {}
                                    strong.content {
                                        extractText()
                                    }
                                    br.content {}
                                    br.content {}
                                    extractText()
                                    a.attributes {
                                        attribute("href", "mailto:tucan@tu-darmstadt.de")
                                    }.content {
                                        extractText()
                                    }
                                    br.content {}
                                    br.content {}
                                    extractText()
                                    a.attributes {
                                        attribute(
                                            "href",
                                            "https://www.tu-darmstadt.de/studieren/tucan_studienorganisation/tucan_faq/index.de.jsp"
                                        )
                                    }.content {
                                        extractText()
                                    }
                                    br.content {}
                                    br.content {}
                                    br.content {}
                                    strong.content {
                                        extractText()
                                    }
                                    br.content {}
                                    extractText()
                                    a.attributes {
                                        attribute("href", "https://www.tu-darmstadt.de/gasthoerer")
                                    }.content {
                                        extractText()
                                    }
                                    extractText()
                                    br.content {}
                                    br.content {}
                                    br.content {}
                                    br.content {}
                                    br.content {}
                                }
                                ul.attributes {
                                    attribute("class", "auditRegistrationList")
                                    attribute("id", "auditRegistration_list")
                                }.content {
                                    li.attributes {
                                        attribute("title", "Courses held in English")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AFaBjY-~dlA97Q95hTDg530USertB0w0gSdwbmpF1wgc4TeBAA2DaeqetChvQSgenK~IEZ7LgJrKYm7MDGG5s7ZTX5poIFfKTNR94ss6OTDovAIbSP6cW~ixczRAMokmQ3h7TlWz05eSZcT2zSIiT7yzVbo8fhWgM1gH2SrqIbbShTcE6EqATUOp2hzHEf13nov~ZH0t2Gik5B2A_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "Alle Orientierungs- und Einführungsveranstaltungen")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AoWIFi6JzJRCiVFqCUe1IxY7Mcl85xLl-JyG5I0cpr0~lSIDF0fjhpPk3Bxi3x4gwh0bDRNdXwKN5EgKHi9OAtRIdw~63UeEv3lMeyy8~nrIYMpbmQdNlS0-JnuAb3e0R7r-Lb21c4cioU0LLWNvYlBRDMXb~AQexO-TbOfK9v2dsbgaqJpBmLVHKMCU8gga8s9ciSJx2Wd1t654_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB01 - Rechts- und Wirtschaftswissenschaften")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AkKrSzTbTGr7vY2b9037brLjqNR6FGcYTUTN1p-yoWcE3K2cPur5GyiVLm-C3D4U3RbWJDfDI9lqCvkuwSPC3lQib4GZhJraRuH99SRcqFrbc4z6Mlnc9yVzz5tMtoUnhStl1o7iclDJON0Y3FgxbT2kAKHLRY~tGvT~yiZ~FY3qiohMClhumiivFHva4IgVMu1PVlY9qKWrvvtU_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB02 - Gesellschafts- und Geschichtswissenschaften")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AQUBHBeYq~pTzN5Rf0y9hhVpDxy~wkFM-yEOoYayBLICrLLedQT7D0CYcf8jWD4WYN-w8An86vBW7KbB8OUJwh2hpA9u6B5WN-coMFc3x02ENzNoF6xivJGK3uEn3~raq67kfb807pPFs5Qoya5zdZLzseUmJ8Tum8pih~omx7HelnvdiDywdtXb8EHhXpRNaMsHe1ps4A~sNaaU_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB03 - Humanwissenschaften")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AvqZrRwIPRSlh1ZmkCrOfLcrGapBUc44pOJXTuzcAGrsjkOjG9n2jWFGKKh0Vs4oQ~abVyeG1tJyhXt03QFGmdzu709ER6WNHbCJ2kjEmp1IT8vhxcdytY9U9itHxxTJqDYtGj1vWOjDt5qrVyMOMPLP7WJUNyz0X8VeWaLRqRf90bSGAnSCyidOnzSKP6wb~3~nIx9BINWB2bZY_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB04 - Mathematik")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AnWxxyKK7NTns4NadkyhDHH9ZOvdPVw-QlJZ5kCuO2JaG02vSuddG-SfzWA-3PjH713tvtzFm5ITqFKDNT3AKGCtkwcg-4oGmb2KWDbYKxBnQxyhmHUBAslOCWIWwteZpaKUyRlsPaU9Riu~cSyicjZ~38WlMuwqhTLcFaTxffUUWMBtXqhvDUATWuQoV4uAw74aDX616Ux60lSI_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB05 - Physik")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-ACA4VeLzEsvtf1uwcxL1cJ5IJXjWOzeT08yzJIJRksE1M0K~6XjZaSKCfcC7uH~3EfoNyV-2gJd69IZC1oJrX7ef1s1VMBiJ290u9BpDq3RxQLVGZrhBdnofEslq2C33rkL6B9oSeEkKFFGSfZ7EuIVjzMCtpl8YO59ejQ53UxU44Vs1jaqNmEoWbaFzXgOVicRqKylfg4eJDrRQ_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB07 - Chemie")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AAyb30BCHfi8apDW2HPg0VqCYm5fiPgkdCW38cK9ny-AT55elusk4tatQ49ksCTIynNApdvl7hOCKJd8aUglMN8tsGtpFsEzCKqnhUWZEmVEqX03~wGMXBuj33ZHrdWHcQ-YVo9OLaw0q2SWc2zq~OFZrtabqT~EMS1WiGpm6rGV-xws91YJseLzWoFd2szNIZUUiRCP7TitSuP4_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB10 - Biologie")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AXYY-ppaVx7U2pjARtQzqQFDlF-7jWY9v9GOWQOEUIb2PHSxjrCM5vW3n9Xtkb7uXgcsb0tEDMLx~jKnc3bmQIN2CIljV9XFIfIwEOq7NCiEhntrTt-5qggvk-kHZItjCu4y-UJgkIxwo6UGwOfesvUJ35QpBgJiOgBIxXKNlbHIG6z5ibCFFFy5mktco~zGrDt6htHVVIhmQvcM_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB11 - Material- und Geowissenschaften")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AmMOMZtPPO3PAVrHbWoDFUchvuTE7xPQzzW56PihS2-vYOl1pKw60PQUxBEv56ssd3l6IsXk~nNsapH9EYlr-nRbS8UdZ9C~8kNTehv-qhtO0pTO2Tn92UtkA81sEwr15S19M8NIB0fw7Jhvyk0hPUwH~KCM4mYOCdkTAMOIXBoxJW1ccmVfvAMgVUjE~JCFblB4EotWIhkbB~Cg_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB13 - Bau- und Umweltingenieurwissenschaften")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AdeT0T000rUbh3ZyqvmKZNjniBWv3h4q~i4lctXHUCQsqqTclE9EoyhBFXU6qr3BVEZqQLB7aiM6ov66oO~W8H38lOJueiXi0okOrpXx8OYczvEJr6wwBKLVJDxD5owz2U50oFb9bzeyagfaM3V6BK1hSDg5Gy8QMp7tZ9UOm2tnHlXRcpUKjr7uERpLtkFGBx~OxV9uU-VYNfio_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB15 - Architektur")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-ALDY40i-WtXSMTY7-inMjtEqxiIs1OX0qVQmSMx5foAboRba-A3B7z5g1dP7gEVYapQccKto0yxfBpEdTWxkhJYhQphDygk-OaRDLpkjEpFOWzVbv5RD4cFIE-whyxM43FX~ngGEdIyMODqBzK6OoU5IjdmdM2t255I4QJxI7hGC0vR0xX-X8SsmrX~9EnFp2uWpoHxYxB2PN66A_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB16 - Maschinenbau")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-ANHj68z9LMuqGNHTdpj4ymtW0oUwy5gSZS-cUtuMDy5zsWf4-5GaUYBA2JDlBvVmm-6R2S-oeQekhscyoH5j-IGyXxSJTzdcK~oq1u7jRu7pR-UFzojw8DSP26i-kvpsjJj3UjAc1d-mfbrJf2zYZLpfG7HCDq9yUdSW40j0Y7Qibzd~RbowFlfPJEj9gSegYqEFesh8dAdqg6ts_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB18 - Elektrotechnik und Informationstechnik")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-Abu-lJoDp39ibt1p2VkuRzDe2VEcGlvnESMa3C6rSH90kTngLbHbfGNF4ZuvBCyFBpayboej4ifRg3beW68k-th0FvVU8SjNRBCsvmE1y-~cI1ItJSEbEG05zYaSPWRoOxtb6oTwsLAaSF4asvi5D4JLegCvrWG9pEeUx-opoSvjB~kv4do0svUeIQgviJjOojog9MpoEYdWR0FI_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "FB20 - Informatik")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AWqY8Xw1k6DeNGfyyTz4btqo4L6E4S-VuHdaOqlzFlougMkWh9iePOQSTdW4GPhU3jSlNUtwoBFGshfGQaM2lVXfYbgYq1~o8HfCHxnvKwBQx3x~KY-9ow4ICc~3GPnxRzDWbJn7Cy8ki~MpujsQBoQhZMS592ICVQUrsdrZ2zfm0cC9LF23XM4wFCzN4S~Yz6SZAoIuQLzJCegg_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "Sprachenzentrum")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AgC~m1RBtaJ4sBgd6qhToty9RQZM8UnLj6R9PfYZfOLyDF71-ykGCB7NRjbPnbRPrKOt2TWgtdQvCLM58lADdpdO5u~xrkR5sFFhKYamGpkcx9HkeAv79AASZe0~36~uCaDwfMOAI2IhjuPggVvTKDCAXPP8pid9Ec4DxRlcfgjkBi~Rd7TeOZIqRj4To8F~6DDnUpxMAMhQb8Eg_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "SB CE - Computational Engineering")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AyDZ8R3T~it~F1Z9yGtgOlhSM4VH1guwN-sfqNUMEMtMitONnTjHuEuy-fI1R9E1N~d2M~i~Bw5YI-KuEQRgRoeHHbF9wDg8yHN~nE7X~o7y~0TNW16Re45ZvGpTMbBkyMF6n8Kfuuw7Ea~jCg2kD4YnXY4g6NJQckZra9EQ1bRI9q7jXe3WmI8hr-EKZvNlbZqgrfJXk1VNNByQ_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "SB ESE - Energy Science and Engineering")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AG2BrCShKhHvrxEJyOYJFwPR3kQKwUR7dY830YbMHIMXwu-ALTBqqdAg24hKgp8Omq6fMzKZVlxuRXXdW~ZkvdNJfn7vd2a~cbA0OIDexSDm7~kPdSMcxMphf-bT7zh4Ks3nZp0-PHGpYRay1db3m0rtmh0QixqWPtsr84oTpezcwx5jZzudMP4DVdBg7ZX8UYq34~FvUHFktZk4_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "SB Mechanik")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AmUggOM4IUUjw9qZgHTrBRvru7sVrIZdsztGLzKgIZR87Zd1OG~sTehajkhFbC1qN6hFvRt69L6lsRs1a9hH1l97rizSjljxlSA-h9NYt0V1bs1TbL-2eJRI9Vlq2mJNK30h5fDV-n211gecQNg3gatPTQosrk~dLKmlr9Pp2~Nh339BdfKw8QWEONG~Sgo09-GUcnVIqqyGQfhM_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "RMU - Studienangebot")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-ADgxHcDHc7ts7jFD2MDAcYKoXYhpCL2NFS03W575QIZs8STY5-Ljy8Q-oX~Roqr3lX09QQ~-zY4bdaqJf2fkaySSrRg~-jIYaT8hkc3Y3MTp4ZjnZylPlFgxb0ntVc83nZZR1hTA7avy6rcFcRTA0NUPgWJzRz30ZdZWeo14PEqe2hyyfVA17-FndNQHqu0wKiNNcsfR2iZdonNk_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "Unite Virtual Exchange Credit Programme")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-Agf14L7UR0OR-43ZDaBFRPt-klzilGWoTm83cA0oSuK5L3SrOhnQvPnr~3tU~A8g9-418lRI49auNSzC~oD7gXfoIL9XHBWdE4IeCsndRIuffdHOOgQNXmvTM3KUGSMCcjpfJVItDU3wOMZ-Rpp~OE-vXa4VH~fLSXwW7vS2fYvrWLuPhgDJ4sxvUguZsQXsEIMziw4VHieePKck_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute(
                                            "title",
                                            "Interdisziplinäres Studienprogramm (iSP) Sustainable Futures"
                                        )
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AkTK8DugLq88r8jodRj1OjuJcaTE4EnCiszXPiJmJbWEr2nubFx3luyKUdnb--EDRDwkIYMuGCGjr708hYUFOuERyxTbipCiXL~eHGT5fdkCcl0EAWir1IDFLyKNjhrUnnzah1cPQoFRTVuqfEzKLXwVDh71Pq9ukEWdqdwmhmZ9tiXbvg5KEYOCstySYXaM4H2yt5fuyV6hl678_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "Lehrveranstaltungen mit Nachhaltigkeitsbezug")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AKoeSu2aho84V37TJW0aEEaXg~tfamMtF-5pkHLgRxEfdDIHoccKWD69NBPUvDRIGxDfubgXP~a9d2I~kuBVWfBTf2QLICPHYvSUpvOlN-3Dt2eAAtCOmeeixmHmPPnESZvGHlP4RjfASwcUlHjbVwStaoD48r6H2TrGBJiTa87xvZbAlZxzVur~CIQTm3lyJDsH1iOe7VfkFspU_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "Öffentliche Veranstaltungen an der TU Darmstadt")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AI9IEPygfPDT9PMgQnuSJjF8Y57-t491U4~2G38ZRcxnYOCQOCXQMGOafIyG6q-BinFRwrTYK7ZGRmv8GOQYpMxKTYUuN1j7gGRdjy1Jj6l0xKTrJ1EBQkzGWBUu6vBkdU~NoDXEo2iaALycN-dYkGmMzGjvTgjXFs1hU4yFLUqtTnJWD5y1eKnhnmG6oRX4IzaKsrtIm9XGtM70_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                    li.attributes {
                                        attribute("title", "Raumsperrungen SoSe 2026")
                                    }.content {
                                        a.attributes {
                                            attribute("class", "auditRegNodeLink")
                                            attribute(
                                                "href",
                                                "/scripts/mgrqispi.dll?APPNAME=CampusNet&amp;PRGNAME=ACTION&amp;ARGUMENTS=-AYKYxR4baGHMoTF4~0NvDpLQBa6UdaCp7AYXTrHI-y1rR~U67hSw03cpBFWeJPSHvhTVIZTKwtl7MZEV70vpEtgwwDOWpxw0b7Gv7eZnM6paZ-AE~mUminhEqLmxOnODa6vwW4YgEXWTCH16mA37IS93Q-A-VlCKRnbFvZu8O3TAdK30pj95Pi9y56CY-HwJs0JPevtUdABnvgJc_"
                                            )
                                        }.content {
                                            extractText()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    div.attributes {
                        attribute("id", "pageFoot")
                        attribute("class", "pageElementTop")
                    }.content {
                        div.attributes {
                            attribute("id", "pageFootControls")
                            attribute("class", "pageElementTop")
                        }.content {
                            div.attributes {
                                attribute("id", "pageFootControlsLeft")
                            }.content {
                                a.attributes {
                                    attribute(
                                        "href",
                                        "?APPNAME=CampusNet&amp;PRGNAME=EXTERNALPAGES&amp;ARGUMENTS=-N000000000000001,-N000334,-Aimprint"
                                    )
                                    attribute("class", "img img_arrowImprint pageElementLeft")
                                    attribute("id", "pageFootControl_imp")
                                }.content {
                                    extractText()
                                }
                                a.attributes {
                                    attribute(
                                        "href",
                                        "?APPNAME=CampusNet&amp;PRGNAME=EXTERNALPAGES&amp;ARGUMENTS=-N000000000000001,-N000334,-Acontact"
                                    )
                                    attribute("class", "img img_arrowContact pageElementLeft")
                                    attribute("id", "pageFootControl_con")
                                }.content {
                                    extractText()
                                }
                                a.attributes {
                                    attribute("href", "#")
                                    attribute("onclick", "window.print();")
                                    attribute("class", "img img_arrowPrint pageElementLeft")
                                    attribute("id", "pageFootControl_pri")
                                }.content {
                                    extractText()
                                }
                            }
                            div.attributes {
                                attribute("id", "pageFootControlsRight")
                            }.content {
                                a.attributes {
                                    attribute("href", "#top")
                                    attribute("class", "img img_arrowUp pageElementRight")
                                    attribute("id", "pageFootControl_up")
                                }
                            }
                        }
                    }
                }
                div.attributes {
                    attribute("id", "IEdiv")
                }
                div.attributes {
                    attribute("class", "invAnchor")
                }.content {
                    a.attributes {
                        attribute("name", "bottom")
                        attribute("class", "invAnchor")
                    }
                }
            }
        }
    }
}