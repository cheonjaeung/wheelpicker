package com.cheonjaeung.powerwheelpicker.android

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Enforces [WheelPicker] child view's [RecyclerView.LayoutParams] to [RecyclerView.LayoutParams.MATCH_PARENT].
 */
internal class ChildLayoutParamsEnforcer : RecyclerView.OnChildAttachStateChangeListener {
    override fun onChildViewAttachedToWindow(view: View) {
        val params = view.layoutParams as RecyclerView.LayoutParams
        if (params.width != RecyclerView.LayoutParams.MATCH_PARENT) {
            throw IllegalStateException("WheelPicker's ViewHolder must have match_parent width")
        }
        if (params.height != RecyclerView.LayoutParams.MATCH_PARENT) {
            throw IllegalStateException("WheelPicker's ViewHolder must have match_parent height")
        }
    }

    override fun onChildViewDetachedFromWindow(view: View) {}
}
