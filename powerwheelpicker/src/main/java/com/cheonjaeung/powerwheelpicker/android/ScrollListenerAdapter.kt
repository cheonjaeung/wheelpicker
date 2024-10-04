package com.cheonjaeung.powerwheelpicker.android

import androidx.recyclerview.widget.RecyclerView

/**
 * A class to adapt [RecyclerView.OnScrollListener] events to the [WheelPicker.OnScrollListener].
 */
internal class ScrollListenerAdapter : RecyclerView.OnScrollListener() {
    private var wheelPicker: WheelPicker? = null

    fun attachToWheelPicker(wheelPicker: WheelPicker) {
        if (this.wheelPicker == wheelPicker) {
            return
        }
        if (this.wheelPicker != null) {
            detachAdapterFromRecyclerView()
        }
        this.wheelPicker = wheelPicker
        attachAdapterToRecyclerView()
    }

    fun detachFromWheelPicker() {
        this.wheelPicker = null
        detachAdapterFromRecyclerView()
    }

    private fun attachAdapterToRecyclerView() {
        wheelPicker?.recyclerView?.addOnScrollListener(this)
    }

    private fun detachAdapterFromRecyclerView() {
        wheelPicker?.recyclerView?.removeOnScrollListener(this)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        wheelPicker?.let { wheelPicker ->
            for (listener in wheelPicker.onScrollListeners) {
                listener.onScrollStateChanged(wheelPicker, newState)
            }
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        wheelPicker?.let { wheelPicker ->
            for (listener in wheelPicker.onScrollListeners) {
                listener.onScrolled(wheelPicker, dx, dy)
            }
        }
    }
}
