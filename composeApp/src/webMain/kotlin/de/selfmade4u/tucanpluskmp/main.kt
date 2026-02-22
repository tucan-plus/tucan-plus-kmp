package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.core.okio.WebStorage
import androidx.datastore.core.okio.WebStorageType
import kotlinx.coroutines.CoroutineScope
import okio.FileSystem

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        App(null, createDataStore())
    }
}

fun createDataStore(): DataStore<TokenResponse?> = DataStoreFactory.create(
    storage =
        WebStorage(
            serializer = TokenResponseSerializer,
            name = "tucanplus-config",
            storageType = WebStorageType.LOCAL
        ),
    migrations = listOf(),
)