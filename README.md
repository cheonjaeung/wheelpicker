# PowerWheelPicker for Android

[![ci](https://github.com/cheonjaeung/powerwheelpicker-android/actions/workflows/ci.yml/badge.svg)](https://github.com/cheonjaeung/powerwheelpicker-android/actions/workflows/ci.yml)
[![maven-central](https://img.shields.io/maven-central/v/com.cheonjaeung.powerwheelpicker.android/powerwheelpicker)](https://central.sonatype.com/artifact/com.cheonjaeung.powerwheelpicker.android/powerwheelpicker)
[![Static Badge](https://img.shields.io/badge/License-Apache%202.0-Green)](./LICENSE.txt)

PowerWheelPicker is a highly customizable wheel picker view for Android, backed by RecyclerView and [SimpleCarousel](https://github.com/cheonjaeung/simplecarousel-android).

![sample-gif](./docs/gifs/VideoEditor_20241102_051809_1.gif)

## Installation

To download this library, add dependency to your project.

```kotlin
dependencies {
    implementation("com.cheonjaeung.powerwheelpicker.android:powerwheelpicker:<version>")
}
```

## Getting Started

### Setup

Add `WheelPicker` view to your layout.

```xml
<com.cheonjaeung.powerwheelpicker.android.WheelPicker
    android:id="@+id/wheelPicker"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical" />
```

All supported attributes:

| Attribute           | Type                       | Description                            |
|---------------------|----------------------------|----------------------------------------|
| android:orientation | enum(horizontal, vertical) | Sets orientation of the `WheelPicker`. |
| app:circular        | boolean                    | If `true`, enable circular mode.       |
| app:selectorWidth   | dimension                  | Size of the selector area.             |
| app:selectorHeight  | dimension                  | Size of the selector area.             |

`WheelPicker` needs `RecyclerView.Adapter` to handle data set. Create and add an adapter to `WheelPicker`.

```kotlin
val wheelPicker = findViewById<WheelPicker>(R.id.wheelPicker)
wheelPicker.adapter = SampleAdapter()

class SampleAdapter : RecyclerView.Adapter<SampleAdapter.Holder>() {
    // ...
}
```

### Event Listening

`WheelPicker` supports listeners for scrolling and item selected event.

There are 2 listeners, `WheelPicker.OnScrollListener` and `WheelPicker.OnItemSelectedListener`.
Both listeners can be added and removed via `add` or `remove` prefixed methods.

`OnScrollListener` is a listener to receive scrolling event.
It is same to `RecyclerView.OnScrollListener`.

```kotlin
wheelPicker.addOnScrollListener(object : WheelPicker.OnScrollListener() {
    override fun onScrollStateChanged(wheelPicker: WheelPicker, @ScrollState newState: Int) {
        // Action for scroll state changing.
    }

    override fun onScrolled(wheelPicker: WheelPicker, dx: Int, dy: Int) {
        // Action for scrolling.
    }
})
```

`OnItemSelectedListener` is a listener to receive item selected event.

```kotlin
wheelPicker.addOnItemSelectedListener { _, position ->
    Snackbar.make(findViewById(R.id.main), "Selected position: $position", 1000).show()
}
```

### Visual Effects

`WheelPicker` has `WheelPicker.ItemEffector` to support visual effects like transformation, alpha and others.

`ItemEffector` has 3 callbacks, `applyEffectOnScrollStateChanged`, `applyEffectOnScrolled` and `applyEffectOnItemSelected`.
It makes more flexible to apply visual effect.

```kotlin
wheelPicker.addItemEffector(object : WheelPicker.ItemEffector() {
    // Implementation of custom effector.
})
```

### Sample

This project has sample application in the [sample](./sample) directory.
The sample app provides example of basic usage and visual effects.

## Changelog

Please see [changelog](./CHANGELOG.md) file.

## License

Copyright 2023 Cheon Jaeung.

This project is licensed under the Apache License Version 2.0. See [License file](./LICENSE.txt) for more details.
