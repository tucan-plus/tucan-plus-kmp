package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.platform.UriHandler

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

@OptIn(ExperimentalWasmJsInterop::class)
fun registerProtocolHandler(): Unit = js(
    """{
        navigator.registerProtocolHandler("web+dedatenlotsencampusnettuda", "http://localhost:8080/?to=%s");
}"""
)

actual suspend fun getLoginUrl(uriHandler: UriHandler): String {
    return "https://localhost/?code=test"
}