package de.selfmade4u.tucanpluskmp

import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier

class MyTestExecutionListener : TestExecutionListener {

    override fun executionStarted(testIdentifier: TestIdentifier) {
        println("EXECUTION STARTED")
    }
}