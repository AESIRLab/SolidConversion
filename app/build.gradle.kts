plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"

}

android {
    namespace = "com.example.solidconversion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.solidconversion"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.01"
        manifestPlaceholders["appAuthRedirectScheme"] = "com.example.SolidConversion"


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

val version = "0.0.51"
dependencies {
    // Annotations
    implementation("org.aesirlab:sksolidannotations:$version")
    ksp("org.aesirlab:skannotationscompiler:$version")
    implementation("org.aesirlab:authlib:$version")

    implementation (libs.coil.compose)
    implementation(libs.androidx.datastore.preferences)
    ksp("com.squareup:kotlinpoet:1.14.0")
    ksp("com.squareup:kotlinpoet-ksp:1.12.0")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.nimbus.jose.jwt)
    implementation(libs.okhttp)
    implementation(libs.appauth)

    implementation(libs.androidx.activity.compose)
}