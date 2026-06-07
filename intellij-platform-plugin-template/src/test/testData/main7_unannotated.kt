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
                }
            }
        }
    }
}