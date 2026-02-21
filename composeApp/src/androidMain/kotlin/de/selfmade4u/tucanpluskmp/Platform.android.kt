package de.selfmade4u.tucanpluskmp

import android.os.Build
import androidx.compose.ui.platform.UriHandler

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual suspend fun getLoginUrl(uriHandler: UriHandler): String {
    val url = "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/authorize?client_id=MobileApp&scope=openid+DSF+profile+offline_access&response_mode=query&response_type=code&ui_locales=de&redirect_uri=de.datenlotsen.campusnet.tuda:/oauth2redirect"
    uriHandler.openUri(url)

    return "Test"
}