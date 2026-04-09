package de.selfmade4u.tucanpluskmp

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import okio.Path.Companion.toPath

class DirectoryDataTest : FunSpec({

    val directory = "your/directory".toPath()
    val files = platformFileSystem.list(directory) // Works on JVM, iOS, and Linux

    context("Processing files in ${directory.name}") {
        withData(
            // Use the filename as the test name for better reporting
            nameFn = { it.name },
            files
        ) { path ->
            val content = platformFileSystem.read(path) {
                readUtf8()
            }
            // Your test logic here
            content.isNotBlank() shouldBe true
        }
    }
})