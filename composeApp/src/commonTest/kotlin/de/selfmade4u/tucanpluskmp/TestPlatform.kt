package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import kotlinx.coroutines.test.TestResult
import okio.FileSystem
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// https://github.com/androidx/androidx/commit/47342d974c778259bd81f9af65d177d64ffda237
expect fun getTestDatabase(): AppDatabase

@OptIn(ExperimentalTestApi::class)
expect fun runMyComposeUiTest(
    effectContext: CoroutineContext = EmptyCoroutineContext,
    runTestContext: CoroutineContext = EmptyCoroutineContext,
    testTimeout: Duration = 60.seconds,
    block: suspend ComposeUiTest.() -> Unit,
): TestResult

expect val platformFileSystem: FileSystem