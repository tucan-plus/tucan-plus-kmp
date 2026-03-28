package de.selfmade4u.jacoco_report_multiple_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import javax.inject.Inject

interface MD5WorkParameters : WorkParameters {
    val sourceFile: RegularFileProperty
    val mD5File: RegularFileProperty
}

abstract class GenerateMD5 : WorkAction<MD5WorkParameters> {
    override fun execute() {
        try {
            val sourceFile: File = getParameters().sourceFile.getAsFile().get()
            val md5File: File = getParameters().mD5File.getAsFile().get()
            val stream: InputStream = FileInputStream(sourceFile)
            println("Generating MD5 for " + sourceFile.getName() + "...")
            // Artificially make this task slower.
            Thread.sleep(3000)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

@CacheableTask
abstract class JacocoReportMultiple : SourceTask() {
    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val inputProperty: Property<String>

    @get:OutputDirectory
    abstract val destinationDirectory: DirectoryProperty?

    @get:Inject
    abstract val workerExecutor: WorkerExecutor?

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        val workQueue = this.workerExecutor!!.noIsolation()

        println(
            if (inputChanges.isIncremental) "Executing incrementally"
            else "Executing non-incrementally"
        )

        inputChanges.getFileChanges(inputDir).forEach { change ->
            if (change.fileType == FileType.DIRECTORY) return@forEach

            println("${change.changeType}: ${change.normalizedPath}")
            val targetFile = outputDir.file(change.normalizedPath).get().asFile
            if (change.changeType == ChangeType.REMOVED) {
                targetFile.delete()
            } else {
                targetFile.writeText(change.file.readText().reversed())
            }
        }

        // https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReport.java looks very suspicous like this
        for (sourceFile in getSource().getFiles()) {
            val md5File = this.destinationDirectory!!.file(sourceFile.getName() + ".md5")
            workQueue.submit(GenerateMD5::class.java) {
                this.sourceFile.set(sourceFile)
                this.mD5File.set(md5File)
            }
        }
    }
}

public abstract class JacocoReportMultiplePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("JacocoReportMultiplePlugin") {
            doLast {
                println("Hello world from the build file!")
            }
        }
    }
}
