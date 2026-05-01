annotation class HtmlFromResources(val path: String)

object Test {
    @HtmlFromResources("html")
    fun someFun() {
        val a = 42;
    }
}