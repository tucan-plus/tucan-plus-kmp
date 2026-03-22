package de.selfmade4u.tucanpluskmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.FileStorage
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import io.ktor.http.parameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.StandardProtocolFamily
import java.net.UnixDomainSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ServerSocketChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

@Composable
actual fun RequestNotificationPermission() {

}

@Composable
actual fun retrieveNotifier(): Notifier {
    return object : Notifier {
        override fun sendNotification() {
            println("sending notification")
        }
    }
}

// TODO FIXME duplication with android
actual suspend fun handleLogin(
    uri: Url,
    client: HttpClient,
    dataStore: DataStore<Settings?>,
    backStack: NavBackStack<NavKey>
) {
    val code = uri.parameters["code"]!!
    println(code)
    var response = client.submitForm(
        url = "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/token",
        formParameters = parameters {
            append("client_id", "MobileApp")
            append("code", code)
            append("grant_type", "authorization_code")
            append("redirect_uri", "de.datenlotsen.campusnet.tuda:/oauth2redirect")
        }
    )
    println(response)
    val tokenResponse: TokenResponse = Json.decodeFromString(response.bodyAsText())
    println(tokenResponse)
    // now do the logincheck with that
    loginTucan(client, tokenResponse, dataStore)
    backStack[backStack.size - 1] = StartNavKey
}

@Composable
actual fun LoginHandler(backStack: NavBackStack<NavKey>) {
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            println("desktop getloginurl")
            val userHome = System.getProperty("user.home")
            // for the native app
            Files.writeString(
                Paths.get(userHome, ".local", "share", "applications", "tucanplus.desktop"),
                "[Desktop Entry]\n" +
                        "Exec=sh -c 'printf \"%s\" \"$1\" | socat UNIX-CONNECT:/run/user/1000/tucanplus -' _ %u\n" +
                        "Type=Application\n" +
                        "Name=tucanplus.desktop\n" +
                        "MimeType=x-scheme-handler/de.datenlotsen.campusnet.tuda\n"
            )
            Runtime.getRuntime().exec(
                arrayOf(
                    "xdg-mime",
                    "default",
                    "tucanplus.desktop",
                    "x-scheme-handler/de.datenlotsen.campusnet.tuda"
                )
            )
            val socketPath = Path.of(System.getenv("XDG_RUNTIME_DIR"), "tucanplus")
            val address = UnixDomainSocketAddress.of(socketPath)
            Files.deleteIfExists(socketPath);
            val serverChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX)
            serverChannel.bind(address)
            println("before open")
            val url =
                "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/authorize?client_id=MobileApp&scope=openid+DSF+profile+offline_access&response_mode=query&response_type=code&ui_locales=de&redirect_uri=de.datenlotsen.campusnet.tuda:/oauth2redirect"
            uriHandler.openUri(url)
            println("waiting")
            val channel = serverChannel.accept()
            println("accepted")
            val buffer = ByteBuffer.allocate(4096)
            val bytes = channel.read(buffer)
            println("bytes $bytes")
            val newContent: String = Charsets.UTF_8.decode(buffer.slice(0, bytes)).toString()
            println(newContent)
            backStack.add(AfterLoginNavKey(newContent))
        }
    }
}

fun createDataStore(): DataStore<Settings?> = DataStoreFactory.create(
    storage =
        OkioStorage(
            FileSystem.SYSTEM, SettingsSerializer,
            producePath = {
                val file = File(System.getProperty("java.io.tmpdir"), "tucanplus-config.json")
                file.toOkioPath()
            }
        ),
)

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(
        name = "test.db",
    )
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .fallbackToDestructiveMigration(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Main)
        .build()
}