// Carrega propriedades do gradle.properties
val kotlinVersion: String by project
val hiltVersionApp: String by project
val composeBom: String by project

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.psychonautwiki.journal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.psychonautwiki.journal"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Bibliotecas básicas do Android
    implementation("androidx.core:core-ktx:1.12.0")         // Extensões Kotlin para Android (Core KTX)
    implementation("androidx.core:core-splashscreen:1.2.0") // Splash Screen API
    implementation("androidx.appcompat:appcompat:1.7.0")    // Compatibilidade com versões antigas
    implementation("com.google.android.material:material:1.10.0") // Componentes Material Design

    // -------------------------
    // Jetpack Compose UI (via BOM para versões compatíveis)
    // -------------------------
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")                // Núcleo do Compose
    implementation("androidx.compose.ui:ui-graphics")       // APIs gráficas do Compose
    implementation("androidx.compose.ui:ui-tooling-preview")// Preview do Compose no Android Studio
    implementation("androidx.compose.material:material")    // Componentes Material do Compose
    implementation("androidx.activity:activity-compose:1.8.1") // Atividades com Compose

    val lifecycleVersion = "2.8.7"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")

    implementation("androidx.navigation:navigation-compose:2.8.9")

    implementation("com.google.dagger:hilt-android:2.56.2")
    kapt("com.google.dagger:hilt-compiler:2.56.2")

    implementation("androidx.datastore:datastore-preferences:1.1.5")

    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

    debugImplementation("androidx.compose.ui:ui-tooling")
}