package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.test.TestResult
import org.koin.compose.KoinApplication
import org.koin.core.context.stopKoin
import org.koin.dsl.includes
import org.koin.dsl.koinConfiguration
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

// https://github.com/androidx/androidx/commit/47342d974c778259bd81f9af65d177d64ffda237
actual fun getTestDatabase(): AppDatabase {
    return Room.inMemoryDatabaseBuilder<AppDatabase>().setDriver(BundledSQLiteDriver()).build()
}

@OptIn(ExperimentalTestApi::class)
actual fun runMyComposeUiTest(
    effectContext: CoroutineContext,
    runTestContext: CoroutineContext,
    testTimeout: Duration,
    block: suspend ComposeUiTest.() -> Unit,
): TestResult = runComposeUiTest (effectContext, runTestContext, testTimeout) {
    setContent {
        KoinApplication(
            configuration = koinConfiguration {
                modules(
                    platformModule,
                )
            }
        ) {
            App(null)
        }
    }
    block()
}
