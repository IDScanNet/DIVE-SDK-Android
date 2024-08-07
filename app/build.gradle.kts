plugins {
    id("com.android.application")
}

android {
    namespace = "net.idscan.components.android.dvs.test"

    compileSdk = 33

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        minSdk = 23
        targetSdk = 33

        applicationId = "net.idscan.components.android.dvs.test"
        versionCode = 11
        versionName = "1.5.1"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("net.idscan.components.android:dvs:1.5.1")
}
