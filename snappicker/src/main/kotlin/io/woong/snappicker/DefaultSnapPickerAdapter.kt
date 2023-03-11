package io.woong.snappicker

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.roundToInt

/**
 * Default implementation of the [SnapPickerAdapter].
 */
internal class DefaultSnapPickerAdapter<T>(
    private val maxItemSize: Int
) : SnapPickerAdapter<T, AppCompatTextView>() {

    override fun getMaxItemSize(context: Context): Int {
        return if (maxItemSize == SnapPickerView.DEFAULT_MAX_ITEM_SIZE) {
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                48f,
                context.resources.displayMetrics
            ).roundToInt()
        } else {
            maxItemSize
        }
    }

    override fun createItemView(context: Context): AppCompatTextView {
        val textView = AppCompatTextView(context)
        textView.gravity = Gravity.CENTER
        return textView
    }

    override fun bindItemView(itemView: AppCompatTextView, position: Int) {
        val value = getValue(position)
        itemView.text = value.toString()
    }
}
