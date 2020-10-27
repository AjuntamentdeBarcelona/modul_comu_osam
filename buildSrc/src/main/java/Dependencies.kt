const val propertiesDir = "signin/properties"

const val coroutines_version = "1.3.9-native-mt-2"
const val serialization_version = "1.0.0"
const val fragment_version = "1.1.0"
const val ktor_version = "1.4.1"
const val crashlytics_version = "2.9.8"
const val kotlin_version = "1.4.10"

const val cocoapods_version = "0.6"

object App {
    const val minSdkVersion = 19
    const val targetSdkVersion = 29
    const val versionCode = 1
    const val versionName = "0.0.1"
    const val testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
}

object Common {
    const val minSdkVersion = 19
    const val targetSdkVersion = 29
    const val versionCode = 1
    const val versionName = "0.0.1"
    const val testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
}

object Dependencies {
    object Android {
        // Android
        const val appCompat = "androidx.appcompat:appcompat:1.1.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
        const val recycler = "androidx.recyclerview:recyclerview:1.2.0-alpha01"
        const val multidex = "androidx.multidex:multidex:2.0.0"

        // Play Services
        const val playPrefix = "com.google.android.gms:play-services"

        const val playServices = "$playPrefix-base:16.0.0"

        // Firebase
        const val firebaseCore = "com.google.firebase:firebase-core:16.0.8"
        const val firebaseAnalytics = "com.google.firebase:firebase-analytics:17.2.1"
        const val firebasePerf = "com.google.firebase:firebase-perf:19.0.5"
        const val crashlytics =
            "com.crashlytics.sdk.android:crashlytics:$crashlytics_version@aar" // transitive = true

        // Coroutines
        const val coroutinesAndroid =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version"
        const val coroutinesPlayServices =
            "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutines_version"

        object Test {
            const val junit = "junit:junit:4.12"
            const val runner = "com.android.support.test:runner:1.0.2"
        }

        object Data {
            const val fabric = "com.crashlytics.sdk.android:crashlytics:$crashlytics_version@aar"
        }
    }

    object Root {
        const val android = "com.android.tools.build:gradle:4.0.1"
        const val googlePlay = "com.google.gms:google-services:4.2.0"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:2.0.0-beta04"
        const val serialization = "org.jetbrains.kotlin:kotlin-serialization:$kotlin_version"
        const val cocoapods = "co.touchlab:kotlinnativecocoapods:$cocoapods_version"
    }

    object Common {
        object Main {
            const val coroutines =
                "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version"
            const val serialization =
                "org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version"

            const val ktorClientCore = "io.ktor:ktor-client-core:$ktor_version"
            const val ktorClientJson = "io.ktor:ktor-client-json:$ktor_version"
            const val ktorSerialization = "io.ktor:ktor-client-serialization:$ktor_version"
            const val ktorClientAuth = "io.ktor:ktor-client-auth:$ktor_version"
            const val ktorLogging = "io.ktor:ktor-client-logging:$ktor_version"

            const val stately = "co.touchlab:stately-common:1.0.3"
        }

        object Android {
            const val serialization =
                "org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version"
            const val ktorClientCore = "io.ktor:ktor-client-okhttp:$ktor_version"
        }

        object Native {
            const val serialization =
                "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serialization_version"
            const val ktorClientCore = "io.ktor:ktor-client-ios:$ktor_version"
        }
    }
}
