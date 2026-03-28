package de.selfmade4u.jacoco_report_multiple_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.reporting.ConfigurableReport
import org.gradle.api.reporting.DirectoryReport
import org.gradle.api.reporting.ReportContainer
import org.gradle.api.reporting.Reporting
import org.gradle.api.reporting.SingleFileReport
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
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

interface JacocoReportsMultipleContainer : ReportContainer<ConfigurableReport> {

    @get:Internal
    val html: DirectoryReport

    @get:Internal
    val xml: SingleFileReport

    @get:Internal
    val csv: SingleFileReport
}

// https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReport.java
// https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReportBase.java
// https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReportsContainer.java
@CacheableTask
abstract class JacocoReportMultiple : SourceTask(), Reporting<JacocoReportsMultipleContainer> {
    @get:Incremental
    @get:PathSensitive(PathSensitivity.NONE)
    @get:InputFiles
    abstract val executionData: DirectoryProperty

    @get:IgnoreEmptyDirectories
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFiles
    abstract val sourceDirectories: ConfigurableFileCollection

    @get:Classpath
    abstract val classDirectories: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val inputProperty: Property<String>

    @get:Inject
    abstract val workerExecutor: WorkerExecutor?

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        val workQueue = this.workerExecutor!!.noIsolation()

        println(
            if (inputChanges.isIncremental) "Executing incrementally"
            else "Executing non-incrementally"
        )

        /*inputChanges.getFileChanges(inputDir).forEach { change ->
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
        }*/
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
