const val propertiesDir = "signin/properties"

const val coroutines_version = "1.6.4"
const val serialization_version = "1.5.0"
const val ktor_version = "2.2.4"
const val kotlin_version = "1.8.20"
const val google_play_core_version = "1.10.3"
const val google_play_core_ktx_version = "1.8.1"


object App {
    const val applicationId = "cat.bcn.commonmodule"
    const val minSdkVersion = 21
    const val targetSdkVersion = 33
    const val versionCode = 2023041700
    const val versionName = "1.1.1"
    const val testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
}

object Common {
    const val minSdkVersion = App.minSdkVersion
    const val targetSdkVersion = App.targetSdkVersion
    const val testInstrumentationRunner = App.testInstrumentationRunner
}

object Dependencies {
    object Android {
        // Android
        const val appCompat = "androidx.appcompat:appcompat:1.6.1"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.4"
        const val recycler = "androidx.recyclerview:recyclerview:1.2.0"
        const val multidex = "androidx.multidex:multidex:2.0.1"

        // Firebase
        const val firebaseBoM = "com.google.firebase:firebase-bom:29.0.4" //import platform
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val firebasePerf = "com.google.firebase:firebase-perf-ktx"
        const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-ktx"

        // Coroutines
        const val coroutinesAndroid =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version"
        const val coroutinesPlayServices =
            "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutines_version"

        object Test {
            const val junit = "junit:junit:4.13.2"
            const val runner = "com.android.support.test:runner:1.0.2"
        }
    }

    object Root {
        const val android = "com.android.tools.build:gradle:7.4.2"
        const val googleServices = "com.google.gms:google-services:4.3.15"
        const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-gradle:2.9.5"
        const val serialization = "org.jetbrains.kotlin:kotlin-serialization:$kotlin_version"
        const val firebasePerformance = "com.google.firebase:perf-plugin:1.4.2"

    }

    object Common {
        object Main {
            const val coroutines =
                "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version"
            const val serialization =
                "org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version"

            const val ktorClientCore = "io.ktor:ktor-client-core:$ktor_version"
            const val ktorContentNegotiation = "io.ktor:ktor-client-content-negotiation:$ktor_version"
            const val ktorClientJson = "io.ktor:ktor-client-json:$ktor_version"
            const val ktorSerialization = "io.ktor:ktor-client-serialization:$ktor_version"
            const val ktorClientAuth = "io.ktor:ktor-client-auth:$ktor_version"
            const val ktorLogging = "io.ktor:ktor-client-logging:$ktor_version"
            const val ktorSerializationJson = "io.ktor:ktor-serialization-kotlinx-json:$ktor_version"
        }

        object Android {
            const val serialization =
                "org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version"
            const val ktorClientCore = "io.ktor:ktor-client-okhttp:$ktor_version"
            const val googlePlayCore = "com.google.android.play:core:$google_play_core_version"
            const val googlePlayCoreKtx = "com.google.android.play:core-ktx:$google_play_core_ktx_version"
        }

        object Native {
            const val serialization =
                "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serialization_version"
            const val ktorClientCore = "io.ktor:ktor-client-ios:$ktor_version"
        }
    }
}
