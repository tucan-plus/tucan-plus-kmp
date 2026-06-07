package de.selfmade4u

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.java.LanguageLevel
import com.intellij.testFramework.ExpectedHighlightingData
import com.intellij.testFramework.IdeaTestUtil
import com.intellij.testFramework.common.ThreadLeakTracker
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl
import com.intellij.testFramework.runInEdtAndWait
import org.jetbrains.kotlin.idea.base.highlighting.dsl.DslStyleUtils
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

    // https://plugins.jetbrains.com/docs/intellij/code-intentions-preview.html#testing
    @Test
    fun testHtmlParsingQuickFix() {
        fixture.copyFileToProject("HtmlParsing.kt")
        fixture.copyDirectoryToProject("simple_html", "html")
        checkHighlighting(fixture.copyFileToProject("main1.kt", "main.kt"))
        checkHighlighting(fixture.copyFileToProject("main2.kt", "main.kt"))
    }

    private fun checkHighlighting(main: VirtualFile) {
        runInEdtAndWait {
            // https://github.com/JetBrains/intellij-community/blob/037ff732d0aecb30622e490f3aff5eb46c79691b/plugins/kotlin/gradle/gradle-java/tests.shared/test/org/jetbrains/kotlin/gradle/K2GradleCodeInsightTestCase.kt#L63
            fixture.openFileInEditor(main)
            val data = ExpectedHighlightingData(fixture.editor.document, true, true, false, false)
            // manually register DSL_TYPE_SEVERITY to ignore it
            val severity = DslStyleUtils.typeById(1).getSeverity(null)
            data.registerHighlightingType(severity.name, ExpectedHighlightingData.ExpectedHighlightingSet(severity, false, false))
            data.init()
            (fixture as CodeInsightTestFixtureImpl).collectAndCheckHighlighting(data)
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
