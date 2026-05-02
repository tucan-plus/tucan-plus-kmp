annotation class HtmlFromResources(val path: String)

object Test {
    @HtmlFromResources("html")
    fun Root.someFun(sessionId: String, menuId: String) {
        val a = 42;
        var sessionId = sessionId
        var menuId = menuId
        doctype {
            attribute("#doctype", "html")
            attribute("name", "html")
            attribute("publicId", "-//W3C//DTD XHTML 1.0 Strict//EN")
            attribute("systemId", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd")
            attribute("pubsyskey", "PUBLIC")
        }
    }
}