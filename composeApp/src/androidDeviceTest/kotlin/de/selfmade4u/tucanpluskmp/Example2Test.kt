package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.runAndroidComposeUiTest
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilExactlyOneExists
import kotlin.test.Test


class Example2Test {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun myTest() = runAndroidComposeUiTest<MainActivity> {
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