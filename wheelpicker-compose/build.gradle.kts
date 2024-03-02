import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "io.woong.wheelpicker.compose"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api(project(":wheelpicker"))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    coordinates("io.woong.wheelpicker", "wheelpicker-compose", "${project.version}")

    pom {
        name.set("wheelpicker-compose")
        description.set("Yet another Android wheel picker library, for Jetpack Compose.")
        url.set("https://github.com/cheonjaeung/wheelpicker")

        licenses {
            license {
                name.set("Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("cheonjaeung")
                name.set("Jaeung Cheon")
                email.set("cheonjaewoong@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/cheonjaeung/wheelpicker")
            connection.set("scm:git:git://github.com/cheonjaeung/wheelpicker.git")
            developerConnection.set("scm:git:ssh://git@github.com/cheonjaeung/wheelpicker.git")
        }
    }
}
