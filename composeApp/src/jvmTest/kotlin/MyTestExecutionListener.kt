package de.selfmade4u.tucanpluskmp

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.reporting.FileEntry
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.MethodSource
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.TestPlan
import java.io.File
import java.net.URL
import java.nio.file.Paths

class MyTestExecutionListener : TestExecutionListener {

    fun getPathFromTestIdentifier(testIdentifier: TestIdentifier): String? {
        val clazz: Class<*> = when (val source = testIdentifier.source.orElse(null)) {
            is ClassSource -> source.javaClass
            is MethodSource -> Class.forName(source.className)
            else -> return null // Not a class-based test
        }

        // 2. Resolve the physical path
        return getClassFilePath(clazz)
    }

    fun getClassFilePath(clazz: Class<*>): String? {
        val resourceName = "${clazz.simpleName}.class"
        val url: URL? = clazz.getResource(resourceName)

        // Returns the absolute path to the .class file
        return url?.path
    }

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
        val folder = "./build/jacoco/${testIdentifier.displayName}/"
        File("$folder/JACOCO").mkdirs()
        File("$folder/metadata.json").writeText("""
            { "testInfo": 
              {
                "uniformPath": "${getPathFromTestIdentifier(testIdentifier)}", 
                "result": "PASSED",
                "duration": 1 
              }
             }
        """.trimIndent())
        File("$folder/JACOCO/jvmTest.exec").writeBytes(executionData)
        println("DUMPED")
    }

    override fun executionSkipped(testIdentifier: TestIdentifier, reason: String) {

    }
}