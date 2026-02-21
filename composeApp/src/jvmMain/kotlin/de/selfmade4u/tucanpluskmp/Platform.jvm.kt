package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.platform.UriHandler
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

actual fun getLoginUrl(uriHandler: UriHandler): String {
    val userHome = System.getProperty("user.home")
    Files.writeString(Paths.get(userHome, ".local", "share", "applications", "tucanplus.desktop"), "[Desktop Entry]\n" +
            "Exec=printf %u | socat UNIX-CONNECT:/run/user/1000/tucanplus -\n" +
            "Type=Application\n" +
            "Name=tucanplus.desktop\n" +
            "MimeType=x-scheme-handler/de.datenlotsen.campusnet.tuda\n")
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
    return newContent
}