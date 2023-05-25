buildscript {
    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.0.1")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}