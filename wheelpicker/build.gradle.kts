import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "io.woong.wheelpicker"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
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
    implementation(libs.androidx.appcompat)
    api(libs.androidx.recylerview)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    coordinates("io.woong.wheelpicker", "wheelpicker", "${project.version}")

    pom {
        name.set("wheelpicker-compose")
        description.set("Yet another Android wheel picker library.")
        url.set("https://github.com/cheonjaewoong/wheelpicker")

        licenses {
            license {
                name.set("Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("cheonjaewoong")
                name.set("Jaewoong Cheon")
                email.set("cheonjaewoong@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/cheonjaewoong/wheelpicker")
            connection.set("scm:git:git://github.com/cheonjaewoong/wheelpicker.git")
            developerConnection.set("scm:git:ssh://git@github.com/cheonjaewoong/wheelpicker.git")
        }
    }
}
