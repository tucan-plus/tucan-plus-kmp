package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.core.okio.WebStorage
import androidx.datastore.core.okio.WebStorageType
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okio.FileSystem

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(SQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Main)
        .build()
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val builder = getDatabaseBuilder()
    val db = getRoomDatabase(builder)
    ComposeViewport {
        App(null, createDataStore())
    }
}

fun createDataStore(): DataStore<Settings?> = DataStoreFactory.create(
    storage =
        WebStorage(
            serializer = SettingsSerializer,
            name = "tucanplus-config",
            storageType = WebStorageType.LOCAL
        ),
    migrations = listOf(),
)