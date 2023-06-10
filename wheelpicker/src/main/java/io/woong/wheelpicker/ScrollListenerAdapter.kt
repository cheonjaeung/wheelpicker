package io.woong.wheelpicker

import androidx.recyclerview.widget.RecyclerView

/**
 * A class to adapt [RecyclerView.OnScrollListener] events to [ValuePickerView.OnScrollListener].
 */
internal class ScrollListenerAdapter : RecyclerView.OnScrollListener() {
    private var pickerView: ValuePickerView? = null
    private val onScrollListeners: MutableList<ValuePickerView.OnScrollListener> = mutableListOf()

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

    internal fun addOnScrollListener(onScrollListener: ValuePickerView.OnScrollListener) {
        this.onScrollListeners.add(onScrollListener)
    }

    internal fun removeOnScrollListener(onScrollListener: ValuePickerView.OnScrollListener) {
        this.onScrollListeners.remove(onScrollListener)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        val picker = this.pickerView
        if (picker != null) {
            for (listener in onScrollListeners) {
                listener.onScrollStateChanged(picker, newState)
            }
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val picker = this.pickerView
        if (picker != null) {
            for (listener in onScrollListeners) {
                listener.onScrolled(picker, dx, dy)
            }
        }
    }
}
