package de.selfmade4u.tucanpluskmp

import android.util.Log
import androidx.test.platform.io.PlatformTestStorageRegistry
import org.junit.runner.Description
import org.junit.runner.notification.RunListener
import java.io.File


// https://cs.android.com/androidx/android-test/+/main:runner/android_test_orchestrator/java/androidx/test/orchestrator/AndroidTestOrchestrator.java
// https://cs.android.com/androidx/android-test/+/main:runner/android_junit_runner/java/androidx/test/internal/runner/coverage/InstrumentationCoverageReporter.java
@Suppress("unused")
class MyRunListener : RunListener() {

    override fun testStarted(description: Description) {
        Log.e("MYRUNLISTENER", "testStarted ${description.displayName}")
        val rtClass = Class.forName("org.jacoco.agent.rt.RT")
        val getAgentMethod = rtClass.getMethod("getAgent")
        val agent = getAgentMethod.invoke(null)
        val resetMethod = agent.javaClass.getMethod("reset")
        resetMethod.invoke(agent)
    }

    override fun testFinished(description: Description) {
        Log.e("MYRUNLISTENER", "testFinished ${description.displayName}")

        val name = "${description.className}.${description.methodName}"
        val slashName = "${description.className}/${description.methodName}"

        val rtClass = Class.forName("org.jacoco.agent.rt.RT")

        val getAgentMethod = rtClass.getMethod("getAgent")
        val agent = getAgentMethod.invoke(null)

        val getExecutionDataMethod = agent.javaClass.getMethod(
            "getExecutionData",
            Boolean::class.javaPrimitiveType
        )

        val executionData = getExecutionDataMethod.invoke(agent, true) as ByteArray

        val metadataFile = PlatformTestStorageRegistry.getInstance().openOutputFile("${name}/metadata.json")
        metadataFile.use { f ->
            f.bufferedWriter().use {
                it.write("""
                    { "testInfo": 
                      {
                        "uniformPath": "$slashName",
                        "result": "PASSED",
                        "duration": 1 
                      }
                     }
                """.trimIndent())
            }
        }
        val execFile = PlatformTestStorageRegistry.getInstance().openOutputFile("$name/$name.exec")
        execFile.use { f ->
            f.write(executionData)
        }
        println("DUMPED")
    }
}