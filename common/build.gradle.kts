import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("co.touchlab.native.cocoapods")
    id("com.squareup.sqldelight")
}

android {
    compileSdkVersion(Common.targetSdkVersion)

    defaultConfig {
        minSdkVersion(Common.minSdkVersion)
        targetSdkVersion(Common.targetSdkVersion)
        versionCode = Common.versionCode
        versionName = Common.versionName
        testInstrumentationRunner = Common.testInstrumentationRunner
    }

    flavorDimensions("version")

    productFlavors {
        create(AppFlavor.AppName1.name.toLowerCase())
        create(AppFlavor.AppName2.name.toLowerCase())
    }

    signingConfigs {
        // create(AppFlavor.AppName1.signInName)
        // create(AppFlavor.AppName2.signInName)
    }

    buildTypes {
        val proguard = getDefaultProguardFile("proguard-android.txt")
    }
}

dependencies {
    implementation(Dependencies.Android.coroutinesPlayServices)
}

kotlin {
    android()
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

    version = "1.1"

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
        implementation(Dependencies.Common.Main.sqldelightRuntime)

        implementation(Dependencies.Common.Main.stately)

        implementation(kotlin("stdlib-common"))
    }

    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
    }

    sourceSets["androidMain"].dependencies {
        implementation(Dependencies.Common.Android.coroutines)
        implementation(Dependencies.Common.Android.serialization)
        implementation(Dependencies.Common.Android.ktorClientCore)
        implementation(Dependencies.Common.Android.ktorClientJson)
        implementation(Dependencies.Common.Android.ktorSerialization)
        implementation(Dependencies.Common.Android.ktorClientAuth)
        implementation(Dependencies.Common.Android.ktorLogging)
        implementation(Dependencies.Common.Android.sqldelightDriverAndroid)

        implementation(kotlin("stdlib"))
    }

    sourceSets["androidTest"].dependencies {
        implementation(kotlin("test"))
        implementation(kotlin("test-junit"))
    }

    sourceSets["iosMain"].dependencies {
        implementation(Dependencies.Common.Native.coroutines)
        implementation(Dependencies.Common.Native.serialization)
        implementation(Dependencies.Common.Native.ktorClientCore)
        implementation(Dependencies.Common.Native.ktorClientJson)
        implementation(Dependencies.Common.Native.ktorSerialization)
        implementation(Dependencies.Common.Native.ktorClientAuth)
        implementation(Dependencies.Common.Native.ktorLogging)
        implementation(Dependencies.Common.Native.sqldelightDriverNative)

        implementation(kotlin("stdlib"))
    }
    sourceSets["iosTest"].dependencies {
    }

    cocoapodsext {
        summary = "Common library for the Eroski/Caprabo app"
        homepage = "https://gitlab.kazan.atosworldline.com/android-tempos-21/eroski_mpp"
        isStatic = false
    }
}

sqldelight {
    database("eroskiclub") {
        packageName = "com.tempos21.eroskiclub.db"
    }
}
