import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
    id("maven-publish")
}

val libName = "OSAMCommon"
val libGroup = "com.github.AjuntamentdeBarcelona"
val libVersionName = "2.1.8"

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
            xcFramework.add(this)
        }
    }

    sourceSets {

        commonMain.dependencies {
            implementation(libs.coroutinesCore)
            implementation(libs.serialization)
            implementation(libs.ktorClientCore)
            implementation(libs.ktorEngineCio)
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
        }

        iosMain.dependencies {
            implementation(libs.ktorClientCore)
        }

    }

    tasks {

        register("moveToMasterBranch") {
            doLast {
                project.exec {
                    workingDir = File("$rootDir")
                    commandLine("git", "checkout", "master").standardOutput
                }
            }
        }

        register("createFramework") {
            dependsOn("assemble${libName}ReleaseXCFramework")
            doLast {
                copy {
                    from("$buildDir/XCFrameworks/release")
                    into("$rootDir")
                }
            }
        }

        register("publishFramework") {
            description = "Publish iOs framework to the Cocoa Repo"
            group = "Publishing"

            dependsOn("moveToMasterBranch")
            // Create Release Framework for Xcode
            dependsOn("assemble${libName}ReleaseXCFramework")

            named("assemble${libName}ReleaseXCFramework").get().mustRunAfter("moveToMasterBranch")

            doLast {
                copy {
                    from("$buildDir/XCFrameworks/release")
                    into("$rootDir")
                }

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
                val successful = tempFile.renameTo(dir)

                if (successful) {

                    project.exec {
                        workingDir = File("$rootDir")
                        commandLine("git", "add", ".").standardOutput
                    }

                    project.exec {
                        workingDir = File("$rootDir")
                        commandLine(
                            "git",
                            "commit",
                            "-m",
                            "\"New release: ${libVersionName}\""
                        ).standardOutput
                    }

                    project.exec {
                        workingDir = File("$rootDir")
                        commandLine("git", "tag", libVersionName).standardOutput
                    }

                    project.exec {
                        workingDir = File("$rootDir")
                        commandLine("git", "push", "origin", "master", "--tags").standardOutput
                    }
                }
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