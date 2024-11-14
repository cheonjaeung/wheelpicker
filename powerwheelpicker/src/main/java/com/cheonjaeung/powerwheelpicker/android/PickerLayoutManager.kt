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
            if (value != field) {
                field = value
                requestLayout()
            }
        }

    var itemHeight: Int = itemHeight
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
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

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (isItemSizeInvalid()) {
            return
        }
        super.onLayoutChildren(recycler, state)
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        if (isItemSizeInvalid()) {
            return 0
        }
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        if (isItemSizeInvalid()) {
            return 0
        }
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    private fun isItemSizeInvalid(): Boolean {
        return (orientation == HORIZONTAL && itemWidth == 0) || (orientation == VERTICAL && itemHeight == 0)
    }
}
