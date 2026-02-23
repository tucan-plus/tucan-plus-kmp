package de.selfmade4u.tucanpluskmp

import android.content.Context
import android.os.Build
import androidx.compose.ui.platform.UriHandler
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import java.io.File

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual suspend fun getLoginUrl(uriHandler: UriHandler): String {
    val url = "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/authorize?client_id=MobileApp&scope=openid+DSF+profile+offline_access&response_mode=query&response_type=code&ui_locales=de&redirect_uri=de.datenlotsen.campusnet.tuda:/oauth2redirect"
    uriHandler.openUri(url)

    return "Test"
}

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(
        name = "test",
        context = context,
    )
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(AndroidSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Main)
        .build()
}

fun createDataStore(context: Context, scope: CoroutineScope): DataStore<Settings?> = DataStoreFactory.create(
    storage =
        OkioStorage(
            FileSystem.SYSTEM, SettingsSerializer,
            producePath = {
                val file = context.filesDir.resolve("tucanplus-config.json")
                file.toOkioPath()
            }
        ),
    migrations = listOf(),
    corruptionHandler = ReplaceFileCorruptionHandler { ex ->
        null
    },
    scope = scope
)