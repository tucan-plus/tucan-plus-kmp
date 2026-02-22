package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.platform.UriHandler
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.FileStorage
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

actual suspend fun getLoginUrl(uriHandler: UriHandler): String {
    return withContext(Dispatchers.IO) {
        println("desktop getloginurl")
        val userHome = System.getProperty("user.home")
        Files.writeString(
            Paths.get(userHome, ".local", "share", "applications", "tucanplus.desktop"),
            "[Desktop Entry]\n" +
                    "Exec=sh -c 'printf \"%s\" \"$1\" | socat UNIX-CONNECT:/run/user/1000/tucanplus -' _ %u\n" +
                    "Type=Application\n" +
                    "Name=tucanplus.desktop\n" +
                    "MimeType=x-scheme-handler/de.datenlotsen.campusnet.tuda\n"
        )
        Runtime.getRuntime().exec(arrayOf("xdg-mime", "default", "tucanplus.desktop", "x-scheme-handler/de.datenlotsen.campusnet.tuda"))
        val socketPath = Path.of(System.getenv("XDG_RUNTIME_DIR"), "tucanplus")
        val address = UnixDomainSocketAddress.of(socketPath)
        Files.deleteIfExists(socketPath);
        val serverChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX)
        serverChannel.bind(address)
        println("before open")
        val url = "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/authorize?client_id=MobileApp&scope=openid+DSF+profile+offline_access&response_mode=query&response_type=code&ui_locales=de&redirect_uri=de.datenlotsen.campusnet.tuda:/oauth2redirect"
        uriHandler.openUri(url)
        println("waiting")
        val channel = serverChannel.accept()
        println("accepted")
        val buffer = ByteBuffer.allocate(4096)
        val bytes = channel.read(buffer)
        println("bytes $bytes")
        val newContent: String = Charsets.UTF_8.decode(buffer.slice(0, bytes)).toString()
        println(newContent)
        return@withContext newContent
    }
}

fun createDataStore(): DataStore<TokenResponse> = DataStoreFactory.create(
    storage =
        OkioStorage(
            FileSystem.SYSTEM, TokenResponseSerializer,
            producePath = {
                val file = File(System.getProperty("java.io.tmpdir"), "tucanplus-config.json")
                file.toOkioPath()
            }
        ),
)