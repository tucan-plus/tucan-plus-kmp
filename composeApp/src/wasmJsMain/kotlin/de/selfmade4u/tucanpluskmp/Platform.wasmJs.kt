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
import org.w3c.dom.Worker

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

@OptIn(ExperimentalWasmJsInterop::class)
private fun registerProtocolHandler(): Unit =
    // TODO don't hardcode extension id
    js("navigator.registerProtocolHandler('web+dedatenlotsencampusnettuda', 'chrome-extension://beljbpcjdojfolompoclagcakkojdhlj/build/dist/wasmJs/developmentExecutable/index.html?%s', 'TUCaN Plus KMP')")

@Composable
actual fun LoginHandler(backStack: NavBackStack<NavKey>) {
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(Unit) {
        // TODO our callback also lands here so this loops
        // TODO I think this needs user interaction
        registerProtocolHandler()
        val url =
            "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/authorize?client_id=MobileApp&scope=openid+DSF+profile+offline_access&response_mode=query&response_type=code&ui_locales=de&redirect_uri=de.datenlotsen.campusnet.tuda-web:/oauth2redirect"
        uriHandler.openUri(url)
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