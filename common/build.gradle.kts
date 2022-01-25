import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("maven-publish")
    kotlin("native.cocoapods")
}

group = "com.github.AjuntamentdeBarcelona"
version = "1.0.5"

kotlin {
    android()
    android {
        publishLibraryVariants("release", "debug")
    }

    // workaround 1: select iOS target platform depending on the Xcode environment variables
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64
    iOSTarget("ios") {
        binaries {
            framework {
                baseName = "common"
            }
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(Dependencies.Common.Main.coroutines)
                implementation(Dependencies.Common.Main.serialization)
                implementation(Dependencies.Common.Main.ktorClientCore)
                implementation(Dependencies.Common.Main.ktorClientJson)
                implementation(Dependencies.Common.Main.ktorSerialization)
                implementation(Dependencies.Common.Main.ktorClientAuth)
                implementation(Dependencies.Common.Main.ktorLogging)

                implementation(Dependencies.Common.Main.time)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Dependencies.Common.Android.ktorClientCore)
                implementation(project.dependencies.platform(Dependencies.Android.firebaseBoM))
                implementation(Dependencies.Android.analytics)
                implementation(Dependencies.Android.firebaseCrashlytics)
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(Dependencies.Common.Native.ktorClientCore)
            }
        }

        val iosTest by getting {
            dependencies {
            }
        }
    }

    cocoapods {
        summary = "Common library for the osam version and rating app control"
        homepage = "https://github.com/AjuntamentdeBarcelona/modul_comu_osam"
        framework {
            baseName = "OSAMCommon"
        }
        license = "BSD"
        authors = "Eduard Carbonell eduard.carbonell@worldline.com"
        ios.deploymentTarget = "13.0"
        pod("FirebaseAnalytics")
        pod("FirebaseCrashlytics")
    }

    val podspec = tasks["podspec"] as org.jetbrains.kotlin.gradle.tasks.PodspecTask
    podspec.doLast {
        val podspec = file("${project.name.replace("-", "_")}.podspec")
        val newPodspecContent = podspec.readLines().map {
            if (it.contains("spec.source")) "    spec.source                   = { :git => \"https://github.com/AjuntamentdeBarcelona/modul_comu_osam.git\", :tag => \"$version\" }" else it
        }
        podspec.writeText(newPodspecContent.joinToString(separator = "\n"))
    }
}

android {
    compileSdk = Common.targetSdkVersion
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Common.minSdkVersion
        targetSdk = Common.targetSdkVersion
        testInstrumentationRunner = Common.testInstrumentationRunner
    }
}

dependencies {
    implementation(Dependencies.Android.coroutinesPlayServices)
    implementation(Dependencies.Android.appCompat)
}