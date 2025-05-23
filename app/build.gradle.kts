plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.spend"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.spend"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.compose.material:material-icons-extended:<version>")
    // Use the Compose BOM to align versions of all Compose libraries
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))

    // Jetpack Compose UI Libraries
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.foundation:foundation")
    // Optional Material3 component (if you choose to use it)
    implementation("androidx.compose.material3:material3:1.1.0")

    // Tooling (preview and debug)
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Activity integration with Compose
    implementation("androidx.activity:activity-compose:1.7.2")

    // Navigation Compose for in-app navigation
    implementation("androidx.navigation:navigation-compose:2.7.2")

    // Lifecycle runtime for managing app lifecycle events
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Ensure your test dependencies use the same Compose BOM version
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    // On-device text recognition
    implementation ("com.google.mlkit:text-recognition:16.0.0")
}