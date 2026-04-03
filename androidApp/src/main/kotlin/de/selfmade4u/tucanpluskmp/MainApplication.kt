package de.selfmade4u.tucanpluskmp

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.work.Configuration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.plugin.module.dsl.worker

class MainApplication : Application(), Configuration.Provider {
    override val workManagerConfiguration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
            workManagerFactory()
            module {
                worker<CoroutineDownloadWorker>()
            }
        }
    }
}