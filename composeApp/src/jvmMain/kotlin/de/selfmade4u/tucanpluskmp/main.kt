package de.selfmade4u.tucanpluskmp

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin

fun main() {
    initKoin {

    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "tucanpluskmp",
        ) {
            App(null)
        }
    }
}