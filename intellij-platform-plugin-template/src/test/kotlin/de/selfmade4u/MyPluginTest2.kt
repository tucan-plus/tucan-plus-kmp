package de.selfmade4u

import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.javaCodeInsightFixture
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.moduleFixture
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.testFramework.junit5.fixture.tempPathFixture
import org.junit.jupiter.api.Test

// https://github.com/JetBrains/intellij-community/tree/master/platform/testFramework/junit5/src/fixture
@TestApplication
@TestDataPath("\$PROJECT_ROOT/community/my-plugin/tests/testData/myFeature")
class MyPluginTest2 {
    private val tempDir = tempPathFixture()
    private val project = projectFixture(tempDir, openAfterCreation = true)
    private val module = project.moduleFixture(tempDir, addPathToSourceRoot = true)
    private val fixture by javaCodeInsightFixture(project, tempDir)

    @Test
    fun testSomething() {
        PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()
    }
}