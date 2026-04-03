package de.selfmade4u.tucanpluskmp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import de.selfmade4u.tucanpluskmp.library.R
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import io.ktor.http.parameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.get
import org.koin.core.context.KoinContext
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun RequestNotificationPermission() {
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(
            android.Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        object : PermissionState {
            override val permission: String
                get() = "android.permission.POST_NOTIFICATIONS"
            override val status: PermissionStatus
                get() = PermissionStatus.Granted

            override fun launchPermissionRequest() {

            }
        }
    }

    if (notificationPermissionState.status.isGranted) {
        Text("Notification permission granted")
    } else {
        Column {
            val textToShow = if (notificationPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "The notification permission is important for this app. Please grant the permission."
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Notification permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Text(textToShow)
            Button(onClick = { notificationPermissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}

private const val GRADE_CHANGES_CHANNEL = "GRADE_CHANGES"

fun getNotifier(context: Context) = object : Notifier {

        override fun sendNotification() {
            val name = "Notenänderungen"
            val descriptionText = "Jegliche Änderungen deiner Noten"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(GRADE_CHANGES_CHANNEL, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val intent = Intent().apply {
                setClassName(context, "de.selfmade4u.tucanpluskmp.MainActivity")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(context, GRADE_CHANGES_CHANNEL)
                .setSmallIcon(R.drawable.grading_24px)
                .setContentTitle("Notenänderung")
                .setContentText("Überraschung!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    // ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array&lt;out String&gt;,
                    //                                        grantResults: IntArray)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    return@with
                }
                notify(42, builder.build())
            }
        }
}

fun retrieveNotifier(context: Context): Notifier {
    return getNotifier(context)
}

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

actual suspend fun handleLogin(
    uri: Url,
    client: HttpClient,
    dataStore: DataStore<Settings?>,
    backStack: NavBackStack<NavKey>
) {
    val code = uri.parameters["code"]!!
    println(code)
    var response = client.submitForm(
        url = "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/token",
        formParameters = parameters {
            append("client_id", "MobileApp")
            append("code", code)
            append("grant_type", "authorization_code")
            append("redirect_uri", "de.datenlotsen.campusnet.tuda:/oauth2redirect")
        }
    )
    println(response)
    val tokenResponse: TokenResponse = Json.decodeFromString(response.bodyAsText())
    println(tokenResponse)
    // now do the logincheck with that
    loginTucan(client, tokenResponse, dataStore)
    backStack[backStack.size - 1] = StartNavKey
}

actual val platformModule: Module = module {
    single<AppDatabase> {
        createDatabase(get())
    }
    single<DataStore<Settings?>> {
        createDataStore(get())
    }
    single<Notifier> {
        retrieveNotifier(get())
    }
}

fun createDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder<AppDatabase>(
            name = "test",
            context = context,
        )
        .fallbackToDestructiveMigration(true)
        .setDriver(AndroidSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Main)
        .build()
}

fun createDataStore(context: Context): DataStore<Settings?> {
    return DataStoreFactory.create(
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
    )
}

