import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.datastore.core.DataStore
import de.selfmade4u.tucanpluskmp.App
import de.selfmade4u.tucanpluskmp.Settings
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

class ExampleTest2 {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun myTest() = runComposeUiTest {
        setContent {
            App(null)
        }
        // https://developer.android.com/develop/ui/compose/testing/apis
        // https://developer.android.com/develop/ui/compose/accessibility/semantics
        waitUntilExactlyOneExists(hasText("Logged in: true"), timeoutMillis = 10_000)
        /*onNodeWithTag("button").performClick()
        onNodeWithTag("text").assertTextEquals("Compose")*/
    }
}