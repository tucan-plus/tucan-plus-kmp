package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.platform.UriHandler

class JsPlatform : Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()

actual suspend fun getLoginUrl(uriHandler: UriHandler): String {
    return "https://localhost/?code=test"
}