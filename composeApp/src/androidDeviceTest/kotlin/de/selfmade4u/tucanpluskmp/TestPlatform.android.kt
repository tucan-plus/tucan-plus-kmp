package de.selfmade4u.tucanpluskmp

import android.content.Context
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runAndroidComposeUiTest
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.test.TestResult
import okio.FileSystem
import org.koin.mp.KoinPlatform
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// https://github.com/androidx/androidx/commit/47342d974c778259bd81f9af65d177d64ffda237
actual fun getTestDatabase(): AppDatabase {
    val koin = KoinPlatform.getKoin()
    val context: Context = koin.get()
    return Room.inMemoryDatabaseBuilder<AppDatabase>(context).setDriver(BundledSQLiteDriver()).build()
}

@OptIn(ExperimentalTestApi::class)
actual fun runMyComposeUiTest(
    effectContext: CoroutineContext,
    runTestContext: CoroutineContext,
    testTimeout: Duration,
    block: suspend ComposeUiTest.() -> Unit,
): TestResult = runAndroidComposeUiTest<MainActivity>(effectContext, runTestContext, testTimeout, block)

actual val platformFileSystem: FileSystem
    get() = FileSystem.SYSTEM