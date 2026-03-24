package de.selfmade4u.tucanpluskmp

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import java.io.File
import java.nio.file.Paths

class MyTestExecutionListener : TestExecutionListener {

    override fun executionStarted(testIdentifier: TestIdentifier) {
        println("EXECUTION STARTED")
    }

    override fun executionFinished(
        testIdentifier: TestIdentifier,
        testExecutionResult: TestExecutionResult
    ) {
        val agent = org.jacoco.agent.rt.RT.getAgent()
        val executionData = agent.getExecutionData(true)
        File("./build/jacoco/${testIdentifier.displayName}.exec").writeBytes(executionData)
        println("DUMPED")
    }
}