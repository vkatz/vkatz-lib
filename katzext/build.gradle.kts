import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(28)
        versionName = "3.0"
        versionCode = 16
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin")
        }
    }
}

androidExtensions {
    //isExperimental = true
    configure(delegateClosureOf<AndroidExtensionsExtension> {
        isExperimental = true
    })
}

dependencies {
    api("androidx.appcompat:appcompat:1.0.2")
    api("androidx.recyclerview:recyclerview:1.0.0")
    api("androidx.constraintlayout:constraintlayout:1.1.3")
    api("androidx.lifecycle:lifecycle-extensions:2.0.0")
    //api ("androidx.browser:browser:1.0.0")

    //api ("com.google.android.material:material:1.0.0")
    //api ("com.google.android.gms:play-services-maps:16.0.0")
    //api ("com.google.android.gms:play-services-location:16.0.0")

    api("android.arch.navigation:navigation-fragment-ktx:1.0.0-alpha07")
    api("android.arch.navigation:navigation-ui-ktx:1.0.0-alpha07")

    api("org.jetbrains.kotlin:kotlin-stdlib:1.3.10")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.0.0")
}