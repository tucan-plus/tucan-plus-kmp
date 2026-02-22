package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController { App(intent.data, dataStore) }