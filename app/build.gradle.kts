import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.shak.taskmanagerapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shak.taskmanagerapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    val secretProperties = Properties()
    val secretPropertiesFile = File(rootDir,"secrets.properties")
    if(secretPropertiesFile.exists() && secretPropertiesFile.isFile){
        secretPropertiesFile.inputStream().use {
            secretProperties.load(it)
        }
    }

    buildTypes {

        debug {

            // Secret keys
            buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${secretProperties.getProperty("GOOGLE_WEB_CLIENT_ID")}\"")
            buildConfigField("String", "FACEBOOK_APP_ID", "\"${secretProperties.getProperty("FACEBOOK_APP_ID")}\"")
            buildConfigField("String", "FB_LOGIN_PROTOCOL_SCHEME", "\"${secretProperties.getProperty("FB_LOGIN_PROTOCOL_SCHEME")}\"")
            buildConfigField("String", "FACEBOOK_CLIENT_TOKEN", "\"${secretProperties.getProperty("FACEBOOK_CLIENT_TOKEN")}\"")
            buildConfigField("String", "MAIN_PREFERENCE_KEY", "\"${secretProperties.getProperty("MAIN_PREFERENCE_KEY")}\"")
            buildConfigField("String", "IS_LOGGED_OUT_KEY", "\"${secretProperties.getProperty("IS_LOGGED_OUT_KEY")}\"")

            
            // Add manifest placeholders for direct manifest access
            // For google web client id
            manifestPlaceholders["googleWebClientId"] = secretProperties.getProperty("GOOGLE_WEB_CLIENT_ID")

            // For facebook
            manifestPlaceholders["facebookAppId"] = secretProperties.getProperty("FACEBOOK_APP_ID")
            manifestPlaceholders["fbLoginProtocolScheme"] = secretProperties.getProperty("FB_LOGIN_PROTOCOL_SCHEME")
            manifestPlaceholders["facebookClientToken"] = secretProperties.getProperty("FACEBOOK_CLIENT_TOKEN")

        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Secret keys
            buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${secretProperties.getProperty("GOOGLE_WEB_CLIENT_ID")}\"")
            buildConfigField("String", "FACEBOOK_APP_ID", "\"${secretProperties.getProperty("FACEBOOK_APP_ID")}\"")
            buildConfigField("String", "FB_LOGIN_PROTOCOL_SCHEME", "\"${secretProperties.getProperty("FB_LOGIN_PROTOCOL_SCHEME")}\"")
            buildConfigField("String", "FACEBOOK_CLIENT_TOKEN", "\"${secretProperties.getProperty("FACEBOOK_CLIENT_TOKEN")}\"")
            buildConfigField("String", "MAIN_PREFERENCE_KEY", "\"${secretProperties.getProperty("MAIN_PREFERENCE_KEY")}\"")
            buildConfigField("String", "IS_LOGGED_OUT_KEY", "\"${secretProperties.getProperty("IS_LOGGED_OUT_KEY")}\"")

        }
    }

    buildFeatures{
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.facebook.android.sdk)
    implementation(libs.lottie)
    implementation(libs.tbuonomo)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}