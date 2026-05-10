package de.selfmade4u

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.pom.java.LanguageLevel
import com.intellij.testFramework.IdeaTestUtil
import com.intellij.testFramework.common.ThreadLeakTracker
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5
import com.intellij.testFramework.runInEdtAndWait
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
        val main = fixture.copyFileToProject("main1.kt", "main.kt");
        runInEdtAndWait {
            fixture.openFileInEditor(htmlParsing)
            fixture.doHighlighting(HighlightSeverity.ERROR).let { assert(it == listOf<HighlightInfo>(), { it }) };
            fixture.openFileInEditor(main)
            fixture.doHighlighting(HighlightSeverity.ERROR).let { assert(it == listOf<HighlightInfo>(), { it }) }; // TODO expect error
        }
    }

    @Test
    fun testHtmlParsingA() {
        val htmlParsing = fixture.copyFileToProject("HtmlParsing.kt")
        fixture.copyDirectoryToProject("simple_html", "html");
        val main = fixture.copyFileToProject("a.kt", "a.kt");
        runInEdtAndWait {
            fixture.openFileInEditor(htmlParsing)
            fixture.doHighlighting(HighlightSeverity.ERROR).let { assert(it == listOf<HighlightInfo>(), { it }) };
            fixture.openFileInEditor(main)
            fixture.doHighlighting(HighlightSeverity.ERROR).let { assert(it == listOf<HighlightInfo>(), { it }) };
        }
    }

    @Test
    fun testHtmlParsingB() {
        val htmlParsing = fixture.copyFileToProject("HtmlParsing.kt")
        fixture.copyDirectoryToProject("simple_html", "html");
        val main = fixture.copyFileToProject("b.kt", "b.kt");
        runInEdtAndWait {
            fixture.openFileInEditor(htmlParsing)
            fixture.doHighlighting(HighlightSeverity.ERROR).let { assert(it == listOf<HighlightInfo>(), { it }) };
            fixture.openFileInEditor(main)
            fixture.doHighlighting(HighlightSeverity.ERROR).let { assert(it == listOf<HighlightInfo>(), { it }) };
        }
    }

    @Test
    fun testHtmlParsingC() {
        val htmlParsing = fixture.copyFileToProject("HtmlParsing.kt")
        fixture.copyDirectoryToProject("simple_html", "html");
        val main = fixture.copyFileToProject("c.kt", "c.kt");
        runInEdtAndWait {
            fixture.openFileInEditor(htmlParsing)
            fixture.doHighlighting(HighlightSeverity.ERROR).let { assert(it == listOf<HighlightInfo>(), { it }) };
            fixture.openFileInEditor(main)
            fixture.doHighlighting(HighlightSeverity.ERROR).let { assert(it == listOf<HighlightInfo>(), { it }) };
        }
    }

    // https://plugins.jetbrains.com/docs/intellij/code-intentions-preview.html#testing
    @Test
    fun testHtmlParsingQuickFix() {
        fixture.copyFileToProject("HtmlParsing.kt")
        fixture.copyDirectoryToProject("simple_html", "html");
        val main = fixture.copyFileToProject("main1.kt", "main.kt")
        runInEdtAndWait {
            fixture.openFileInEditor(main)
            var quickFixes = fixture.getAllQuickFixes("main.kt")
            println(fixture.doHighlighting(HighlightSeverity.ERROR).first())
            check(fixture.doHighlighting(HighlightSeverity.ERROR).first().findRegisteredQuickFix { _, _ -> true } != null)
            fixture.checkPreviewAndLaunchAction(quickFixes.single().asIntention())
            fixture.checkResultByFile("main2.kt")
            var main = fixture.copyFileToProject("main2.kt", "main.kt")
            fixture.openFileInEditor(main)
            quickFixes = fixture.getAllQuickFixes()
            println(fixture.doHighlighting(HighlightSeverity.ERROR).first())
            check(fixture.doHighlighting(HighlightSeverity.ERROR).first().findRegisteredQuickFix { _, _ -> true } != null)
            fixture.checkPreviewAndLaunchAction(quickFixes.single().asIntention())
            fixture.checkResultByFile("main3.kt")
            main = fixture.copyFileToProject("main3.kt", "main.kt")
            fixture.openFileInEditor(main)
            quickFixes = fixture.getAllQuickFixes()
            fixture.checkPreviewAndLaunchAction(quickFixes.single().asIntention())
            quickFixes = fixture.getAllQuickFixes()
            fixture.checkPreviewAndLaunchAction(quickFixes.single().asIntention())
            quickFixes = fixture.getAllQuickFixes()
            fixture.checkPreviewAndLaunchAction(quickFixes.single().asIntention())
            quickFixes = fixture.getAllQuickFixes()
            fixture.checkPreviewAndLaunchAction(quickFixes.single().asIntention())
            fixture.checkResultByFile("main3.kt")

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
