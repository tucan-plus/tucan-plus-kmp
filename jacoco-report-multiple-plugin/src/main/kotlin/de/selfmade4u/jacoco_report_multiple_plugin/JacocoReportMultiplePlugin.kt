package de.selfmade4u.jacoco_report_multiple_plugin

import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileType
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.jacoco.JacocoReportAction
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

abstract class JacocoReportsMultipleContainer {

    @get:OutputFile
    abstract val xmlOutputLocation: RegularFileProperty
}

// https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReport.java
// https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReportBase.java
// https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReportsContainer.java
@CacheableTask
abstract class JacocoReportMultiple : SourceTask() {
    @get:Incremental
    @get:PathSensitive(PathSensitivity.NONE)
    @get:InputFiles
    abstract val executionData: ConfigurableFileCollection

    @get:IgnoreEmptyDirectories
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFiles
    abstract val sourceDirectories: ConfigurableFileCollection

    @get:Classpath
    abstract val classDirectories: ConfigurableFileCollection

    @get:Classpath
    abstract val jacocoClasspath: FileCollection

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val inputProperty: Property<String>

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    @get:Inject
    abstract val objects: ObjectFactory

    @get:Nested
    abstract val reports: JacocoReportsMultipleContainer

    @get:Input
    abstract val reportProjectName: Property<String>

    @get:Incubating
    @get:Optional
    @get:Input
    abstract val sourceEncoding: Property<String>

    init {
        reportProjectName.convention(project.name)
        reportProjectName.disallowChanges()
    }

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        val queue = this.workerExecutor.noIsolation()

        println(
            if (inputChanges.isIncremental) "Executing incrementally"
            else "Executing non-incrementally"
        )

        // https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReport.java looks very suspicous like this
        inputChanges.getFileChanges(executionData).forEach { change ->
            if (change.fileType == FileType.DIRECTORY) return@forEach

            println("${change.changeType}: ${change.normalizedPath}")
            val targetFile = outputDir.file(change.normalizedPath).get().asFile
            if (change.changeType == ChangeType.REMOVED) {
                targetFile.delete()
            } else {
                queue.submit(JacocoReportAction::class.java) {
                    this.getAntLibraryClasspath().convention(jacocoClasspath);
                    this.getProjectName().convention(reportProjectName);
                    this.getEncoding().convention(sourceEncoding);
                    this.getAllSourcesDirs().convention(sourceDirectories);
                    this.getAllClassesDirs().convention(classDirectories);
                    this.getExecutionData().convention(executionData);

                    this.getGenerateHtml().convention(false);
                    //this.getHtmlDestination().convention(reports.getHtml().getOutputLocation());
                    this.getGenerateXml().convention(true);
                    this.getXmlDestination().convention(reports.xmlOutputLocation);
                    this.getGenerateCsv().convention(false);
                    //this.getCsvDestination().convention(reports.getCsv().getOutputLocation());
                }
                targetFile.writeText(change.file.readText().reversed())
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
