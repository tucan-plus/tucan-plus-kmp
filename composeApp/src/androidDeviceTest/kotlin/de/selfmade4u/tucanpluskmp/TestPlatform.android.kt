package de.selfmade4u.tucanpluskmp

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.mp.KoinPlatform

// https://github.com/androidx/androidx/commit/47342d974c778259bd81f9af65d177d64ffda237
actual fun getTestDatabase(): AppDatabase {
    val koin = KoinPlatform.getKoin()
    val context: Context = koin.get()
    return Room.inMemoryDatabaseBuilder<AppDatabase>(context).setDriver(BundledSQLiteDriver()).build()
}