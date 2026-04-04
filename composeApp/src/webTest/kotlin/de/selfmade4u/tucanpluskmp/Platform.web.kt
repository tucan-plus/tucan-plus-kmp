package de.selfmade4u.tucanpluskmp

import androidx.room3.Room

// https://github.com/androidx/androidx/commit/47342d974c778259bd81f9af65d177d64ffda237
actual fun getTestDatabase(): AppDatabase {
    return Room.inMemoryDatabaseBuilder<AppDatabase>().setDriver(fromWorker(createWorker())).build()
}