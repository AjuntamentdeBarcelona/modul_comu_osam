const val propertiesDir = "signin/properties"

const val coroutines_version = "1.6.0-native-mt"
const val serialization_version = "1.3.2"
const val ktor_version = "1.6.5"
const val kotlin_version = "1.6.10"
const val google_play_review_version = "2.0.1"
const val google_play_review_ktx_version = "2.0.1"

object App {
    const val applicationId = "cat.bcn.commonmodule"
    const val minSdkVersion = 21
    const val targetSdkVersion = 31
    const val versionCode = 2021050001
    const val versionName = "1.0.1"
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
        const val appCompat = "androidx.appcompat:appcompat:1.4.1"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.3"
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
            const val junit = "junit:junit:4.12"
            const val runner = "com.android.support.test:runner:1.0.2"
        }
    }

    object Root {
        const val android = "com.android.tools.build:gradle:7.1.0"
        const val googleServices = "com.google.gms:google-services:4.3.10"
        const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-gradle:2.8.1"
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
            const val ktorClientJson = "io.ktor:ktor-client-json:$ktor_version"
            const val ktorSerialization = "io.ktor:ktor-client-serialization:$ktor_version"
            const val ktorClientAuth = "io.ktor:ktor-client-auth:$ktor_version"
            const val ktorLogging = "io.ktor:ktor-client-logging:$ktor_version"
        }

        object Android {
            const val serialization =
                "org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version"
            const val ktorClientCore = "io.ktor:ktor-client-okhttp:$ktor_version"
            const val androidPlayReview =
                "com.google.android.play:review:$google_play_review_version"
            const val androidPlayReviewKtx =
                "com.google.android.play:review-ktx:$google_play_review_ktx_version"
        }

        object Native {
            const val serialization =
                "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serialization_version"
            const val ktorClientCore = "io.ktor:ktor-client-ios:$ktor_version"
        }
    }
}
