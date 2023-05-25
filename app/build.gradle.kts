plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.8.21"
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.antweb.silentboot"
        minSdk = 28
        targetSdk = 34
        versionCode = 25
        versionName = "3.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
    namespace = "com.antweb.silentboot"
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference:1.2.0")
    implementation("com.google.android.material:material:1.9.0")
}
