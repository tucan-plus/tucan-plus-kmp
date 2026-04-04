package de.selfmade4u.tucanpluskmp

import android.app.ComponentCaller
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import de.selfmade4u.tucanpluskmp.connector.AuthenticatedResponse
import de.selfmade4u.tucanpluskmp.database.refreshModuleResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.decodeToImageVector
import org.jetbrains.compose.resources.getDrawableResourceBytes
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import org.koin.mp.KoinPlatform
import tucanpluskmp.composeapp.generated.resources.Res
import tucanpluskmp.composeapp.generated.resources.menu_24px
import java.util.concurrent.TimeUnit

class CoroutineDownloadWorker(
    val context: Context,
    params: WorkerParameters,
    private val dataStore: DataStore<Settings?>,
    private val database: AppDatabase,
    private val notifier: Notifier
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        println("DOING SOME WORK")
        try {
            when (val response = refreshModuleResults(notifier, dataStore, database)) {
                is AuthenticatedResponse.NetworkLikelyTooSlow<*> -> {
                    println("NETWORK TOO SLOW, RETRYING")
                    return Result.retry()
                }
                is AuthenticatedResponse.Success<*> -> {
                    println("DONE WITH SOME WORK $response")
                    return Result.success()
                }
                else -> {
                    println("FAILURE $response")
                    return Result.failure()
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            println("FAILURE IN SOME WORK")
            return Result.failure()
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        // TODO request permission for notifications

        // TODO don't do this blocking at startup
        /*WorkManager.getInstance(this).enqueueUniquePeriodicWork("periodic-update",
            ExistingPeriodicWorkPolicy.UPDATE, PeriodicWorkRequestBuilder<CoroutineDownloadWorker>(15,
                TimeUnit.MINUTES).setConstraints(constraints).build())
*/
        Log.e("TUCANPLUS", "onCreate $intent")

        setContent {
            App(intent.data.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("TUCANPLUS", "onResume $intent")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TUCANPLUS", "onDestroy $intent")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.e("TUCANPLUS", "ONNEWINTENT $intent")
    }
}
