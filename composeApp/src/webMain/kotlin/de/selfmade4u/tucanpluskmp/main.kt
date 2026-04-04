package de.selfmade4u.tucanpluskmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.RecomposerInfo
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.window.ComposeViewport
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.core.okio.WebStorage
import androidx.datastore.core.okio.WebStorageType
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import io.ktor.client.HttpClient
import io.ktor.http.Url
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
import kotlin.time.Clock

expect fun fromWorker(worker: Worker): WebWorkerSQLiteDriver

expect suspend fun getSessionCookie(): String

@Composable
actual fun LoginHandler(backStack: NavBackStack<NavKey>) {
    println("wasm login handler")
    LaunchedEffect(Unit) {
        val url =
            "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/authorize?client_id=ClassicWeb&scope=openid%20DSF%20email&response_mode=query&response_type=code&ui_locales=de&redirect_uri=https%3a%2f%2fwww.tucan.tu-darmstadt.de%2Fscripts%2Fmgrqispi.dll%3FAPPNAME%3DCampusNet%26PRGNAME%3DLOGINCHECK%26ARGUMENTS%3D-N000000000000001%2Cids_mode%26ids_mode%3DY"
        window.location.href = url
    }
}

actual suspend fun handleLogin(
    uri: Url,
    client: HttpClient,
    dataStore: DataStore<Settings?>,
    backStack: NavBackStack<NavKey>
) {
    println("traditional login")
    val sessionId: String =
        uri.parameters["ARGUMENTS"]!!.split(",", limit = 2)[0].substringAfter("-N")
    println(sessionId)
    val cookie = getSessionCookie()
    println(cookie)
    dataStore.updateData {
        Settings(null, sessionId, cookie, Clock.System.now(), GermanLocalizer)
    }
    backStack[backStack.size - 1] = StartNavKey
}

fun createDatabase(): AppDatabase {
    return Room.databaseBuilder<AppDatabase>("test.db")
        .fallbackToDestructiveMigration(true)
        .setDriver(fromWorker(createWorker()))
        .build()
}

@OptIn(ExperimentalWasmJsInterop::class)
fun createWorker() =
    Worker(js("""new URL("sqlite-web-worker/worker.js", import.meta.url)"""))

@OptIn(ExperimentalWasmJsInterop::class)
fun getSessionCookieInternal(): Promise<JsString> = js(
    """chrome.cookies.get({
  url: "https://www.tucan.tu-darmstadt.de/scripts",
  name: "cnsc",
}).then(function (c) { return c.value })"""
)

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    println(window.location)
    val uri = if (window.location.search.contains("APPNAME")) {
        window.location.href
    } else {
        null
    }
    val database = createDatabase();
    ComposeViewport {
        App(uri, database)
    }
}

actual fun createDataStore(): DataStore<Settings?> = DataStoreFactory.create(
    storage =
        WebStorage(
            serializer = SettingsSerializer,
            name = "tucanplus-config",
            storageType = WebStorageType.LOCAL
        ),
    migrations = listOf(),
)