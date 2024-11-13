# Item Effect

The `WheelPicker` view has an interface to apply customized visual effects to the item views.

## ItemEffector

`ItemEffector` is a special class to apply visual effects with view animation properties.
Create an effector and add it to the `WheelPicker`.

```kotlin
class CustomEffector : WheelPicker.ItemEffector() {
    // ...
}

wheelPicker.addItemEffector(CustomEffector())
```

## Examples

This is simple examples of how to use `ItemEffector` interface.

### Effects on scrolling

You can use `ItemEffector.applyEffectOnScrolled` to apply visual effect for scrolling.
This callback is called when scroll is consumed.

For example, you can make custom alpha effect like this:

```kotlin
class AlphaEffector : WheelPicker.ItemEffector() {
    override fun applyEffectOnScrolled(view: View, delta: Int, positionOffset: Int, centerOffset: Int) {
        val density = view.resources.displayMetrics.density
        view.alpha = 1f - abs(centerOffset) / (250 * density)
    }
}
```

<video style="width:60%" loop muted autoplay>
    <source src="../videos/alpha_effect.mp4" type="video/mp4">
</video>

### Effects on scroll state changing

You can use `ItemEffector.applyEffectOnScrollStateChanged` to apply visual effect when scroll state changed.

For example, you can make text color effect by combining with `ItemEffector.applyEffectOnScrolled`.

```kotlin
private class TextColorEffector(val effectColor: Int, val normalColor: Int) : WheelPicker.ItemEffector() {
    private var colorAnimator: ValueAnimator? = null
    private var targetColor = effectColor
    private var currentColor = normalColor

    override fun applyEffectOnScrollStateChanged(view: View, newState: Int, positionOffset: Int, centerOffset: Int) {
        val textView = view.findViewById<TextView>(R.id.text)
        targetColor = if (newState != WheelPicker.SCROLL_STATE_IDLE) {
            effectColor
        } else {
            normalColor
        }
        colorAnimator = ValueAnimator.ofArgb(textView.currentTextColor, targetColor).apply {
            duration = 500
            addUpdateListener {
                currentColor = it.animatedValue as Int
                textView?.setTextColor(currentColor)
            }
        }
        colorAnimator?.start()
    }

    override fun applyEffectOnScrolled(view: View, delta: Int, positionOffset: Int, centerOffset: Int) {
        val textView = view.findViewById<TextView>(R.id.text)
        textView?.setTextColor(currentColor)
    }
}
```

<video style="width:60%" loop muted autoplay>
    <source src="../videos/text_color_effect.mp4" type="video/mp4">
</video>

### Effects on item selecting

You can use `ItemEffector.applyEffectOnItemSelected` to apply effect for item selecting.

For example, you can make scale effect when item is selected.

```kotlin
class SelectedScaleEffector : WheelPicker.ItemEffector() {
    override fun applyEffectOnItemSelected(view: View, position: Int) {
        ValueAnimator.ofFloat(1.0f, 1.2f, 1.0f).apply {
            duration = 250
            addUpdateListener {
                val scale = it.animatedValue as Float
                view.scaleX = scale
                view.scaleY = scale
            }
        }.start()
    }
}
```

<video style="width:60%" loop muted autoplay>
    <source src="../videos/scale_effect.mp4" type="video/mp4">
</video>
