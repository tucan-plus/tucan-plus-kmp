package de.selfmade4u.tucanpluskmp

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import java.io.File

class MyTestExecutionListener : TestExecutionListener {

    override fun executionStarted(testIdentifier: TestIdentifier) {
        if (testIdentifier.isContainer)
            return;
        println("EXECUTION STARTED $testIdentifier")
        val agent = org.jacoco.agent.rt.RT.getAgent()
        agent.reset()
    }

    override fun executionFinished(
        testIdentifier: TestIdentifier,
        testExecutionResult: TestExecutionResult
    ) {
        if (testIdentifier.isContainer)
            return;

        val agent = org.jacoco.agent.rt.RT.getAgent()
        val executionData = agent.getExecutionData(true)
        File("./build/jacoco/${testIdentifier.displayName}.metadata.json").writeText("""
            { "testInfo": 
              {
                "result": "PASSED",
                "duration": 1 
              }
             }
        """.trimIndent())
        File("./build/jacoco/${testIdentifier.displayName}.exec").writeBytes(executionData)
        println("DUMPED")
    }

    override fun executionSkipped(testIdentifier: TestIdentifier, reason: String) {

    }
}