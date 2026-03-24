import org.gradle.kotlin.dsl.jacocoAgent
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    jacoco
}

jacoco {
    toolVersion = "0.8.14"
}

// ./gradlew :composeApp:cleanJvmTest :composeApp:jvmTest
tasks.register<JacocoReport>("jvmTestCodeCoverageReport") {
    group = "Verification"
    description = "Generates Jacoco coverage reports for the JVM target."

    // 1. Depend on the unit test task
    dependsOn(tasks.named("jvmTest"))

    // 2. Define which execution data to use
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("jacoco/jvmTest.exec")
    })

    // 3. Point to the compiled .class files (excluding generated code)
    val classFiles = fileTree(layout.buildDirectory.dir("classes/kotlin/jvm/main")) {
        exclude("**/R.class", "**/BuildConfig.*")
    }
    classDirectories.setFrom(classFiles)

    // 4. Map back to your source code for the report visualization
    sourceDirectories.setFrom(files(
        "src/commonMain/kotlin",
        "src/jvmMain/kotlin"
    ))

    reports {
        xml.required.set(true)
        html.required.set(true) // Generates a readable website in build/reports/jacoco
    }
}

compose.resources {
    publicResClass = true
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(25)
        //vendor = JvmVendorSpec.JETBRAINS
    }

    android {
        namespace = "de.selfmade4u.tucanpluskmp.library"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        androidResources {
            enable = true
        }
    }

    /*listOf(
        //iosArm64(),
        //iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }*/

    jvm() {
        tasks.named<Test>("jvmTest") {
            useJUnitPlatform()
        }
    }

    js {
        browser {
            commonWebpackConfig {
                devtool = "inline-source-map"
            }
        }
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                devtool = "inline-source-map"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.material3.adaptiveNavigation3)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.logging)
            implementation(libs.serialization.json)
            implementation(libs.kotlinx.serialization.json.okio)
            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.core.okio)
            implementation(libs.okio.fakefilesystem)
            implementation(libs.okio)
            implementation(libs.ksoup)
            implementation(libs.androidx.room.runtime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ui.test)
        }
        jvmMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.junit.jupiter)
            implementation(libs.junit.platform.launcher)
            compileOnly("org.jacoco:org.jacoco.agent:0.8.14:runtime")
        }
        androidMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.androidx.browser)
            implementation(libs.accompanist.permissions)
        }
        webMain.dependencies {
            implementation(libs.androidx.sqlite.web)
            implementation(npm("@sqlite.org/sqlite-wasm", "3.50.1-build1"))
            implementation(libs.navigation3.browser)
            implementation(libs.kotlinx.browser)
        }
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspWasmJs", libs.androidx.room.compiler)
    add("kspJs", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}

compose.desktop {
    application {
        mainClass = "de.selfmade4u.tucanpluskmp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "de.selfmade4u.tucanpluskmp"
            packageVersion = "1.0.0"
        }
    }
}
