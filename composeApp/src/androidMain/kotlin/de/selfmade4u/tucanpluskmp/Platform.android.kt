package de.selfmade4u.tucanpluskmp

import android.content.Context
import android.os.Build
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import java.io.File

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun LoginHandler(backStack: NavBackStack<NavKey>) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val url =
            "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/authorize?client_id=MobileApp&scope=openid+DSF+profile+offline_access&response_mode=query&response_type=code&ui_locales=de&redirect_uri=de.datenlotsen.campusnet.tuda:/oauth2redirect"
        val intent = CustomTabsIntent.Builder()
            .build()
        intent.launchUrl(context, url.toUri())
    }
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
        .setDriver(BundledSQLiteDriver())
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