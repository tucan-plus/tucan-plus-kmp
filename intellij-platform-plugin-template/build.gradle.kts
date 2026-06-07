import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
}
/*
sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}*/

dependencies {
    testRuntimeOnly("junit:junit:4.13.2")

    // Import the BOM to align all junit-jupiter/platform versions
    testImplementation(platform("org.junit:junit-bom:5.14.4"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter")

    // If you use specialized platform features:
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        androidStudio("2026.1.2.5")
        bundledPlugin("org.jetbrains.kotlin")
        bundledPlugin("com.intellij.java")
        testFramework(TestFrameworkType.Platform)
        //testFramework(TestFrameworkType.Starter, configurationName = "integrationTestImplementation")
        testFramework(TestFrameworkType.JUnit5)
        testFramework(TestFrameworkType.Plugin.Java)
        //bundledLibrary("org.jetbrains.kotlin.kotlin-test-framework")
    }

   /* integrationTestImplementation("org.junit.jupiter:junit-jupiter-engine")
    integrationTestImplementation("org.junit.platform:junit-platform-launcher")
    integrationTestImplementation("org.jetbrains.kotlin:kotlin-stdlib")
    integrationTestImplementation("org.kodein.di:kodein-di-jvm:7.20.2")
    integrationTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.1")*/
}
/*
val integrationTest by intellijPlatformTesting.testIdeUi.registering {
    task {
        val integrationTestSourceSet = sourceSets.getByName("integrationTest")
        testClassesDirs = integrationTestSourceSet.output.classesDirs
        classpath = integrationTestSourceSet.runtimeClasspath
        useJUnitPlatform()
    }
}*/

tasks.test {
    useJUnitPlatform()
}
