package io.woong.snappicker

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

    private var previousCenterValueIndex: Int = NO_POSITION

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
            val centerPosition = findCenterItemPosition(recyclerView, pickerAdapter)
            val centerValueIndex = centerPosition % pickerAdapter.getValueCount()
            if (centerValueIndex != previousCenterValueIndex) {
                previousCenterValueIndex = centerValueIndex
                onPickerValueSelectedListener?.onValueSelected(pickerView, centerValueIndex)
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
     * Returns an item position at the center of picker view by calculating scroll offset
     * and view sizes.
     */
    private fun findCenterItemPosition(
        recyclerView: RecyclerView,
        pickerAdapter: ValuePickerAdapter<*, *>
    ): Int {
        val scrollOffset = if (pickerView.orientation == ValuePickerView.ORIENTATION_HORIZONTAL) {
            recyclerView.computeHorizontalScrollOffset().toFloat()
        } else {
            recyclerView.computeVerticalScrollOffset().toFloat()
        }
        val itemSize = pickerAdapter.getMaxItemSize(recyclerView.context)
        val centerScrollOffset = scrollOffset + (itemSize / 2)
        return centerScrollOffset.toInt() / itemSize
    }

    companion object {
        private const val NO_POSITION: Int = -1
    }
}
