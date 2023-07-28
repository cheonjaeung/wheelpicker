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

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.ceil

internal class AlphaEffector : RecyclerView.OnScrollListener() {
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

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val picker = this.pickerView
        if (picker != null) {
            val targetItemCount = computeActuallyVisibleItemCount(recyclerView)
            val targetViews = findActuallyVisibleItemViews(recyclerView, targetItemCount) ?: return
            val itemViewSize = computeItemViewSize(recyclerView)
            for (view in targetViews) {
                val distanceToCenter = computeDistanceToCenter(view, recyclerView) ?: break
                val absoluteDistanceToCenter = abs(distanceToCenter)
                view.alpha = if (absoluteDistanceToCenter < itemViewSize) {
                    1f - (absoluteDistanceToCenter / itemViewSize) + 0.25f
                } else {
                    0.25f
                }
            }
        }
    }

    /**
     * In the [ValuePickerView], [RecyclerView]'s clipToPadding is always false. For that
     * reason, RecyclerView's computed visible area is smaller that actually visible area.
     *
     * Alpha effect should applied to actually visible area, because users can see actually
     * visible area, not computed visible area.
     *
     * This method calculates actually visible item count to get how much items should be
     * applied alpha effect.
     */
    private fun computeActuallyVisibleItemCount(recyclerView: RecyclerView): Int {
        val singleItemViewSize = recyclerView.computeVerticalScrollExtent().toFloat()
        val totalSize = recyclerView.measuredHeight.toFloat()
        return ceil(totalSize / singleItemViewSize).toInt()
    }

    private fun findActuallyVisibleItemViews(recyclerView: RecyclerView, itemCount: Int): List<View>? {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

        val centerFirstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val centerFirstVisibleView = layoutManager.findViewByPosition(centerFirstVisiblePosition)
        val centerLastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        val centerLastVisibleView = layoutManager.findViewByPosition(centerLastVisiblePosition)
        if (centerFirstVisibleView == null || centerLastVisibleView == null) {
            return null
        }

        val centerFirstVisibleViewBounds = getViewBounds(centerFirstVisibleView)
        val recyclerViewBounds = getViewBounds(recyclerView)
        if (centerFirstVisibleViewBounds == null || recyclerViewBounds == null) {
            return null
        }

        val centerPosition: Int
        val centerView: View
        val centerFirstViewBottom = centerFirstVisibleViewBounds.bottom
        val centerY = recyclerViewBounds.centerY()
        if (centerY < centerFirstViewBottom) {
            centerPosition = centerFirstVisiblePosition
            centerView = centerFirstVisibleView
        } else {
            centerPosition = centerLastVisiblePosition
            centerView = centerLastVisibleView
        }

        val halfItemCount = (itemCount - 1) / 2
        val rangeFirst = centerPosition - halfItemCount
        val rangeLast = centerPosition + halfItemCount
        val positionRange = rangeFirst..rangeLast
        val views = mutableListOf<View>()
        for (viewPosition in positionRange) {
            if (viewPosition == centerPosition) {
                views.add(centerView)
                continue
            }
            val view = layoutManager.findViewByPosition(viewPosition) ?: return null
            views.add(view)
        }
        return views
    }

    private fun computeItemViewSize(recyclerView: RecyclerView): Float {
        return recyclerView.computeVerticalScrollExtent().toFloat()
    }

    private fun computeDistanceToCenter(view: View, recyclerView: RecyclerView): Float? {
        val viewBounds = getViewBounds(view)
        val recyclerViewBounds = getViewBounds(recyclerView)
        if (viewBounds == null || recyclerViewBounds == null) {
            return null
        }

        val viewCenter = viewBounds.exactCenterY()
        val recyclerViewCenter = recyclerViewBounds.exactCenterY()
        return recyclerViewCenter - viewCenter
    }

    private fun getViewBounds(view: View): Rect? {
        val bounds = Rect()
        val succeed = view.getGlobalVisibleRect(bounds)
        return if (succeed) {
            bounds
        } else {
            null
        }
    }
}
