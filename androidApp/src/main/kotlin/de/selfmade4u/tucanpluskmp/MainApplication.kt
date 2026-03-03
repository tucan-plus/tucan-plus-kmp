package de.selfmade4u.tucanpluskmp

import android.app.Application
import androidx.work.Configuration

class MainApplication() : Application(), Configuration.Provider {
    override val workManagerConfiguration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
}