plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.ktor)
    application
}

group = "de.selfmade4u.tucanpluskmp.server"
version = "1.0.0"
application {
    mainClass.set("de.selfmade4u.tucanpluskmp.server.ApplicationKt")
}

dependencies {
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverJetty)
    testImplementation(libs.kotlin.testJunit)
    implementation(libs.ktor.network.tls.certificates)
    implementation("org.slf4j:slf4j-simple:2.0.17")
}