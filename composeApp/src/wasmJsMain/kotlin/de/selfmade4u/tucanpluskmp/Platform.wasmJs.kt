package de.selfmade4u.tucanpluskmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Worker

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

@Composable
actual fun LoginHandler(backStack: NavBackStack<NavKey>) {
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(Unit) {
        val url =
            "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/authorize?client_id=ClassicWeb&scope=openid%20DSF%20email&response_mode=query&response_type=code&ui_locales=de&redirect_uri=https%3a%2f%2fwww.tucan.tu-darmstadt.de%2Fscripts%2Fmgrqispi.dll%3FAPPNAME%3DCampusNet%26PRGNAME%3DLOGINCHECK%26ARGUMENTS%3D-N000000000000001%2Cids_mode%26ids_mode%3DY"
        window.location.href = url
    }
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(
        name = "test",
    )
}

public actual fun createDefaultWebWorkerDriver(): WebWorkerSQLiteDriver {
    return WebWorkerSQLiteDriver(jsWorker())
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun jsWorker(): Worker =
    js("""new Worker(new URL("@androidx/sqlite-web-worker/worker.js", import.meta.url))""")