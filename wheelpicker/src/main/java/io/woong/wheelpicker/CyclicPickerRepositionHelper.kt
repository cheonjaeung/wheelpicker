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

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A helper class to reposition cyclic picker.
 *
 * A picker which cyclic option is enabled has very large size of items for infinite-like scrolling.
 * To make user feels infinite-like, the picker's position should be placed on center around.
 * (If picker's item size is Int.MAX_VALUE, the position should be approximately half of
 * INT.MAX_VALUE.) This class reposition picker's position to 'approximately half' whenever scroll
 * is finished.
 */
internal class CyclicPickerRepositionHelper : RecyclerView.OnScrollListener() {
    private var pickerView: ValuePickerView? = null

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

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            val picker = this.pickerView
            if (picker != null && picker.isCyclic) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val targetPosition = findApproximatelyCenterPosition(currentPosition)
                    if (targetPosition != RecyclerView.NO_POSITION) {
                        picker.scrollToPosition(targetPosition)
                    }
                }
            }
        }
    }

    @Suppress("FoldInitializerAndIfToElvis")
    internal fun findApproximatelyCenterPosition(currentPosition: Int): Int {
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
        val pickerAdapter = pickerView?.adapter
        if (pickerAdapter == null) {
            return RecyclerView.NO_POSITION
        }
        val adapterItemCount = pickerAdapter.itemCount
        val actualValueCount = pickerAdapter.values.size
        val chunkCount = adapterItemCount / actualValueCount
        val currentValueIndex = currentPosition % actualValueCount
        val approximatelyCenterChunkFirstIndex = actualValueCount * (chunkCount / 2)
        return approximatelyCenterChunkFirstIndex + currentValueIndex
    }
}
