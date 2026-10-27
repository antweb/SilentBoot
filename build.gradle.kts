buildscript {
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}


allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
