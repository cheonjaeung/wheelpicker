import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "${project.group}"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    api(libs.androidx.recylerview)
    implementation(libs.simplecarousel)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates("${project.group}", "powerwheelpicker", "${project.version}")

    pom {
        name.set("PowerWheelPicker for Android")
        description.set("PowerWheelPicker is a highly customizable wheel picker view for Android.")
        url.set("https://github.com/cheonjaeung/powerwheelpicker-android")

        licenses {
            license {
                name.set("Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("cheonjaeung")
                name.set("Cheon Jaeung")
                email.set("cheonjaewoong@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/cheonjaeung/powerwheelpicker-android")
            connection.set("scm:git:git://github.com/cheonjaeung/powerwheelpicker-android.git")
            developerConnection.set("scm:git:ssh://git@github.com/cheonjaeung/powerwheelpicker-android.git")
        }
    }
}
