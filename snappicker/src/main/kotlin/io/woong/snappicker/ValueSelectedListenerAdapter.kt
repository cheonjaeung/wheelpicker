package io.woong.snappicker

import android.graphics.Point
import android.graphics.Rect
import androidx.core.graphics.contains
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A class to adapt [RecyclerView.OnScrollListener] to [ValuePickerView.OnValueSelectedListener].
 */
internal class ValueSelectedListenerAdapter : RecyclerView.OnScrollListener() {
    private var pickerView: ValuePickerView? = null
    private var onValueSelectedListener: ValuePickerView.OnValueSelectedListener? = null

    private var prevSelectedIndex: Int = -1

    internal fun attachToPickerView(pickerView: ValuePickerView) {
        if (this.pickerView == pickerView) {
            return
        }
        if (this.pickerView != null) {
            removeListener()
        }
        this.pickerView = pickerView
        addListener()
    }

    private fun addListener() {
        pickerView?.recyclerView?.addOnScrollListener(this)
    }

    private fun removeListener() {
        pickerView?.recyclerView?.removeOnScrollListener(this)
    }

    internal fun setOnValueSelectedListener(
        onValueSelectedListener: ValuePickerView.OnValueSelectedListener?
    ) {
        this.onValueSelectedListener = onValueSelectedListener
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val picker = this.pickerView
        val pickerAdapter = picker?.adapter
        if (picker != null && pickerAdapter != null) {
            val orientation = picker.orientation
            val centerPosition = findCenterVisibleItemPosition(recyclerView, orientation)
            if (centerPosition != RecyclerView.NO_POSITION) {
                val actualValueCount = pickerAdapter.getValueCount()
                val centerIndex = centerPosition % actualValueCount
                if (centerIndex != prevSelectedIndex) {
                    prevSelectedIndex = centerIndex
                    onValueSelectedListener?.onValueSelected(picker, centerIndex)
                }
            }
        }
    }

    private fun findCenterVisibleItemPosition(recyclerView: RecyclerView, orientation: Int): Int {
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

        val recyclerViewCenter = if (orientation == ValuePickerView.ORIENTATION_HORIZONTAL) {
            Point(recyclerViewGlobalBounds.centerX(), recyclerViewGlobalBounds.centerY())
        } else {
            Point(recyclerViewGlobalBounds.centerX(), recyclerViewGlobalBounds.centerY())
        }
        val isFirstCloserThanLast = firstItemViewGlobalBounds.contains(recyclerViewCenter)

        return if (isFirstCloserThanLast) firstVisiblePosition else lastVisiblePosition
    }
}
