const val propertiesDir = "signin/properties"

const val coroutines_version = "1.3.5-native-mt"
const val kodein_version = "6.4.0"
const val serialization_version = "0.20.0"
const val fragment_version = "1.1.0"
const val ktor_version = "1.3.2"
const val crashlytics_version = "2.9.8"
const val settings_version = "0.6"
const val dexter_version = "6.1.0"
const val kotlin_version = "1.3.71"
const val sqldelight_version = "1.3.0"

const val cocoapods_version = "0.6"

const val realm_version = "5.7.0"

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
        const val material = "com.google.android.material:material:1.2.0-alpha03"
        const val fragment = "androidx.fragment:fragment:$fragment_version"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:$fragment_version"
        const val appCompatLegacy = "androidx.legacy:legacy-support-v4:1.0.0"
        const val browser = "androidx.browser:browser:1.0.0"
        const val multidex = "androidx.multidex:multidex:2.0.0"

        // Play Services
        const val playPrefix = "com.google.android.gms:play-services"

        const val playServices = "$playPrefix-base:16.0.0"
        const val playServicesAuthPhone = "$playPrefix-auth-api-phone:16.0.0"
        const val playServicesAuth = "$playPrefix-auth:16.0.0"
        const val playServicesLocation = "$playPrefix-location:16.0.0"
        const val playServicesMaps = "$playPrefix-maps:16.1.0"
        const val playServicesGcm = "$playPrefix-gcm:16.1.0"
        const val playServicesVision = "$playPrefix-vision:17.0.2"

        // Firebase
        const val firebaseCore = "com.google.firebase:firebase-core:16.0.8"
        const val firebaseAnalytics = "com.google.firebase:firebase-analytics:17.2.1"
        const val firebasePerf = "com.google.firebase:firebase-perf:19.0.5"
        const val crashlytics = "com.crashlytics.sdk.android:crashlytics:$crashlytics_version@aar" // transitive = true

        // Kodein
        const val kodeinJvm = "org.kodein.di:kodein-di-generic-jvm:$kodein_version"
        const val kodeinX = "org.kodein.di:kodein-di-framework-android-x:$kodein_version"

        // Coroutines
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version"
        const val coroutinesPlayServices = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutines_version"

        // Other
        const val glide = "com.github.bumptech.glide:glide:4.9.0"
        const val httpClient = "cz.msebera.android:httpclient:4.4.1.2"
        const val barcode = "me.dm7.barcodescanner:zxing:1.9.13" // exclude group: 'com.google.android.gms'
        const val dexter = "com.karumi:dexter:$dexter_version"

        object Test {
            const val junit = "junit:junit:4.12"
            const val runner = "com.android.support.test:runner:1.0.2"
        }

        object Data {
            const val fabric = "com.crashlytics.sdk.android:crashlytics:$crashlytics_version@aar"
        }
    }

    object Root {
        const val android = "com.android.tools.build:gradle:3.5.3"
        const val googlePlay = "com.google.gms:google-services:4.2.0"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:2.0.0-beta04"
        const val fabric = "io.fabric.tools:gradle:1.28.1"
        const val serialization = "org.jetbrains.kotlin:kotlin-serialization:$kotlin_version"
        const val cocoapods = "co.touchlab:kotlinnativecocoapods:$cocoapods_version"
        const val realm = "io.realm:realm-gradle-plugin:$realm_version"
        const val sqldelight = "com.squareup.sqldelight:gradle-plugin:$sqldelight_version"
    }

    object Common {
        object Main {
            const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$coroutines_version"
            const val serialization =
                "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serialization_version"

            const val ktorClientCore = "io.ktor:ktor-client-core:$ktor_version"
            const val ktorClientJson = "io.ktor:ktor-client-json:$ktor_version"
            const val ktorSerialization = "io.ktor:ktor-client-serialization:$ktor_version"
            const val ktorClientAuth = "io.ktor:ktor-client-auth:$ktor_version"
            const val ktorLogging = "io.ktor:ktor-client-logging:$ktor_version"


            const val sqldelightRuntime = "com.squareup.sqldelight:runtime:$sqldelight_version"
            const val sqldelightRuntimeJdk = "com.squareup.sqldelight:runtime-jvm:$sqldelight_version"

            const val stately = "co.touchlab:stately-common:1.0.3"
        }

        object Android {
            const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version"
            const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serialization_version"

            const val ktorClientCore = "io.ktor:ktor-client-okhttp:$ktor_version"
            const val ktorClientJson = "io.ktor:ktor-client-json-jvm:$ktor_version"
            const val ktorSerialization = "io.ktor:ktor-client-serialization-jvm:$ktor_version"
            const val ktorClientAuth = "io.ktor:ktor-client-auth-jvm:$ktor_version"
            const val ktorLogging = "io.ktor:ktor-client-logging-jvm:$ktor_version"
            const val sqldelightDriverAndroid = "com.squareup.sqldelight:android-driver:$sqldelight_version"
        }

        object Native {
            const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutines_version"
            const val serialization =
                "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serialization_version"

            const val ktorClientCore = "io.ktor:ktor-client-ios:$ktor_version"
            const val ktorClientJson = "io.ktor:ktor-client-json-native:$ktor_version"
            const val ktorSerialization = "io.ktor:ktor-client-serialization-native:$ktor_version"
            const val ktorClientAuth = "io.ktor:ktor-client-auth-native:$ktor_version"
            const val ktorLogging = "io.ktor:ktor-client-logging-native:$ktor_version"
            const val sqldelightDriverNative = "com.squareup.sqldelight:native-driver:$sqldelight_version"
        }
    }
}
