package de.selfmade4u

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtLibraryModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSdkModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtAnnotation
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtVisitorVoid
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.system.exitProcess


// https://github.com/detekt/detekt/blob/9db81c0e15bc296ab3031f8406ad05de4a1a3b19/detekt-test-utils/src/main/kotlin/dev/detekt/test/utils/KotlinAnalysisApiEngine.kt#L6
// https://github.com/detekt/detekt/blob/9db81c0e15bc296ab3031f8406ad05de4a1a3b19/detekt-core/src/main/kotlin/dev/detekt/core/settings/EnvironmentAware.kt#L14
fun main() {
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
        ktFile.accept(object : KtTreeVisitorVoid() {
            override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry) {
                super.visitAnnotationEntry(annotationEntry)
                println("annotation entry ${annotationEntry.text}")
            }
        })
    }
    exitProcess(0)
}