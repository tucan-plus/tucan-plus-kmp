package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.test.TestResult
import okio.FileSystem
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// https://github.com/androidx/androidx/commit/47342d974c778259bd81f9af65d177d64ffda237
expect fun getTestDatabase(): AppDatabase

object InMemoryDataStore : DataStore<Settings?> {
    private val _data = MutableStateFlow<Settings?>(null)
    override val data: Flow<Settings?> = _data.asStateFlow()

    override suspend fun updateData(transform: suspend (t: Settings?) -> Settings?): Settings? {
        return _data.updateAndGet { transform(it) }
    }
}

@OptIn(ExperimentalTestApi::class)
expect fun runMyComposeUiTest(
    effectContext: CoroutineContext = EmptyCoroutineContext,
    runTestContext: CoroutineContext = EmptyCoroutineContext,
    testTimeout: Duration = 60.seconds,
    block: suspend ComposeUiTest.() -> Unit,
): TestResult

expect val platformFileSystem: FileSystem