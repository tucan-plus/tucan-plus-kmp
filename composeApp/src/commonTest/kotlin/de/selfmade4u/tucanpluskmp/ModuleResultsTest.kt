package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.datastore.core.DataStore
import de.selfmade4u.tucanpluskmp.connector.ModuleResultsConnector
import kotlinx.coroutines.flow.toList
import okio.Path.Companion.toPath
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
        val path = "src/commonTest/kotlin/de/selfmade4u/tucanpluskmp/Test.kt".toPath()
        platformFileSystem.write(path) {
            writeUtf8("package de.selfmade4u.tucanpluskmp\nimport kotlin.test.Test\nclass Test {\n   fun test(value: String) {}")
            for (elem in result) {
                writeUtf8("\n   @Test fun test$elem() = test(\"$elem\")")
            }
            writeUtf8("\n}")
        }
    }
}