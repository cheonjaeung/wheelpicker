package io.woong.snappicker

import android.graphics.Point
import android.graphics.Rect
import androidx.core.graphics.contains
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * An adapter class to invokes scroll event listeners (like [OnPickerScrollListener] or
 * [OnPickerValueSelectedListener]) when [ValuePickerView]'s internal [RecyclerView] are scrolled.
 */
internal class ScrollEventAdapter(
    private val pickerView: ValuePickerView
) : RecyclerView.OnScrollListener() {

    private var onPickerScrollListener: OnPickerScrollListener? = null
    private var onPickerValueSelectedListener: OnPickerValueSelectedListener? = null

    private var previousCenterValueIndex: Int = RecyclerView.NO_POSITION

    internal fun setOnPickerScrollListener(listener: OnPickerScrollListener?) {
        this.onPickerScrollListener = listener
    }

    internal fun setOnPickerValueSelectedListener(listener: OnPickerValueSelectedListener?) {
        this.onPickerValueSelectedListener = listener
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        onPickerScrollListener?.onScrollStateChanged(pickerView, newState)

        if (newState == ValuePickerView.SCROLL_STATE_IDLE) {
            val layoutManager  = recyclerView.layoutManager as LinearLayoutManager
            val currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (currentPosition != RecyclerView.NO_POSITION) {
                val repositionTargetPosition = findRepositionablePosition(currentPosition)
                pickerView.scrollToPosition(repositionTargetPosition)
            }
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        onPickerScrollListener?.onScrolled(pickerView, dx, dy)

        val pickerAdapter = pickerView.adapter
        if (pickerAdapter != null) {
            val centerPosition = findClosestToCenterItemPosition(recyclerView)
            if (centerPosition != RecyclerView.NO_POSITION) {
                val centerValueIndex = centerPosition % pickerAdapter.getValueCount()
                if (centerValueIndex != previousCenterValueIndex) {
                    previousCenterValueIndex = centerValueIndex
                    onPickerValueSelectedListener?.onValueSelected(pickerView, centerValueIndex)
                }
            }
        }
    }

    /**
     * Returns around center position based on given current position for repositioning.
     *
     * A picker which enabled cyclic option has large size of items (Int.MAX_VALUE) for infinite
     * scrolling. To make user feel infinite items, current adapter position should be on around
     * center of adapter (around half of Int.MAX_VALUE). It calculates correct index to reposition
     * when scroll is idle, and move to the index.
     */
    internal fun findRepositionablePosition(currentPosition: Int): Int {
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
        if (!pickerView.isCyclic) {
            return currentPosition
        }
        val pickerAdapter = pickerView.adapter ?: return RecyclerView.NO_POSITION

        val adapterItemCount = pickerAdapter.itemCount
        val valueCount = pickerAdapter.getValueCount()
        val chunkCount = adapterItemCount / valueCount
        val currentValueIndex = currentPosition % valueCount
        val aroundCenterChunkFirstIndex = valueCount * (chunkCount / 2)
        return aroundCenterChunkFirstIndex + currentValueIndex
    }

    /**
     * Returns an item position that is the closest to picker view center.
     */
    private fun findClosestToCenterItemPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleItemView = layoutManager.findViewByPosition(firstVisiblePosition)
            ?: return RecyclerView.NO_POSITION
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        val lastVisibleItemView = layoutManager.findViewByPosition(lastVisiblePosition)
            ?: return RecyclerView.NO_POSITION

        val firstItemViewGlobalBounds = Rect()
        if (!firstVisibleItemView.getGlobalVisibleRect(firstItemViewGlobalBounds)) {
            return RecyclerView.NO_POSITION
        }
        val lastItemViewGlobalBounds = Rect()
        if (!lastVisibleItemView.getGlobalVisibleRect(lastItemViewGlobalBounds)) {
            return RecyclerView.NO_POSITION
        }
        val recyclerViewGlobalBounds = Rect()
        if (!recyclerView.getGlobalVisibleRect(recyclerViewGlobalBounds)) {
            return RecyclerView.NO_POSITION
        }

        val recyclerViewCenter = if (pickerView.orientation == ValuePickerView.ORIENTATION_HORIZONTAL) {
            Point(recyclerViewGlobalBounds.centerX(), recyclerViewGlobalBounds.centerY())
        } else {
            Point(recyclerViewGlobalBounds.centerX(), recyclerViewGlobalBounds.centerY())
        }
        val isFirstCloserThanLast: Boolean = firstItemViewGlobalBounds.contains(recyclerViewCenter)

        return if (isFirstCloserThanLast) firstVisiblePosition else lastVisiblePosition
    }
}
