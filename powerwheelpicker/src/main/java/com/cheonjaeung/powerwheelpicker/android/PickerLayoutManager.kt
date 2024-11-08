package com.cheonjaeung.powerwheelpicker.android

import androidx.recyclerview.widget.RecyclerView
import com.cheonjaeung.simplecarousel.android.CarouselLayoutManager

internal class PickerLayoutManager(
    @WheelPicker.Orientation orientation: Int,
    circular: Boolean,
    itemWidth: Int,
    itemHeight: Int
) : CarouselLayoutManager(orientation, circular) {
    var itemWidth: Int = itemWidth
        set(value) {
            field = value
            requestLayout()
        }

    var itemHeight: Int = itemHeight
        set(value) {
            field = value
            requestLayout()
        }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return if (orientation == HORIZONTAL) {
            RecyclerView.LayoutParams(
                itemWidth,
                RecyclerView.LayoutParams.MATCH_PARENT
            )
        } else {
            RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                itemHeight
            )
        }
    }
}
