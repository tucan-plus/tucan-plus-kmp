import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "de.selfmade4u.tucanpluskmp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "de.selfmade4u.tucanpluskmp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    dependencies {
        implementation(projects.composeApp)
        implementation(libs.compose.uiToolingPreview)
        debugImplementation(libs.compose.uiTooling)
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.datastore.core)
        implementation(libs.androidx.room.runtime)
        implementation(libs.okio)
        implementation(libs.androidx.datastore.core.okio)
        implementation(libs.androidx.work.runtime.ktx)
    }
}