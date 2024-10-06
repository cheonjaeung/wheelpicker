import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false
}

allprojects {
    group = "com.cheonjaeung.powerwheelpicker.android"
    version = "0.3.0"

    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}
