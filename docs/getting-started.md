# Getting Started

PowerWheelPicker for Android provides a view named `WheelPicker`.
It requires at least 2 components to work correctly, the view itself and the adapter.

## WheelPicker View

You can place this view into your XML layout or initialize it programmatically.

```xml
<com.cheonjaeung.powerwheelpicker.android.WheelPicker
    android:id="@+id/wheelPicker"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical" />
```

The `WheelPicker` has some XML attributes.
You can set attributes like below:

| Attribute           | Type                       | Description                            |
|---------------------|----------------------------|----------------------------------------|
| android:orientation | enum(horizontal, vertical) | Sets orientation of the `WheelPicker`. |
| app:circular        | boolean                    | If `true`, enable circular mode.       |
| app:selectorWidth   | dimension                  | Size of the selector area.             |
| app:selectorHeight  | dimension                  | Size of the selector area.             |

## Adapter

`WheelPicker` is implemented based on `RecyclerView` and it needs `RecyclerView.Adapter`.
Creates an adapter and set it to your `WheelPicker`.

```kotlin
val wheelPicker = findViewById<WheelPicker>(R.id.wheelPicker)
wheelPicker.adapter = SampleAdapter()

class SampleAdapter : RecyclerView.Adapter<SampleAdapter.Holder>() {
    // ...

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ...
    }
}
```

!!! warning
    `RecyclerView.ViewHolder` must have `match_parent` layout params.

The advantage of using `RecyclerView` is that it recycles views off the screen and reuses views instead of creating new views every time.
It can help to use less memory.
And also the `RecyclerView.Adapter` is well-known framework to make view that handles dynamic dataset.
You can make any picker what you want.

## Circular

The `WheelPicker` has `circular` option.
When the `circular` is enabled, the `WheelPicker` connects the first/last item to the last/first item.

- Circular: true

<video style="width:60%" loop muted autoplay>
    <source src="../videos/circular_true.mp4" type="video/mp4">
</video>

- Circular: false

<video style="width:60%" loop muted autoplay>
    <source src="../videos/circular_false.mp4" type="video/mp4">
</video>

## OnItemSelectedListener

`WheelPicker` has `OnItemSelectedListener` to receive item selecting event.
`OnItemSelectedListener` callback is invokes whenever an item is selected that means the item view is positioned at the center.

```kotlin
wheelPicker.addOnItemSelectedListener { wheelPicker, position ->
    // ...
}
```

!!! info
    The `position` parameter of the `OnItemSelectedListener` is an adapter position.
