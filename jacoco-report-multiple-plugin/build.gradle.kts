plugins {
    `kotlin-dsl`
}

group = "de.selfmade4u"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jacoco:org.jacoco.report:0.8.14")
}

gradlePlugin {
    plugins {
        create("jacoco_report_multiple_plugin") {
            id = "de.selfmade4u.jacoco_report_multiple_plugin"
            implementationClass = "de.selfmade4u.jacoco_report_multiple_plugin.JacocoReportMultiplePlugin"
        }
    }
}