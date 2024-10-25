import com.android.build.api.dsl.ApkSigningConfig
import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.build.gradle.internal.dsl.SigningConfig
import java.util.Locale

plugins {
    alias(libs.plugins.androidApp)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.firebasePerformance)
}

android {
    compileSdk = App.targetSdkVersion
    namespace = "com.app.app"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
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

    implementation(libs.appCompat)
    implementation(libs.constraintLayout)
    implementation(libs.recycler)

    implementation(libs.multidex)

    implementation(platform(libs.firebaseBom))
    implementation(libs.analytics)
    implementation(libs.firebasePerf)
    implementation(libs.firebaseCrashlytics)

    implementation(libs.coroutinesAndroid)
    implementation(libs.coroutinesPlayServices)

    androidTestImplementation(libs.junitRunner)
}

fun NamedDomainObjectContainer<ApplicationProductFlavor>.createProductFlavor(appFlavor: AppFlavor) =
    create(appFlavor.name.lowercase(Locale.getDefault())) {
        applicationId = appFlavor.appId
    }

fun NamedDomainObjectContainer<out ApkSigningConfig>.createSignInConfig(appFlavor: AppFlavor) =
    create(appFlavor.signInName) {
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
) = getByName(buildType.name.lowercase(Locale.getDefault())) {
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
    val properties = VariantProperties(name.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    })
    // resValue("string", "PROPERTY_NAME", properties.PROPERTY_NAME)
    // buildConfigField("String", "PROPERTY_NAME", properties.PROPERTY_NAME)
}

fun ApplicationVariant.applyProperties() {
    val properties = VariantProperties(name.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    })

    // resValues.put(makeResValueKey("string", "PROPERTY_NAME"), ResValue(properties.PROPERTY_NAME))
    // buildConfigFields.put("PROPERTY_NAME", BuildConfigField("String", properties.PROPERTY_NAME, null))
}