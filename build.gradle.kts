import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://dl.bintray.com/touchlabpublic/kotlin")
        maven(url = "https://maven.fabric.io/public")
    }
    dependencies {
        classpath(Dependencies.Root.android)
        // classpath(Dependencies.Root.googlePlay)
        classpath(Dependencies.Root.serialization)
        classpath(Dependencies.Root.cocoapods)
        classpath(kotlin("gradle-plugin", kotlin_version))
    }
}

allprojects {

    val localProperties = gradleLocalProperties(rootDir)

    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://dl.bintray.com/touchlabpublic/kotlin")
        maven(url = "https://maven.fabric.io/public")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
