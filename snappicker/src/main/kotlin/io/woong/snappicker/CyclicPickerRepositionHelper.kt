package io.woong.snappicker

import androidx.recyclerview.widget.RecyclerView

internal object CyclicPickerRepositionHelper {
    fun moveToCenterPosition(pickerView: ValuePickerView, currentPosition: Int) {
        if (currentPosition == RecyclerView.NO_POSITION) return
        if (!pickerView.isCyclic) return

        val pickerAdapter = pickerView.adapter
        if (pickerAdapter != null) {
            val totalValueCount = pickerAdapter.getValueCount()
            val currentValueIndex = currentPosition % totalValueCount
            val mod = Int.MAX_VALUE % totalValueCount
            val targetPosition = (totalValueCount * (mod / 2)) + currentValueIndex
            pickerView.scrollToPosition(targetPosition)
        }
    }
}
