package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.platform.UriHandler
import androidx.room3.Room
import androidx.room3.RoomDatabase

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

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(
        name = "test",
    )
}