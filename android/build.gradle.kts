import com.android.build.api.dsl.ApkSigningConfig
import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.build.gradle.internal.dsl.SigningConfig

plugins {
    id("com.android.application")

    kotlin("android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = App.targetSdkVersion

    defaultConfig {
        minSdk = App.minSdkVersion
        targetSdk = App.targetSdkVersion
        applicationId = App.applicationId
        versionCode = App.versionCode
        versionName = App.versionName
        testInstrumentationRunner = App.testInstrumentationRunner

        multiDexEnabled = true
    }

    buildTypes {
        val proguard = getDefaultProguardFile("proguard-android.txt")
        createBuildType(AppBuildType.Release, productFlavors, signingConfigs, proguard)
    }

    applicationVariants.all { applyProperties() }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    lint {
        abortOnError = false
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation(project(":common"))

    implementation(Dependencies.Android.appCompat)
    implementation(Dependencies.Android.constraintLayout)
    implementation(Dependencies.Android.recycler)

    implementation(Dependencies.Android.multidex)

    implementation(platform(Dependencies.Android.firebaseBoM))
    implementation(Dependencies.Android.analytics)
    implementation(Dependencies.Android.firebasePerf)
    implementation(Dependencies.Android.firebaseCrashlytics)

    implementation(Dependencies.Android.coroutinesAndroid)
    implementation(Dependencies.Android.coroutinesPlayServices)

    androidTestImplementation(Dependencies.Android.Test.runner)
}

fun NamedDomainObjectContainer<ApplicationProductFlavor>.createProductFlavor(appFlavor: AppFlavor) =
    create(appFlavor.name.toLowerCase()) {
        applicationId = appFlavor.appId
    }

fun NamedDomainObjectContainer<out ApkSigningConfig>.createSignInConfig(appFlavor: AppFlavor) = create(appFlavor.signInName) {
        val path = "${rootDir.absolutePath}/$propertiesDir"
        storeFile = getSignFile(rootDir.absolutePath, "$path/${appFlavor.signInFile}")
        storePassword = getSignFilePassword("$path/${appFlavor.signInFile}")
        keyAlias = getSignAlias("$path/${appFlavor.signInFile}")
        keyPassword = getSignAliasPassword("$path/${appFlavor.signInFile}")
        storeFile?.printFileName()
    }

fun NamedDomainObjectContainer<ApplicationBuildType>.createBuildType(
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
        /*
        flavors.getByName(AppFlavor.Demo.name.toLowerCase()).signingConfig =
            signingConfigs.getByName(AppFlavor.Demo.signInName)
        flavors.getByName(AppFlavor.Jenkins.name.toLowerCase()).signingConfig =
            signingConfigs.getByName(AppFlavor.Jenkins.signInName)
         */
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

fun ApplicationVariant.applyProperties() {
    val properties = VariantProperties(name.capitalize())

    // resValues.put(makeResValueKey("string", "PROPERTY_NAME"), ResValue(properties.PROPERTY_NAME))
    // buildConfigFields.put("PROPERTY_NAME", BuildConfigField("String", properties.PROPERTY_NAME, null))
}