import java.io.FileInputStream
import java.io.InputStream

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

abstract class CreateMD5 : SourceTask() {
    @get:OutputDirectory
    abstract val destinationDirectory: DirectoryProperty?

    @get:Inject
    abstract val workerExecutor: WorkerExecutor?

    @TaskAction
    fun createHashes() {
        val workQueue = this.workerExecutor!!.noIsolation()

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