package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.platform.UriHandler
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import org.w3c.dom.Worker

class JsPlatform : Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()

actual suspend fun getLoginUrl(uriHandler: UriHandler): String {
    return "https://localhost/?code=test"
}

public actual fun createDefaultWebWorkerDriver(): WebWorkerSQLiteDriver {
    return WebWorkerSQLiteDriver(
        Worker(js("""new URL("@androidx/sqlite-web-worker/worker.js", import.meta.url)"""))
    )
}