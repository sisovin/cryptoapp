import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.peanech.cryptoapp"
    compileSdk = 36


    defaultConfig {
        applicationId = "com.peanech.cryptoapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "COINGECKO_BASE_URL", "\"https://api.coingecko.com/api/v3\"")
        buildConfigField("String", "DEFAULT_VS_CURRENCY", "\"usd\"")
        buildConfigField("int", "MARKETS_PAGE_SIZE", "50")
        buildConfigField("long", "CACHE_TTL_MARKETS_MS", "60000L")
        buildConfigField("long", "CACHE_TTL_DETAIL_MS", "300000L")
    }

    signingConfigs {
        create("release") {
            storeFile = file("../../release.keystore")
            storePassword = "password"
            keyAlias = "release"
            keyPassword = "password"
        }
        create("upload") {
            storeFile = file("../../upload.keystore")
            storePassword = "password"
            keyAlias = "upload"
            keyPassword = "password"
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-project.txt")
            isDebuggable = project.properties["enableLogsInRelease"]?.toString()?.toBoolean() ?: false
            // signingConfig = signingConfigs.getByName("release")
        }
        create("upload") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-project.txt")
            isDebuggable = project.properties["enableLogsInRelease"]?.toString()?.toBoolean() ?: false
            // signingConfig = signingConfigs.getByName("upload")
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
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/NOTICE.md"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.material) // Add Material library for themes


    // Firebase
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Coil
    implementation(libs.io.coil.kt.coil.compose)

    // Modules
    implementation(project(":domain"))
    // implementation(project(":data"))
    implementation(project(":core-ui"))
    implementation(project(":testing"))

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}