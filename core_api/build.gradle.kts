plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
//    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id ("kotlin-kapt")
}

android {
    namespace = "com.itskidan.core_api"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    kapt ("androidx.compose.compiler:compiler:1.5.14")

    // Room
    implementation (libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    // LifeCycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Timber
    implementation(libs.timber)
}