plugins {
    kotlin("jvm") version "2.3.21"
}

group = "de.selfmade4u"
version = ""

repositories {
    // https://github.com/detekt/detekt/blob/9db81c0e15bc296ab3031f8406ad05de4a1a3b19/settings.gradle.kts
    exclusiveContent {
        forRepository {
            // Remove when this is closed: https://youtrack.jetbrains.com/issue/KT-56203/AA-Publish-analysis-api-standalone-and-dependencies-to-Maven-Central
            maven("https://redirector.kotlinlang.org/maven/intellij-dependencies")
        }
        filter {
            includeModuleByRegex("org.jetbrains.kotlin", ".*-for-ide")
        }
    }
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    // https://github.com/JetBrains/amper/blob/13868b31d1442f7868bfd5f1fba68d43d41e8477/sources/extensibility/uses-aa.module-template.yaml#L15
    // https://youtrack.jetbrains.com/issue/KT-61419
    // https://youtrack.jetbrains.com/issue/KT-61639

    implementation(libs.kotlin.compiler)
    implementation(libs.kotlinx.serializationCore)

    // maybe put this separately?
    implementation(libs.kotlin.analysisApiStandalone) { isTransitive = false }

    implementation(libs.kotlin.analysisApi) { isTransitive = false }
    implementation(libs.kotlin.analysisApiK2) { isTransitive = false }
    implementation(libs.kotlin.analysisApiImplBase) { isTransitive = false }
    implementation(libs.kotlin.analysisApiPlatformInterface) { isTransitive = false }
    implementation(libs.kotlin.lowLevelApiFir) { isTransitive = false }
    implementation(libs.kotlin.symbolLightClasses) { isTransitive = false }

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}