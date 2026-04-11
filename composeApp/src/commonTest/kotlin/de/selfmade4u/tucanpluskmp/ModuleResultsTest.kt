package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.datastore.core.DataStore
import de.selfmade4u.tucanpluskmp.connector.ModuleResultsConnector
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.flow.toList
import org.koin.core.Koin
import org.koin.mp.KoinPlatform
import kotlin.test.Test

class ModuleResultsTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun extractRelevantPages() = runMyComposeUiTest {
        onNode(hasTextExactly("Login")).performClick()
        // https://developer.android.com/develop/ui/compose/testing/apis
        // https://developer.android.com/develop/ui/compose/accessibility/semantics
        waitUntilExactlyOneExists(hasTextExactly("Logout"), timeoutMillis = 10_000)
        /*onNodeWithTag("button").performClick()
        onNodeWithTag("text").assertTextEquals("Compose")*/
        val datastore: DataStore<Settings?> = KoinPlatform.getKoin().get()
        val result = ModuleResultsConnector.extractRelevantPages(datastore).toList()
        println(result)
    }
}