import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.text.SimpleDateFormat
import java.util.*

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("maven-publish")
}

val libName = "OSAMCommon"
val libGroup = "com.github.AjuntamentdeBarcelona"
val libVersionName = "1.0.6"
group = libGroup
version = libVersionName

val xcframeworkDestinationRepo = "common_module_mobile_frameworks"

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

    tasks {

        /*register("publishDevFramework") {
            description = "Publish iOs framework to the Cocoa Repo"

            project.exec {
                workingDir = File("$rootDir/../$xcframeworkDestinationRepo")
                commandLine("git", "checkout", "develop").standardOutput
            }

            dependsOn("assemble${libName}DebugXCFramework")

            doLast {

                copy {
                    from("$buildDir/XCFrameworks/debug")
                    into("$rootDir/../$xcframeworkDestinationRepo")
                }

                val dir = File("$rootDir/../$xcframeworkDestinationRepo/$libName.podspec")
                val tempFile = File("$rootDir/../$xcframeworkDestinationRepo/$libName.podspec.new")

                val reader = dir.bufferedReader()
                val writer = tempFile.bufferedWriter()
                var currentLine: String?

                while (reader.readLine().also { currLine -> currentLine = currLine } != null) {
                    if (currentLine?.startsWith("s.version") == true) {
                        writer.write("s.version       = \"${libVersionName}\"" + System.lineSeparator())
                    } else {
                        writer.write(currentLine + System.lineSeparator())
                    }
                }
                writer.close()
                reader.close()
                val successful = tempFile.renameTo(dir)

                if (successful) {

                    project.exec {
                        workingDir = File("$rootDir/../$xcframeworkDestinationRepo")
                        commandLine(
                            "git",
                            "add",
                            "."
                        ).standardOutput
                    }

                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                    project.exec {
                        workingDir = File("$rootDir/../$xcframeworkDestinationRepo")
                        commandLine(
                            "git",
                            "commit",
                            "-m",
                            "\"New dev release: ${libVersionName}-${dateFormatter.format(Date())}\""
                        ).standardOutput
                    }

                    project.exec {
                        workingDir = File("$rootDir/../$xcframeworkDestinationRepo")
                        commandLine("git", "push", "origin", "develop").standardOutput
                    }
                }
            }
        }

        register("publishFramework") {
            description = "Publish iOs framework to the Cocoa Repo"

            project.exec {
                workingDir = File("$rootDir/../$xcframeworkDestinationRepo")
                commandLine("git", "checkout", "master").standardOutput
            }

            // Create Release Framework for Xcode
            dependsOn("assemble${libName}ReleaseXCFramework")

            // Replace
            doLast {

                copy {
                    from("$buildDir/XCFrameworks/release")
                    into("$rootDir/../$xcframeworkDestinationRepo")
                }

                val dir = File("$rootDir/../$xcframeworkDestinationRepo/$libName.podspec")
                val tempFile = File("$rootDir/../$xcframeworkDestinationRepo/$libName.podspec.new")

                val reader = dir.bufferedReader()
                val writer = tempFile.bufferedWriter()
                var currentLine: String?

                while (reader.readLine().also { currLine -> currentLine = currLine } != null) {
                    if (currentLine?.startsWith("s.version") == true) {
                        writer.write("s.version       = \"${libVersionName}\"" + System.lineSeparator())
                    } else {
                        writer.write(currentLine + System.lineSeparator())
                    }
                }
                writer.close()
                reader.close()
                val successful = tempFile.renameTo(dir)

                if (successful) {

                    project.exec {
                        workingDir = File("$rootDir/../$xcframeworkDestinationRepo")
                        commandLine("git", "add", ".").standardOutput
                    }

                    project.exec {
                        workingDir = File("$rootDir/../$xcframeworkDestinationRepo")
                        commandLine("git", "commit", "-m", "\"New release: ${libVersionName}\"").standardOutput
                    }

                    project.exec {
                        workingDir = File("$rootDir/../$xcframeworkDestinationRepo")
                        commandLine("git", "tag", libVersionName).standardOutput
                    }

                    project.exec {
                        workingDir = File("$rootDir/../$xcframeworkDestinationRepo")
                        commandLine("git", "push", "origin", "master", "--tags").standardOutput
                    }
                }
            }
        }*/
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