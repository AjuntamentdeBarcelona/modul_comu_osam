import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
    id("maven-publish")
}

val libName = "OSAMCommon"
val libGroup = "com.github.AjuntamentdeBarcelona"
val libVersionName = "2.2.4"

group = libGroup
version = libVersionName

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release", "debug")
    }

    val xcFramework = XCFramework(libName)
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->

        iosTarget.binaries.framework {
            this.embedBitcode(org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode.DISABLE)
            baseName = libName
        }
        iosTarget.binaries.framework(libName) {
            this.embedBitcode(org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode.DISABLE)
            linkerOpts("-framework", "SystemConfiguration")
            xcFramework.add(this)
        }
    }

    sourceSets {

        commonMain.dependencies {
            implementation(libs.coroutinesCore)
            implementation(libs.serialization)
            implementation(libs.ktorClientCore)
            implementation(libs.ktorClientJson)
            implementation(libs.contentNegotiation)
            implementation(libs.ktorSerialization)
            implementation(libs.ktorClientAuth)
            implementation(libs.ktorLogging)
        }

        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }

        androidMain.dependencies {
            implementation(libs.ktorClientCore)
            implementation(libs.androidPlayReview)
            implementation(libs.androidPlayReviewKtx)
            implementation(libs.ktorClientOkhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktorClientCore)
            implementation(libs.ktorClientDarwin)
        }

    }

    tasks {
        register<Delete>("deleteXCFramework") {
            println("Remove XCFramework from root dir")
            delete(File("$rootDir/OSAMCommon.xcframework"))
        }

        register<Copy>("copyFiles") {
            println("Copy new XCFramework to root dir")
            from(layout.buildDirectory.dir("XCFrameworks/release"))
            into(layout.buildDirectory.dir("$rootDir"))
        }

        register("createFramework") {
            println("Generate the new XCFramework")
            dependsOn("assemble${libName}ReleaseXCFramework")
            dependsOn("copyFiles")
            named("copyFiles").get().mustRunAfter("assemble${libName}ReleaseXCFramework")
        }

        register("publishFramework") {
            description = "Publish iOs framework to the Cocoa Repo"
            group = "Publishing"

            // Create Release Framework for Xcode
            dependsOn("deleteXCFramework")
            dependsOn("createFramework")
            named("createFramework").get().mustRunAfter("deleteXCFramework")


            doLast {
                val dir = File("$rootDir/$libName.podspec")
                val tempFile = File("$rootDir/$libName.podspec.new")

                val reader = dir.bufferedReader()
                val writer = tempFile.bufferedWriter()
                var currentLine: String?

                while (reader.readLine().also { currLine -> currentLine = currLine } != null) {
                    if (currentLine?.startsWith("    s.version") == true) {
                        writer.write("    s.version                  = \'${libVersionName}\'" + System.lineSeparator())
                    } else {
                        writer.write(currentLine + System.lineSeparator())
                    }
                }
                writer.close()
                reader.close()
                tempFile.renameTo(dir)
            }
        }
    }
}

android {
    compileSdk = Common.targetSdkVersion
    namespace = "cat.bcn.commonmodule"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = Common.minSdkVersion
        testInstrumentationRunner = Common.testInstrumentationRunner

        consumerProguardFile("proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(Dependencies.Android.coroutinesPlayServices)
    implementation(Dependencies.Android.appCompat)
    implementation(libs.multidex)
}