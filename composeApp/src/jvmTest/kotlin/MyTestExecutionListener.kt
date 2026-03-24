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

        // we should use the jacoco reporting stuff to get the uniform path.

        val agent = org.jacoco.agent.rt.RT.getAgent()
        val executionData = agent.getExecutionData(true)
        val folder = "./build/jacoco/${testIdentifier.displayName}/"
        File("$folder/JACOCO").mkdirs()
        File("$folder/metadata.json").writeText("""
            { "testInfo": 
              {
                "uniformPath": "null", 
                "result": "PASSED",
                "duration": 1 
              }
             }
        """.trimIndent())
        // TODO teamscale needs the xml files
        File("$folder/JACOCO/jvmTest.exec").writeBytes(executionData)
        println("DUMPED")
    }

    override fun executionSkipped(testIdentifier: TestIdentifier, reason: String) {

    }
}