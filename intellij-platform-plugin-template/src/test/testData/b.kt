package de.selfmade4u.tucanpluskmp

object Test {
    @HtmlFromResources("html")
    fun RootContentScope.someFun(sessionId: String, menuId: String) {
        val a = 42;
        var sessionIdTmp = sessionId
        var menuIdTmp = menuId
        html.content {
            extractText()
        }
    }
}