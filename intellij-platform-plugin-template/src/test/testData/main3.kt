package de.selfmade4u.tucanpluskmp

object Test {
    @HtmlFromResources("html")
    fun Root.someFun(sessionId: String, menuId: String) {
        val a = 42;
        var sessionIdTmp = sessionId
        var menuIdTmp = menuId
        html {
            attribute("xmlns", "http://www.w3.org/1999/xhtml")
            attribute("xml:lang", "de")
            attribute("lang", "de")
            head {
                title {}
            }
        }
    }
}