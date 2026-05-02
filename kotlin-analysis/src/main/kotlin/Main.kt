package de.selfmade4u

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
}