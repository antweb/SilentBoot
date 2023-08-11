plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.8"
    }
    namespace = "com.antweb.silentboot"
}

dependencies {
    val androidxNavigationVersion = "2.6.0"

//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("androidx.preference:preference:1.2.0")
    implementation("com.google.android.material:material:1.9.0")

    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui")

    implementation("androidx.activity:activity-compose:1.7.2")

    implementation("androidx.navigation:navigation-ui-ktx:$androidxNavigationVersion")
    implementation("androidx.navigation:navigation-compose:$androidxNavigationVersion")
}
