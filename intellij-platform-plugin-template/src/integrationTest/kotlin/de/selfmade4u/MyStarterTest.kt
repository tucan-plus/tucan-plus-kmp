package de.selfmade4u

import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.project.NoProject
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.Test

// https://plugins.jetbrains.com/docs/intellij/integration-tests-intro.html#creating-the-first-integration-test
class MyStarterTest {
    @Test
    fun simpleTestWithoutProject() {
        Starter.newContext(
            testName = "testExample",
            TestCase(IdeProductProvider.IU, projectInfo = NoProject).withVersion("2026.1")
        ).apply {
        }.runIdeWithDriver().useDriverAndCloseIde {
        }
    }
}