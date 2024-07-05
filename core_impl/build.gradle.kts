plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
//    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id ("kotlin-kapt")
}

android {
    namespace = "com.itskidan.core_impl"
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
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
    // Modules
    api(project(":core_api"))
    // Room
    implementation (libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)
    kapt (libs.androidx.room.compiler)
    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    // LifeCycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Timber
    implementation(libs.timber)
    // Dagger
    implementation (libs.dagger.android)
    kapt(libs.dagger.android.compiler)
//    implementation (libs.dagger.android.support)
//    annotationProcessor (libs.dagger.android.processor)
}