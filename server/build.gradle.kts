plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.ktor)
    application
}

group = "de.selfmade4u.tucanpluskmp.server"
version = "1.0.0"
application {
    mainClass.set("de.selfmade4u.tucanpluskmp.server.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverJetty)
    testImplementation(libs.kotlin.testJunit)
    implementation(libs.ktor.network.tls.certificates)
}