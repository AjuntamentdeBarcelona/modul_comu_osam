import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("maven-publish")
}

val libName = "OSAMCommon"
val libGroup = "com.github.AjuntamentdeBarcelona"
val libVersionName = "2.0.2"
group = libGroup
version = libVersionName

kotlin {
    android()
    android {
        publishLibraryVariants("release", "debug")
    }

    val xcFramework = XCFramework(libName)
    ios {
        binaries.framework {
            baseName = libName
        }
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
                implementation(Dependencies.Common.Android.googlePlayCore)
                implementation(Dependencies.Common.Android.googlePlayCoreKtx)
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

        register("moveToMasterBranch") {
            doLast {
                project.exec {
                    workingDir = File("$rootDir")
                    commandLine("git", "checkout", "master").standardOutput
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
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Common.minSdkVersion
        targetSdk = Common.targetSdkVersion
        testInstrumentationRunner = Common.testInstrumentationRunner

        consumerProguardFile("proguard-rules.pro")
    }
}

dependencies {
    implementation(Dependencies.Android.coroutinesPlayServices)
    implementation(Dependencies.Android.appCompat)
}