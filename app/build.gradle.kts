import java.io.FileInputStream
import java.util.Properties

val propertiesConfig = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.projetogpsoliverpedro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projetogpsoliverpedro"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "GOOGLE_MAPS_API_KEY",
                    "\"${propertiesConfig.getProperty("google.maps.api.key")}\"")
        manifestPlaceholders["googleMapsApiKey"] = propertiesConfig.getProperty("google.maps.api.key")

        val googleGeocodingApiKey = propertiesConfig.getProperty("google.geocoding.api.key") ?: "null"
        buildConfigField("String", "GOOGLE_GEOCODING_API_KEY", "\"${googleGeocodingApiKey}\"")

        val googleMapsStaticApiKey = propertiesConfig.getProperty("google.mapsstatic.api.key") ?: "null"
        buildConfigField("String", "GOOGLE_MAPSSTATIC_API_KEY", "\"${googleMapsStaticApiKey}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.play.services.location)
    implementation(libs.androidx.preference.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}