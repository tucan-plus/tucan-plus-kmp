package de.selfmade4u

import java.nio.file.Paths
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtLibraryModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms

fun main() {
    val session = buildStandaloneAnalysisAPISession {
        buildKtModuleProvider {
            val targetPlatform = JvmPlatforms.defaultJvmPlatform
            platform = targetPlatform
            addModule(
                buildKtSourceModule {
                    addSourceRoots(listOf())
                    platform = targetPlatform
                    moduleName = "source"
                }
            )
        }
    }
}