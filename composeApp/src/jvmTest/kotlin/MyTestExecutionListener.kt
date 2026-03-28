package de.selfmade4u.tucanpluskmp

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.reporting.FileEntry
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.TestPlan
import java.io.File
import kotlin.jvm.optionals.getOrNull

class MyTestExecutionListener : TestExecutionListener {

    override fun executionStarted(testIdentifier: TestIdentifier) {
        if (testIdentifier.isContainer)
            return;
        println("EXECUTION STARTED $testIdentifier")
        val agent = org.jacoco.agent.rt.RT.getAgent()
        agent.reset()
    }

    var currentTestPlan: TestPlan? = null

    override fun fileEntryPublished(testIdentifier: TestIdentifier, file: FileEntry) {
        println("fileEntryPublished $testIdentifier $file")
    }

    override fun reportingEntryPublished(testIdentifier: TestIdentifier, entry: ReportEntry) {
        println("reportingEntryPublished $testIdentifier $entry")
    }

    override fun testPlanExecutionStarted(testPlan: TestPlan) {
        currentTestPlan = testPlan
        println("testPlanExecutionStarted $testPlan")
    }

    override fun testPlanExecutionFinished(testPlan: TestPlan) {
        println("testPlanExecutionFinished $testPlan")
    }

    override fun dynamicTestRegistered(testIdentifier: TestIdentifier) {
        println("dynamicTestRegistered $testIdentifier")
    }

    override fun executionFinished(
        testIdentifier: TestIdentifier,
        testExecutionResult: TestExecutionResult
    ) {
        if (testIdentifier.isContainer)
            return;

        val name = currentTestPlan!!.getParent(testIdentifier).getOrNull()!!.displayName + "." + testIdentifier.displayName
        val slashName = currentTestPlan!!.getParent(testIdentifier).getOrNull()!!.displayName + "/" + testIdentifier.displayName

        val agent = org.jacoco.agent.rt.RT.getAgent()
        val executionData = agent.getExecutionData(true)
        File("./build/jacoco/${name}.metadata.json").writeText("""
            { "testInfo": 
              {
                "uniformPath": "$slashName",
                "result": "${when (testExecutionResult.status) {
            TestExecutionResult.Status.SUCCESSFUL -> "PASSED"
            TestExecutionResult.Status.ABORTED -> "ERROR"
            TestExecutionResult.Status.FAILED -> "FAILURE"
        }}",
                "duration": 1 
              }
             }
        """.trimIndent())
        File("./build/jacoco/${name}.exec").writeBytes(executionData)
        println("DUMPED")
    }

    override fun executionSkipped(testIdentifier: TestIdentifier, reason: String) {

    }
}