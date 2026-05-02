package de.selfmade4u

import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import org.junit.jupiter.api.Test

// https://github.com/JetBrains/intellij-community/tree/master/platform/testFramework/junit5/src/fixture
@TestApplication
class MyPluginTest2 {
    private val project = projectFixture()

    @Test
    fun test() {
        val p = project.get()
    }
}