package de.selfmade4u.tucanpluskmp

object Test {
    @HtmlFromResources("html")
    fun Root.someFun(sessionId: String, menuId: String) {
        val a = 42;
        var sessionIdTmp = sessionId
        var menuIdTmp = menuId
        <error descr="TODO missing parsing">html {
            attribute("xmlns", "http://www.w3.org/1999/xhtml")
            attribute("xml:lang", "de")
            <error descr="Fix the parsing here 1">attribute("lang", "de")</error>
        }</error>
    }
}