package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.platform.UriHandler

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getLoginUrl(uriHandler: UriHandler)