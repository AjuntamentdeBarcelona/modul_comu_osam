import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.build.gradle.internal.dsl.SigningConfig

plugins {
    id("com.android.application")

    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

repositories {
    maven(url = "https://jitpack.io")
    flatDir {
        dirs = mutableSetOf(File("libs"))
    }
}

android {
    compileSdkVersion(App.targetSdkVersion)

    defaultConfig {
        minSdkVersion(App.minSdkVersion)
        targetSdkVersion(App.targetSdkVersion)
        versionCode = App.versionCode
        versionName = App.versionName
        testInstrumentationRunner = App.testInstrumentationRunner

        multiDexEnabled = true
    }

    flavorDimensions("version")

    productFlavors {
        createProductFlavor(AppFlavor.Demo)
        createProductFlavor(AppFlavor.Jenkins)
    }

    signingConfigs {
        getByName("debug") {
        }
        // createSignInConfig(AppFlavor.AppName1)
        // createSignInConfig(AppFlavor.AppName2)
    }

    buildTypes {
        val proguard = getDefaultProguardFile("proguard-android.txt")
        createBuildType(AppBuildType.Debug, productFlavors, signingConfigs, proguard)
        createBuildType(AppBuildType.Release, productFlavors, signingConfigs, proguard)
    }

    applicationVariants.all { applyProperties() }

    packagingOptions {
        exclude("META-INF/*")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lintOptions {
        isAbortOnError = false
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(kotlin("stdlib-jdk7", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION))

    implementation(project(":common"))

    implementation(Dependencies.Android.appCompat)
    implementation(Dependencies.Android.constraintLayout)
    implementation(Dependencies.Android.recycler)

    implementation(Dependencies.Android.multidex)
    implementation(Dependencies.Android.playServices)

    implementation(Dependencies.Android.firebaseCore)
    implementation(Dependencies.Android.analytics)
    implementation(Dependencies.Android.firebasePerf)
    implementation(Dependencies.Android.firebaseCrashlytics)

    implementation(Dependencies.Android.coroutinesAndroid)
    implementation(Dependencies.Android.coroutinesPlayServices)

    androidTestImplementation(Dependencies.Android.Test.runner)
}

fun NamedDomainObjectContainer<ProductFlavor>.createProductFlavor(appFlavor: AppFlavor) =
    create(appFlavor.name.toLowerCase()) {
        applicationId = appFlavor.appId
    }

fun NamedDomainObjectContainer<SigningConfig>.createSignInConfig(appFlavor: AppFlavor) = create(appFlavor.signInName) {
    val path = "${rootDir.absolutePath}/$propertiesDir"
    storeFile = getSignFile("$path/${appFlavor.signInFile}")
    storePassword = getSignFilePassword("$path/${appFlavor.signInFile}")
    keyAlias = getSignAlias("$path/${appFlavor.signInFile}")
    keyPassword = getSignAliasPassword("$path/${appFlavor.signInFile}")
    storeFile?.printFileName()
}

fun NamedDomainObjectContainer<com.android.build.gradle.internal.dsl.BuildType>.createBuildType(
    buildType: AppBuildType,
    flavors: NamedDomainObjectContainer<ProductFlavor>,
    signingConfigs: NamedDomainObjectContainer<SigningConfig>,
    proguardFile: File
) = getByName(buildType.name.toLowerCase()) {
    isMinifyEnabled = buildType.minified
    isDebuggable = buildType.debuggable
    applicationIdSuffix = buildType.idSuffix
    versionNameSuffix = buildType.nameSuffix

    if (buildType == AppBuildType.Release) {
        // flavors.getByName(AppFlavor.AppName1.name.toLowerCase()).signingConfig =
        //     signingConfigs.getByName(AppFlavor.AppName1.signInName)
        // flavors.getByName(AppFlavor.AppName2.name.toLowerCase()).signingConfig =
        //     signingConfigs.getByName(AppFlavor.AppName2.signInName)
    } else {
        signingConfig = signingConfigs.getByName(buildType.signInConfig)
    }

    proguardFiles(proguardFile, "proguard-rules.pro")
}

fun com.android.build.gradle.api.ApplicationVariant.applyProperties() {
    val properties = VariantProperties(name.capitalize())
    // resValue("string", "PROPERTY_NAME", properties.PROPERTY_NAME)
    // buildConfigField("String", "PROPERTY_NAME", properties.PROPERTY_NAME)
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()

    kotlinOptions {
        jvmTarget = "1.8"
    }
}
