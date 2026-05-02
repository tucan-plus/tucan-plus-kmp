package de.selfmade4u

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.fir.diagnostics.KaFirDiagnostic
import java.nio.file.Paths
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtLibraryModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile

fun main() {
    val session = buildStandaloneAnalysisAPISession {
        buildKtModuleProvider {
            val targetPlatform = JvmPlatforms.defaultJvmPlatform
            platform = targetPlatform
            addModule(
                buildKtSourceModule {
                    addSourceRoots(listOf(Paths.get("kotlin-analysis/src/main/resources/simple")))
                    platform = targetPlatform
                    moduleName = "source"
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
}