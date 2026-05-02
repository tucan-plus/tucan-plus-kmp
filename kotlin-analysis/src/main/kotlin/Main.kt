package de.selfmade4u

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import java.nio.file.Paths
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtLibraryModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSdkModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File
import kotlin.io.path.Path

fun main() {
    println(PathUtil.KOTLIN_JAVA_STDLIB_JAR)
    val targetPlatform = JvmPlatforms.defaultJvmPlatform
    val session = buildStandaloneAnalysisAPISession {
        buildKtModuleProvider {
            platform = targetPlatform
            val jdk = buildKtSdkModule {
                addBinaryRootsFromJdkHome(Path(System.getProperty("java.home")), true)
                platform = targetPlatform
                libraryName = "sdk"
            }
            val lib = buildKtLibraryModule {
                platform = targetPlatform
                libraryName = "classpath"
                addBinaryRoot(File(CharRange::class.java.protectionDomain.codeSource.location.path).toPath())
            }
            addModule(
                buildKtSourceModule {
                    platform = targetPlatform
                    moduleName = "source"
                    addRegularDependency(jdk)
                    addRegularDependency(lib)
                    addSourceRoots(listOf(Paths.get("kotlin-analysis/src/main/resources/simple")))
                }
            )
        }
    }
    val ktFiles = session.modulesWithFiles.values.flatten().map { it as KtFile }
    println(ktFiles)
    for (ktFile in ktFiles) {
        analyze(ktFile) {
            val diagnostics = ktFile
                .collectDiagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS)
           println(diagnostics.map { it.defaultMessage }.joinToString("\n"))
        }
    }
    println("done")
}