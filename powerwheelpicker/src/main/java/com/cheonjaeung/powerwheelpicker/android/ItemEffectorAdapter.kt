package com.cheonjaeung.powerwheelpicker.android

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * A class to adapt [RecyclerView.OnScrollListener] events to [WheelPicker.ItemEffector].
 */
internal class ItemEffectorAdapter : RecyclerView.OnScrollListener() {
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

    @SuppressLint("WrongConstant")
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        wheelPicker?.let { wheelPicker ->
            val layoutManager = wheelPicker.layoutManager
            val centerPosition = wheelPicker.findCenterVisibleItemPosition()
            if (centerPosition == WheelPicker.NO_POSITION) {
                return
            }
            val centerOffset = calculateWheelPickerCenter()
            for (i in 0 until layoutManager.childCount) {
                val view = layoutManager.getChildAt(i)
                checkNotNull(view) {
                    "LayoutManager returns null child view at the $i position"
                }
                val adapterPosition = layoutManager.getPosition(view)
                val positionDiff = adapterPosition - centerPosition
                val viewCenterOffset = wheelPicker.calculateChildCenter(view)
                val offsetFromCenter = centerOffset - viewCenterOffset
                for (effector in wheelPicker.itemEffectors) {
                    effector.applyEffectOnScrollStateChanged(view, newState, positionDiff, offsetFromCenter)
                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        wheelPicker?.let { wheelPicker ->
            val layoutManager = wheelPicker.layoutManager
            val centerPosition = wheelPicker.findCenterVisibleItemPosition()
            if (centerPosition == WheelPicker.NO_POSITION) {
                return
            }
            val centerOffset = calculateWheelPickerCenter()
            for (i in 0 until layoutManager.childCount) {
                val view = layoutManager.getChildAt(i)
                checkNotNull(view) {
                    "LayoutManager returns null child view at the $i position"
                }
                val delta = if (wheelPicker.orientation == WheelPicker.HORIZONTAL) dx else dy
                val adapterPosition = layoutManager.getPosition(view)
                val positionDiff = adapterPosition - centerPosition
                val viewCenterOffset = wheelPicker.calculateChildCenter(view)
                val offsetFromCenter = centerOffset - viewCenterOffset
                for (effector in wheelPicker.itemEffectors) {
                    effector.applyEffectOnScrolled(view, delta, positionDiff, offsetFromCenter)
                }
            }
            if (centerPosition != lastSelectedPosition) {
                val centerChild = wheelPicker.findCenterVisibleView() ?: return
                lastSelectedPosition = centerPosition
                for (effector in wheelPicker.itemEffectors) {
                    effector.applyEffectOnItemSelected(centerChild, centerPosition)
                }
            }
        }
    }

    /**
     * Calculates center offset of the [wheelPicker]. Returns negative value if failed.
     */
    @SuppressLint("WrongConstant")
    private fun calculateWheelPickerCenter(): Int {
        wheelPicker?.let { wheelPicker ->
            return when (wheelPicker.orientation) {
                WheelPicker.HORIZONTAL -> {
                    val left = wheelPicker.paddingLeft
                    val right = wheelPicker.width - wheelPicker.paddingRight
                    ((right - left) / 2f).roundToInt()
                }

                WheelPicker.VERTICAL -> {
                    val top = wheelPicker.paddingTop
                    val bottom = wheelPicker.height - wheelPicker.paddingBottom
                    ((bottom - top) / 2f).roundToInt()
                }

                else -> throw IllegalStateException("Invalid orientation: ${wheelPicker.orientation}")
            }
        } ?: return -1
    }
}
