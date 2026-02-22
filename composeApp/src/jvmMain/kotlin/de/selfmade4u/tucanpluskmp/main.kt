package de.selfmade4u.tucanpluskmp

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "tucanpluskmp",
    ) {
        App(dataStore = createDataStore())
    }
}