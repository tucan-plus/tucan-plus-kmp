package de.selfmade4u

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5
import org.junit.jupiter.api.Test

// https://plugins.jetbrains.com/docs/intellij/intellij-platform-extension-point-list.html
// https://github.com/JetBrains/intellij-community/blob/master/platform/analysis-api/src/com/intellij/codeInspection/GlobalInspectionTool.java

// https://github.com/JetBrains/intellij-community/tree/master/platform/testFramework/junit5/test/showcase
class MyPluginTest : LightJavaCodeInsightFixtureTestCase5() {

    @Test
    fun testFindSimilarities() {
        //Registry.get("platform.random.idempotence.check.rate").setValue(1, getTestRootDisposable())

        val directory = fixture.copyDirectoryToProject("", "");
        val project = fixture.project
        //KotlinFacet.get(module)?.configuration?.settings?.compilerSettings.
        fixture.testHighlighting("HtmlParsing.kt")
        fixture.testHighlighting("main.kt")
    }

    override fun getTestDataPath() = "src/test/testData/simple"
}
