package com.cheonjaeung.powerwheelpicker.android

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.cheonjaeung.simplecarousel.android.CarouselLayoutManager

internal class PickerLayoutManager(
    @Orientation orientation: Int,
    circular: Boolean
) : CarouselLayoutManager(orientation, circular) {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return if (orientation == HORIZONTAL) {
            RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.MATCH_PARENT
            )
        } else {
            RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }
    }
}
