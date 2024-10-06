package com.cheonjaeung.powerwheelpicker.android

import androidx.recyclerview.widget.RecyclerView

/**
 * A class to adapt [RecyclerView.OnScrollListener] events to the [WheelPicker.OnItemSelectedListener].
 */
internal class ItemSelectedListenerAdapter : RecyclerView.OnScrollListener() {
    private var wheelPicker: WheelPicker? = null
    private var lastSelectedPosition: Int = WheelPicker.NO_POSITION

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

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val centerPosition = wheelPicker?.findCenterVisibleItemPosition()
        if (centerPosition == null || centerPosition == WheelPicker.NO_POSITION) {
            return
        }

        wheelPicker?.let { wheelPicker ->
            if (centerPosition != lastSelectedPosition) {
                lastSelectedPosition = centerPosition
                for (listener in wheelPicker.onItemSelectedListeners) {
                    listener.onItemSelected(wheelPicker, centerPosition)
                }
            }
        }
    }
}
