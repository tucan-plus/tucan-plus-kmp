package de.selfmade4u.jacoco_report_multiple_plugin

import org.gradle.api.DefaultTask
import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
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
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.jacoco.JacocoReportAction
import org.gradle.kotlin.dsl.withType
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

abstract class JacocoReportsMultipleContainer {

    @get:OutputFiles
    abstract val xmlOutputLocation: ConfigurableFileCollection
}

// https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReport.java
// https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReportBase.java
// https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReportsContainer.java
//@CacheableTask
abstract class JacocoReportMultiple : DefaultTask() {
    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY) // we need the name to name the output
    @get:InputFiles
    abstract val executionData: ConfigurableFileCollection

    @get:IgnoreEmptyDirectories
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFiles
    abstract val sourceDirectories: ConfigurableFileCollection

    @get:Classpath
    abstract val classDirectories: ConfigurableFileCollection

    @get:Classpath
    abstract var jacocoClasspath: FileCollection

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

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
        println("EXECUTING")
        val queue = this.workerExecutor.classLoaderIsolation()

        println(
            if (inputChanges.isIncremental) "Executing incrementally"
            else "Executing non-incrementally"
        )

        println("executionData ${executionData.files}")
        println("reports.xmlOutputLocation ${reports.xmlOutputLocation.files}")

        // https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReport.java looks very suspicous like this
        inputChanges.getFileChanges(executionData).forEach { change ->
            if (change.fileType == FileType.DIRECTORY) return@forEach

            println("${change.changeType}: ${change.normalizedPath}")
            val name = change.normalizedPath.removeSuffix(".exec")

            val targetFile = reports.xmlOutputLocation.find { it.name == "$name.xml" }!!
            println("targetFile $targetFile")
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
                    this.getGenerateXml().convention(true);
                    this.getXmlDestination().set(targetFile);
                    this.getGenerateCsv().convention(false);
                }
            }
        }
    }
}

public abstract class JacocoReportMultiplePlugin : Plugin<Project> {

    val ANT_CONFIGURATION_NAME: String = "jacocoAnt"

    override fun apply(project: Project) {
        val config: Configuration = project.getConfigurations().getAt(ANT_CONFIGURATION_NAME);
        project.tasks.withType<JacocoReportMultiple> {
            jacocoClasspath = config
        }
    }
}
