# SnapPicker

![android-sdk](https://img.shields.io/badge/android-21+-brightgreen?logo=android)
![maven-central](https://img.shields.io/maven-central/v/io.woong.snappicker/snappicker-compose)
![license](https://img.shields.io/badge/license-MIT-blue)

SnapPicker is an Android library for scrollable picker with bundled date time picker.

**This project is under development and experimental.**

## Installation

This library is published to maven central.
Add library to your module's dependencies in gradle.

```groovy
dependencies {
    implementation "io.woong.snappicker:snappicker-compose:$version"
}
```

You can find versions from [GitHub's releases](https://github.com/cheonjaewoong/snappicker/releases)
or [Maven Search](https://search.maven.org/search?q=io.woong.snappicker).

<details>
    <summary><b>Snapshot versions are available via Sonatype's snapshot repository.</b></summary>

### Add snapshot repository to your project

```groovy
repositories {
    maven { url 'https://s01.oss.sonatype.org/content/repositories/snapshots/' }
}
```

### Add library to your module

```groovy
dependencies {
    implementation "io.woong.snappicker:snappicker-compose:$version-SNAPSHOT"
}
```

You can find snapshot versions from [Sonatype's snapshot repository](https://s01.oss.sonatype.org/content/repositories/snapshots/io/woong/snappicker/).

</details>

## License

This project is licensed under the MIT license.
