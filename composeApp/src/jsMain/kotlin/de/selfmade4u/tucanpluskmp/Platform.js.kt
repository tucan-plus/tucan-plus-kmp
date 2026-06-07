package de.selfmade4u.tucanpluskmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.UriHandler
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.dom.Worker

class JsPlatform : Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()

actual fun fromWorker(worker: Worker): WebWorkerSQLiteDriver {
    return WebWorkerSQLiteDriver(worker)
}

@ExperimentalWasmJsInterop
actual suspend fun getSessionCookie(): String {
    return getSessionCookieInternal().await()
}
