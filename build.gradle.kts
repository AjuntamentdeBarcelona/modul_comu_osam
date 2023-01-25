buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Dependencies.Root.android)
        classpath(Dependencies.Root.googleServices)
        classpath(Dependencies.Root.serialization)
        classpath(Dependencies.Root.firebaseCrashlytics)
        classpath(Dependencies.Root.firebasePerformance)

        classpath(kotlin("gradle-plugin", kotlin_version))
    }
}

allprojects {

    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
