import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "io.woong.snappicker"
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
    implementation("androidx.appcompat:appcompat:1.6.1")
    api("androidx.recyclerview:recyclerview:1.2.1")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    coordinates("io.woong.snappicker", "snappicker", "${project.version}")

    pom {
        name.set("snappicker-compose")
        description.set("High customizable Android picker library which displays values as scrollable list.")
        url.set("https://github.com/cheonjaewoong/snappicker")

        licenses {
            license {
                name.set("MIT")
                url.set("https://github.com/cheonjaewoong/snappicker/blob/master/LICENSE.txt")
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
            url.set("https://github.com/cheonjaewoong/snappicker")
            connection.set("scm:git:git://github.com/cheonjaewoong/snappicker.git")
            developerConnection.set("scm:git:ssh://git@github.com/cheonjaewoong/snappicker.git")
        }
    }
}
