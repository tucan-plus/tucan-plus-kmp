package de.selfmade4u.tucanpluskmp

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.RecomposerInfo
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.window.ComposeViewport
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.core.okio.WebStorage
import androidx.datastore.core.okio.WebStorageType
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import okio.FileSystem
import org.w3c.dom.Worker
import kotlin.coroutines.coroutineContext
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsString
import kotlin.js.Promise
import kotlin.js.js

public expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

public expect fun createDefaultWebWorkerDriver(): WebWorkerSQLiteDriver

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(createDefaultWebWorkerDriver())
        .setQueryCoroutineContext(Dispatchers.Main)
        .build()
}

@OptIn(ExperimentalWasmJsInterop::class)
fun getSessionCookieInternal(): Promise<JsString> = js(
    """chrome.cookies.get({
  url: "https://www.tucan.tu-darmstadt.de/scripts",
  name: "cnsc",
}).then(c => c.value)"""
)

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    println(window.location)
    val uri = if (window.location.search.contains("APPNAME")) {
        window.location.href
    } else {
        null
    }
    ComposeViewport {
        App(uri, createDataStore(), getRoomDatabase(getDatabaseBuilder()))
    }
}

fun createDataStore(): DataStore<Settings?> = DataStoreFactory.create(
    storage =
        WebStorage(
            serializer = SettingsSerializer,
            name = "tucanplus-config",
            storageType = WebStorageType.LOCAL
        ),
    migrations = listOf(),
)