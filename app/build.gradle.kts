plugins {
    id("com.android.application")
}

android {
    namespace = "net.idscan.components.android.dvs.test"

    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        minSdk = 23
        targetSdk = 34

        applicationId = "net.idscan.components.android.dvs.test"
        versionCode = 19
        versionName = "1.13.0"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("net.idscan.components.android:dvs:1.13.1")
}
