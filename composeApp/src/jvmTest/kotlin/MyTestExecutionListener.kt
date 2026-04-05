package de.selfmade4u.tucanpluskmp

import org.jacoco.agent.rt.IAgent
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
        val agent: IAgent = org.jacoco.agent.rt.RT.getAgent()
        agent.reset()
    }

    var currentTestPlan: TestPlan? = null

    override fun testPlanExecutionStarted(testPlan: TestPlan) {
        currentTestPlan = testPlan
        println("testPlanExecutionStarted $testPlan")
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
        val executionData: ByteArray = agent.getExecutionData(true)

        File("./build/jacoco/${name}").mkdirs()
        File("./build/jacoco/${name}/metadata.json").writeText("""
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
        File("./build/jacoco/$name/$name.exec").writeBytes(executionData)
        println("DUMPED")
    }
}