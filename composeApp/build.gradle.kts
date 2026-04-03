import de.selfmade4u.jacoco_report_multiple_plugin.JacocoReportMultiple
import org.gradle.kotlin.dsl.androidTestUtil
import org.gradle.kotlin.dsl.invoke
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
    alias(libs.plugins.koin.compiler)
    jacoco
    id("de.selfmade4u.jacoco_report_multiple_plugin")
}

koinCompiler {
    userLogs = true
    debugLogs = true
    compileSafety = false // broken for separate module?
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.14"
}

// TODO FIXME if you actually delete the build directory, this fails
// rm -R composeApp/build/jacoco/
// ./gradlew --stacktrace clean :composeApp:jvmTest :composeApp:jacocoReportAll
// ~/Downloads/teamscale-build-linux-amd64/bin/teamscale-build coverage testwise -i composeApp/build/jacoco/ -o /tmp/testwise-coverage.json

// https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReport.java
// https://www.eclemma.org/jacoco/trunk/doc/ant.html
// https://docs.gradle.org/current/userguide/custom_tasks.html#sec:implementing_an_incremental_task
// https://docs.gradle.org/current/userguide/worker_api.html#converting_to_worker_api
// https://docs.gradle.org/current/userguide/build_cache.html#sec:using_annotations_to_enable_task_caching

// https://github.com/cqse/teamscale-java-profiler/blob/master/teamscale-gradle-plugin/src/main/kotlin/com/teamscale/reporting/testwise/TestwiseCoverageReport.kt
// https://github.com/cqse/teamscale-java-profiler/blob/527d0d5cda4c13713b0bd707ae2d48ceb7d3309b/teamscale-gradle-plugin/src/main/kotlin/com/teamscale/reporting/testwise/internal/TestwiseCoverageReporting.kt#L20
// https://github.com/cqse/teamscale-java-profiler/blob/527d0d5cda4c13713b0bd707ae2d48ceb7d3309b/report-generator/src/main/kotlin/com/teamscale/report/testwise/jacoco/JaCoCoTestwiseReportGenerator.kt#L28

compose.resources {
    publicResClass = true
    generateResClass = always
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

        withDeviceTestBuilder {
            sourceSetTreeName = "test"

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

    jvm{
        tasks.named<Test>("jvmTest") {
            useJUnitPlatform()
            configure<JacocoTaskExtension> {
                output = JacocoTaskExtension.Output.NONE
                isDumpOnExit = false
                destinationFile = null
            }
        }
        tasks.withType<Test>().configureEach {
            // Force the JVM to include the resources directory in the classpath
            val jvmMainResources = project.file("src/jvmTest/resources")
            inputs.dir(jvmMainResources)

            // Some ServiceLoaders need the context class loader set specifically
            systemProperty("java.util.ServiceLoader.debug", "true")
        }
    }

    js {
        browser {
            commonWebpackConfig {
                devtool = "source-map"
            }
            testTask {
                useKarma {
                    useChromium()
                }
            }
        }
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                devtool = "source-map"
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
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ui.test)
            implementation(libs.androidx.room.testing)
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
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.koin.core)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.workmanager)
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

fun getXmlFilesCollection(execFiles: ConfigurableFileTree): FileCollection {
    val xmlFilesProvider = execFiles.elements.map { locations ->
        locations.map { location ->
            val file = location.asFile
            file.parentFile.resolve("JACOCO").resolve(file.name.replace(".exec", ".xml"))
        }
    }

    return project.files(xmlFilesProvider)
}

fun getHtmlFilesCollection(execFiles: ConfigurableFileTree): FileCollection {
    return project.files(execFiles.elements.map { fileSystemLocations ->
        fileSystemLocations.map { it.asFile.parentFile.resolve("html") }
    })
}

tasks.register("jacocoReportAll", JacocoReportMultiple::class) {
    println("configuring")
    dependsOn(tasks.named("jvmTest"))
    val execData = fileTree(layout.buildDirectory.dir("jacoco")) {
        include("**/*.exec")
    }
    executionData.setFrom(execData)

    sourceDirectories.setFrom(files(
        "src/commonMain/kotlin",
        "src/jvmMain/kotlin"
    ))
    classDirectories.setFrom(fileTree(layout.buildDirectory.dir("classes/kotlin/jvm/main")) {
        exclude("**/R.class", "**/BuildConfig.*")
    })

    reports.xmlOutputLocation.setFrom(getXmlFilesCollection(execData))
    reports.htmlOutputLocation.setFrom(getHtmlFilesCollection(execData))
}