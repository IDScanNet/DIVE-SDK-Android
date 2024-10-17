plugins {
    id("com.android.application")
}

android {
    namespace = "net.idscan.components.android.dvs.test"

    compileSdk = 34

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        minSdk = 23
        targetSdk = 34

        applicationId = "net.idscan.components.android.dvs.test"
        versionCode = 12
        versionName = "1.6.0"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("net.idscan.components.android:dvs:1.7.0")
}
