package de.selfmade4u.tucanpluskmp

import android.content.Context
import android.os.Bundle
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import java.util.concurrent.TimeUnit

class CoroutineDownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        println("DOING SOME WORK")
        return Result.success()
    }
}

object SettingsDataStore {

    // Use a shared dedicated scope for the DataStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // backing property
    @Volatile
    private var dataStoreInstance: DataStore<Settings?>? = null

    // backing property
    @Volatile
    private var databaseInstance: AppDatabase? = null

    /**
     * Returns the single DataStore instance for all platforms.
     */
    fun getDataStore(filePathProvider: () -> Path): DataStore<Settings?> {
        return dataStoreInstance ?: synchronized(this) {
            dataStoreInstance ?: createDataStore(filePathProvider).also { dataStoreInstance = it }
        }
    }

    // context is per application so we can initialize there?
    fun getDatabase(context: Context): AppDatabase {
        return databaseInstance ?: synchronized(this) {
            databaseInstance ?: getRoomDatabase(getDatabaseBuilder(context)).also { databaseInstance = it }
        }
    }

    private fun createDataStore(filePathProvider: () -> Path): DataStore<Settings?> {
        return DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                serializer = SettingsSerializer,
                producePath = filePathProvider
            ),
            corruptionHandler = ReplaceFileCorruptionHandler { null },
            migrations = emptyList(),
            scope = scope
        )
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

        // TODO don't do this blocking at startup
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("periodic-update",
            ExistingPeriodicWorkPolicy.UPDATE, PeriodicWorkRequestBuilder<CoroutineDownloadWorker>(15,
                TimeUnit.MINUTES).setConstraints(constraints).build())

        setContent {
            MainApp(intent.data.toString())
        }
    }
}

@Composable
fun MainApp(url: String) {
    val context = LocalContext.current
    val dataStore = SettingsDataStore.getDataStore {
        context.filesDir.resolve("tucanplus-config.json").toOkioPath()
    }
    val database = SettingsDataStore.getDatabase(context)
    App(url, dataStore, database)
}

@Preview
@Composable
fun AppAndroidPreview() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    App(null, createDataStore(context, lifecycleOwner.lifecycleScope), null!!)
}