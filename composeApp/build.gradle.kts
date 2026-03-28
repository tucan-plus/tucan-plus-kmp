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
    id("GreetingPlugin")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.14"
}

// ./gradlew clean :composeApp:jvmTest :composeApp:jacocoReportAll

val execFiles = fileTree(layout.buildDirectory.dir("jacoco")) {
    include("*.exec")
}

println(execFiles)

// https://github.com/gradle/gradle/blob/master/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/tasks/JacocoReport.java
// https://www.eclemma.org/jacoco/trunk/doc/ant.html
// https://docs.gradle.org/current/userguide/custom_tasks.html#sec:implementing_an_incremental_task
// https://docs.gradle.org/current/userguide/worker_api.html#converting_to_worker_api
// https://docs.gradle.org/current/userguide/build_cache.html#sec:using_annotations_to_enable_task_caching

// https://github.com/cqse/teamscale-java-profiler/blob/master/teamscale-gradle-plugin/src/main/kotlin/com/teamscale/reporting/testwise/TestwiseCoverageReport.kt
// https://github.com/cqse/teamscale-java-profiler/blob/527d0d5cda4c13713b0bd707ae2d48ceb7d3309b/teamscale-gradle-plugin/src/main/kotlin/com/teamscale/reporting/testwise/internal/TestwiseCoverageReporting.kt#L20
// https://github.com/cqse/teamscale-java-profiler/blob/527d0d5cda4c13713b0bd707ae2d48ceb7d3309b/report-generator/src/main/kotlin/com/teamscale/report/testwise/jacoco/JaCoCoTestwiseReportGenerator.kt#L28
execFiles.forEach { execFile ->
    println(execFile)
    val namePart = execFile.name.removeSuffix(".exec")

    tasks.register("jacocoReport_$namePart", JacocoReport::class) {
        dependsOn(tasks.named("jvmTest"))

        executionData.setFrom(execFile)

        sourceDirectories.setFrom(files(
            "src/commonMain/kotlin",
            "src/jvmMain/kotlin"
        ))
        classDirectories.setFrom(fileTree(layout.buildDirectory.dir("classes/kotlin/jvm/main")) {
            exclude("**/R.class", "**/BuildConfig.*")
        })

        reports {
            xml.required.set(true)
            xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/$namePart/JACOCO/coverage.xml"))

            html.required.set(false)
            csv.required.set(false)
        }
    }
}

tasks.register("jacocoReportAll") {
    dependsOn(tasks.withType(JacocoReport::class))
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

            testLogging {
                showStandardStreams = true
                events("started", "passed", "skipped", "failed")
            }

            addTestListener(object : TestListener {
                override fun beforeSuite(suite: TestDescriptor?) {
                    println("before suite")
                }

                override fun beforeTest(testDescriptor: TestDescriptor?) {
                    println("before test $testDescriptor")
                }

                override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
                    println("after test $testDescriptor $result")
                }
            })
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
