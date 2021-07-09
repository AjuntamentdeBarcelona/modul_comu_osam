import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("maven-publish")
    kotlin("native.cocoapods")
}

group = "com.github.AjuntamentdeBarcelona"
version = "1.0.3"

android {
    compileSdkVersion(Common.targetSdkVersion)

    defaultConfig {
        minSdkVersion(Common.minSdkVersion)
        targetSdkVersion(Common.targetSdkVersion)
        versionCode = Common.versionCode
        versionName = Common.versionName
        testInstrumentationRunner = Common.testInstrumentationRunner
    }
}

dependencies {
    implementation(Dependencies.Android.coroutinesPlayServices)
    implementation(Dependencies.Android.appCompat)
    implementation("com.google.firebase:firebase-core:18.0.2")
}

kotlin {
    android()
    android {
        publishLibraryVariants("release")
    }

    // This is for iPhone emulator
    // Switch here to iosArm64 (or iosArm32) to build library for iPhone device
    val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos") ?: false
    if (onPhone) {
        iosArm64("ios")
    } else {
        iosX64("ios")
    }

    targets.getByName<KotlinNativeTarget>("ios").compilations["main"].kotlinOptions.freeCompilerArgs +=
        listOf("-Xobjc-generics", "-Xg0")

    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
                useExperimentalAnnotation("kotlinx.serialization.UnstableDefault")
                useExperimentalAnnotation("kotlin.RequiresOptIn")
            }
        }
    }

    sourceSets["commonMain"].dependencies {
        implementation(Dependencies.Common.Main.coroutines)
        implementation(Dependencies.Common.Main.serialization)
        implementation(Dependencies.Common.Main.ktorClientCore)
        implementation(Dependencies.Common.Main.ktorClientJson)
        implementation(Dependencies.Common.Main.ktorSerialization)
        implementation(Dependencies.Common.Main.ktorClientAuth)
        implementation(Dependencies.Common.Main.ktorLogging)

        implementation(Dependencies.Common.Main.time)

        implementation(kotlin("stdlib-common"))
    }

    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
    }

    sourceSets["androidMain"].dependencies {
        implementation(Dependencies.Common.Android.ktorClientCore)
        implementation(Dependencies.Android.analytics)
        implementation(Dependencies.Android.firebaseCrashlytics)
        implementation(kotlin("stdlib"))
    }

    sourceSets["androidTest"].dependencies {
        implementation(kotlin("test"))
        implementation(kotlin("test-junit"))
    }

    sourceSets["iosMain"].dependencies {
        implementation(Dependencies.Common.Native.ktorClientCore)
        implementation(kotlin("stdlib"))

    }

    sourceSets["iosTest"].dependencies {
    }

    cocoapods {
        summary = "Common library for the osam version and rating app control"
        homepage = "https://github.com/AjuntamentdeBarcelona/modul_comu_osam"
        frameworkName = "OSAMCommon"
        license = "BSD"
        authors = "Eduard Carbonell eduard.carbonell@worldline.com"
        pod("FirebaseAnalytics")
        pod("FirebaseCrashlytics")
        //podfile = project.file("../ios/Podfile")
    }
}