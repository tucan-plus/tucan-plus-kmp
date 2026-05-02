package de.selfmade4u

import java.nio.file.Paths
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtLibraryModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule

fun main() {
    val session = buildStandaloneAnalysisAPISession {
        buildKtModuleProvider {
            // Define your source roots and dependencies here
            addModule(
                buildKtSourceModule {
                    addSourceRoot(Paths.get("src/main/kotlin"))
                    moduleName = "my-module"
                    // Add standard libraries so types like 'String' resolve
                    addRegularDependency(buildKtLibraryModule { })
                }
            )
        }
    }
}