package de.selfmade4u.tucanpluskmp

object Test {
    @HtmlFromResources("composeApp/src/commonTest/kotlin/de/selfmade4u/tucanpluskmp/testData/simple_html")
    fun RootContentScope.someFun(sessionId: String, menuId: String) {
        val a = 42;
        var sessionIdTmp = sessionId
        var menuIdTmp = menuId
        html.attributes {
            attribute("xmlns", "http://www.w3.org/1999/xhtml")
            attribute("xml:lang", "de")
            attribute("lang", "de")
        }.content {
            head.content {
                title.content {}
            }
        }
    }
}