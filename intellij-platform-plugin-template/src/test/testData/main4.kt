package de.selfmade4u.tucanpluskmp

object Test {
    @HtmlFromResources("html")
    fun RootContentScope.someFun(sessionId: String, menuId: String) {
        val abc = 42;
        var sessionIdTmp = sessionId
        var menuIdTmp = menuId
        <error descr="Here more content parsing is needed">html.attributes {
            attribute("xmlns", "http://www.w3.org/1999/xhtml")
            attribute("xml:lang", "de")
            attribute("lang", "de")
        }.content {
            <error descr="Here more content parsing is needed">head.content {
            <error descr="Here text would need to be parsed">title.content {}</error>
        }</error>
        }</error>
    }
}