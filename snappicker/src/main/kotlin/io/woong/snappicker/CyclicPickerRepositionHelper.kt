package io.woong.snappicker

import androidx.recyclerview.widget.RecyclerView

/**
 * A helper class to reposition current selected item.
 *
 * A picker which enabled cyclic option has large size of items (Int.MAX_VALUE) for infinite
 * scrolling. To make user feel infinite items, current adapter position should be on around center
 * of adapter (around half of Int.MAX_VALUE). It calculates correct index to reposition when scroll
 * is idle, and move to the index.
 */
internal object CyclicPickerRepositionHelper {
    fun moveToCenterPosition(pickerView: ValuePickerView, currentPosition: Int) {
        if (currentPosition == RecyclerView.NO_POSITION) return
        if (!pickerView.isCyclic) return

        val pickerAdapter = pickerView.adapter
        if (pickerAdapter != null) {
            val adapterItemCount = pickerAdapter.itemCount
            val valueCount = pickerAdapter.getValueCount()
            val chunkCount = adapterItemCount / valueCount
            val currentValueIndex = currentPosition % valueCount
            val centerChunkIndex = valueCount * (chunkCount / 2)
            val targetIndex = centerChunkIndex + currentValueIndex
            pickerView.scrollToPosition(targetIndex)
        }
    }
}
