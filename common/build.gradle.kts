import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("maven-publish")
    kotlin("native.cocoapods")
}

val libName = "OSAMCommon"
group = "com.github.AjuntamentdeBarcelona"
version = "1.0.5"

kotlin {
    android()
    android {
        publishLibraryVariants("release", "debug")
    }

    val xcFramework = XCFramework(libName)
    ios {
        binaries.framework(libName) {
            xcFramework.add(this)
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
        //podfile = project.file("../ios/Podfile")
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