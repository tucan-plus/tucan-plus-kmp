package de.selfmade4u.tucanpluskmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.datastore.core.DataStore
import androidx.room3.Room
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlin.test.Test

object InMemoryDataStore : DataStore<Settings?> {
    private val _data = MutableStateFlow<Settings?>(null)
    override val data: Flow<Settings?> = _data.asStateFlow()

    override suspend fun updateData(transform: suspend (t: Settings?) -> Settings?): Settings? {
        return _data.updateAndGet { transform(it) }
    }
}

class ExampleTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun myTest() = runMyComposeUiTest {
        /*setContent {
            App(null)
        }*/
        // https://developer.android.com/develop/ui/compose/testing/apis
        // https://developer.android.com/develop/ui/compose/accessibility/semantics
        waitUntilExactlyOneExists(hasText("Logged in: true"), timeoutMillis = 10_000)
        /*onNodeWithTag("button").performClick()
        onNodeWithTag("text").assertTextEquals("Compose")*/
    }
}