package de.selfmade4u

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.pom.java.LanguageLevel
import com.intellij.testFramework.EdtTestUtil
import com.intellij.testFramework.IdeaTestUtil
import com.intellij.testFramework.RunsInEdt
import com.intellij.testFramework.VfsTestUtil
import com.intellij.testFramework.common.ThreadLeakTracker
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5
import com.intellij.testFramework.runInEdtAndWait
import com.intellij.testFramework.utils.editor.commitToPsi
import com.intellij.testFramework.utils.editor.saveToDisk
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// https://github.com/JetBrains/intellij-community/blob/master/plugins/kotlin/test-framework/test/org/jetbrains/kotlin/idea/test/kmp/KMPNativeLinuxProjectDescriptor.kt
// https://github.com/JetBrains/intellij-community/blob/4192b57a80be69fb8901c5bbc3adf393285c432d/plugins/kotlin/test-framework/test/org/jetbrains/kotlin/idea/test/KotlinLightProjectDescriptor.java#L19

// https://github.com/JetBrains/kotlin/blob/master/analysis/stubs/tests/org/jetbrains/kotlin/analysis/decompiler/BuiltinsDecompilerTest.kt

// https://github.com/JetBrains/intellij-structural-search-for-kotlin/blob/master/src/test/kotlin/com/jetbrains/kotlin/structuralsearch/KotlinLightProjectDescriptor.kt

// https://plugins.jetbrains.com/docs/intellij/intellij-platform-extension-point-list.html
// https://github.com/JetBrains/intellij-community/blob/master/platform/analysis-api/src/com/intellij/codeInspection/GlobalInspectionTool.java

// https://github.com/JetBrains/intellij-community/tree/master/platform/testFramework/junit5/test/showcase
class MyPluginTest : LightJavaCodeInsightFixtureTestCase5(DefaultLightProjectDescriptor({ IdeaTestUtil.getMockJdk(LanguageLevel.HIGHEST) }, listOf("org.jetbrains.kotlin:kotlin-stdlib:2.4.0-Beta2"))) {

    // https://github.com/JetBrains/JetBrainsRuntime/blob/2a24ff85457db452a7499acfb0f16a98f446d4d9/src/java.desktop/unix/classes/sun/awt/wl/WLKeyboard.java#L40
    @Test
    fun testHtmlParsing() {
        val htmlParsing = fixture.copyFileToProject("HtmlParsing.kt")
        fixture.copyDirectoryToProject("simple_html", "html");
        val main = fixture.copyFileToProject("main_annotated.kt", "main.kt");
        runInEdtAndWait {
            fixture.openFileInEditor(htmlParsing)
            fixture.checkHighlighting()
            fixture.openFileInEditor(main)
            fixture.testHighlighting()
        }
        //fixture.testHighlighting("html/page1.html")
        //fixture.testHighlighting("html/page2.html")
    }

    // https://plugins.jetbrains.com/docs/intellij/code-intentions-preview.html#testing
    @Test
    fun testHtmlParsingQuickFix() {
        fixture.copyFileToProject("HtmlParsing.kt")
        fixture.copyDirectoryToProject("simple_html", "html");
        val main = fixture.copyFileToProject("main_before.kt", "main.kt")
        runInEdtAndWait {
            fixture.openFileInEditor(main)
            var quickFixes = fixture.getAllQuickFixes("main.kt")
            fixture.checkPreviewAndLaunchAction(quickFixes.single().asIntention())
            fixture.checkResultByFile("main_after.kt")
            val main = fixture.copyFileToProject("main_after.kt", "main.kt")
            fixture.openFileInEditor(main)
            quickFixes = fixture.getAllQuickFixes("main.kt")
            println(quickFixes)
            fixture.checkPreviewAndLaunchAction(quickFixes.single().asIntention())
            fixture.checkResultByFile("main_after.kt")
        }
    }

    override fun getTestDataPath() = "src/test/testData"

    @BeforeEach
    fun beforeAll() {
        @Suppress("UnstableApiUsage")
        ThreadLeakTracker.longRunningThreadCreated(
            ApplicationManager.getApplication(),
            "WLKeyboard.KeyRepeatManager",
            "AWT-Wayland"
        )
    }
}
