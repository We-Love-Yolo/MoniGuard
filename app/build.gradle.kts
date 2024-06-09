plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.weloveyolo.moniguard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.weloveyolo.moniguard"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

//        manifestPlaceholders["appAuthRedirectScheme"] = "com.weloveyolo.moniguard"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    lint {
        baseline = file("lint-baseline.xml")
    }

}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.volley)
    implementation(libs.okhttp)
//    implementation(libs.libvlc)
    implementation(files("libs/libvlc-release-3.aar"))
    implementation(libs.activity)
    implementation(libs.appauth)
    implementation(libs.gson)
    implementation(libs.worker)
    implementation (libs.annotation)
    compileOnly(libs.lombok)

    testImplementation(libs.junit)
    annotationProcessor(libs.lombok)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")


}