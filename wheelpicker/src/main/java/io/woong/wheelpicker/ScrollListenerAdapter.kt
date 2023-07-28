/*
 * Copyright 2023 Jaewoong Cheon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
