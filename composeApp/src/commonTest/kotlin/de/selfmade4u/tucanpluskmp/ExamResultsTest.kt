package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.datastore.core.DataStore
import com.fleeksoft.ksoup.Ksoup
import de.selfmade4u.tucanpluskmp.connector.AuthenticatedHttpResponse
import de.selfmade4u.tucanpluskmp.connector.ExamResultsConnector
import de.selfmade4u.tucanpluskmp.connector.ExamResultsConnector.parse
import de.selfmade4u.tucanpluskmp.connector.fetchAuthenticated
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import org.koin.mp.KoinPlatform
import kotlin.test.Test

class ExamResultsTest {

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
        val result = ExamResultsConnector.extractRelevantPages(datastore).toList()
        println(result)
        val fetchPath = "src/commonTest/kotlin/de/selfmade4u/tucanpluskmp/GeneratedFetchExamResultsTest.kt".toPath()
        platformFileSystem.write(fetchPath) {
            writeUtf8("package de.selfmade4u.tucanpluskmp\nimport kotlin.test.Test\nimport de.selfmade4u.tucanpluskmp.ExamResultsTest.Companion.fetch\nclass GeneratedFetchExamResultsTest {")
            for (elem in result) {
                writeUtf8("\n   @Test fun test$elem() = fetch(\"$elem\")")
            }
            writeUtf8("\n}")
        }
        val path = "src/commonTest/kotlin/de/selfmade4u/tucanpluskmp/GeneratedExamResultsTest.kt".toPath()
        platformFileSystem.write(path) {
            writeUtf8("package de.selfmade4u.tucanpluskmp\nimport kotlin.test.Test\nimport de.selfmade4u.tucanpluskmp.ExamResultsTest.Companion.test\nclass GeneratedExamResultsTest {")
            for (elem in result) {
                writeUtf8("\n   @Test fun test$elem() = test(\"$elem\")")
            }
            writeUtf8("\n}")
        }
    }

    companion object {
        @OptIn(ExperimentalTestApi::class, DelicateCoroutinesApi::class)
        private val computed: Deferred<DataStore<Settings?>> = GlobalScope.async(start = CoroutineStart.LAZY) {
            var result: DataStore<Settings?>? = null;
            runMyComposeUiTest {
                onNode(hasTextExactly("Login")).performClick()
                // https://developer.android.com/develop/ui/compose/testing/apis
                // https://developer.android.com/develop/ui/compose/accessibility/semantics
                waitUntilExactlyOneExists(hasTextExactly("Logout"), timeoutMillis = 10_000)
                /*onNodeWithTag("button").performClick()
                onNodeWithTag("text").assertTextEquals("Compose")*/
                result = KoinPlatform.getKoin().get()
            }
            return@async result!!
        }

        fun fetch(value: String) = runTest {
            val credentials = computed.await().data.first()!!
            val response = fetchAuthenticated(
                credentials.sessionCookie, "https://www.tucan.tu-darmstadt.de/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXAMRESULTS&ARGUMENTS=-N${credentials.sessionId},-N000325,-N$value"
            ) as AuthenticatedHttpResponse.Success
            val content = response.response.bodyAsText()
            val document = Ksoup.parse(content)
            val path = "src/commonTest/resources/exam-results/$value.html".toPath()
            platformFileSystem.write(path) {
                writeUtf8(content)
            }
        }

        fun test(value: String) {
            val path = "src/commonTest/resources/exam-results/$value.html".toPath()
            val content = platformFileSystem.read(path) {
                readUtf8()
            }
            val document = Ksoup.parse(content)
            //println(document)
            check(document.nameIs("#root")) { document.normalName() }
            check(document.attributesSize() == 0) { document.attributes() }
            val node = Root(
                document,
                document.childNodes()
                    .filterNot(::shouldIgnore)
                    .toMutableList()
            )
            val result = node.parse("000325", "TODOsessionid", GermanLocalizer)
            println(result)
        }
    }
}