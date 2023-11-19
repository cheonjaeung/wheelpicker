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

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * A wheel picker view to allow user to select one value from multiple choices.
 */
public class ValuePickerView : FrameLayout {

    internal val recyclerView: RecyclerView
    private val recyclerViewId: Int

    private val snapHelper: LinearSnapHelper
    private val scrollListenerAdapter: ScrollListenerAdapter
    private val valueSelectedListenerAdapter: ValueSelectedListenerAdapter
    private val cyclicPickerRepositionHelper: CyclicPickerRepositionHelper
    private val alphaEffector: AlphaEffector

    /**
     * Adapter to manage child view and data.
     */
    public var adapter: ValuePickerAdapter<*, *>? = null
        set(value) {
            val currentAdapter = adapter
            if (currentAdapter != null) {
                currentAdapter.pickerView = null
            }
            if (value != null) {
                value.pickerView = this
            }
            recyclerView.adapter = value
            field = value
            requestLayout()
        }

    /**
     * Picker item view's maximum height size in pixel unit.
     */
    public var itemHeight: Int = getDefaultItemSize(resources.displayMetrics)
        set(value) {
            field = value
            requestLayout()
        }

    /**
     * Whether this picker has infinity length or not.
     */
    public var isCyclic: Boolean = DEFAULT_CYCLIC_ENABLED
        set(value) {
            field = value
            requestLayout()
        }

    public constructor(context: Context) : this(context, null)

    public constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    public constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int
    ) : this(context, attrs, defStyleAttr, 0)

    public constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ValuePickerView, defStyleAttr, defStyleRes)
        itemHeight = a.getDimensionPixelSize(
            R.styleable.ValuePickerView_itemHeight,
            getDefaultItemSize(resources.displayMetrics)
        )
        isCyclic = a.getBoolean(R.styleable.ValuePickerView_isCyclic, DEFAULT_CYCLIC_ENABLED)
        val initialIndex = a.getInt(R.styleable.ValuePickerView_initialIndex, 0)
        a.recycle()

        recyclerView = RecyclerView(context)
        recyclerViewId = ViewCompat.generateViewId()
        recyclerView.id = recyclerViewId
        val pickerLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = pickerLayoutManager

        snapHelper = LinearSnapHelper()
        scrollListenerAdapter = ScrollListenerAdapter()
        valueSelectedListenerAdapter = ValueSelectedListenerAdapter()
        cyclicPickerRepositionHelper = CyclicPickerRepositionHelper()
        alphaEffector = AlphaEffector()
        snapHelper.attachToRecyclerView(recyclerView)
        scrollListenerAdapter.attachToPickerView(this)
        valueSelectedListenerAdapter.attachToPickerView(this)
        cyclicPickerRepositionHelper.attachToPickerView(this)
        alphaEffector.attachToPickerView(this)

        addView(recyclerView)
        scrollToIndex(initialIndex)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // Move inner picker recycler view to front.
        if (childCount > 1) {
            val pickerView = getChildAt(0)
            if (pickerView.id == recyclerViewId) {
                removeViewAt(0)
                addView(pickerView)
            }
        }

        // Set padding to internal RecyclerView and set clipToPadding to false for positioning
        // selected item view to center.
        // If clipToPadding is false, the padding are applied but children views are not clipped.
        // So, the first and last item can be placed on the center of RecyclerView, and padding
        // areas can be transparent and scrollable.
        val adapter = this.adapter
        if (adapter != null) {
            val innerPadding = (measuredHeight / 2) - (itemHeight / 2)
            recyclerView.setPadding(0, innerPadding, 0, innerPadding)
            if (recyclerView.clipToPadding) {
                recyclerView.clipToPadding = false
            }
        }
    }

    /**
     * Move this picker's scroll position to the given position without animation.
     *
     * Note: the [position] is related to internal scroll position, not a selected index. To move
     * to a specified selected index, use [scrollToIndex] instead.
     *
     * @param position A destination scroll position.
     */
    public fun scrollToPosition(position: Int) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        layoutManager.scrollToPosition(position)
    }

    /**
     * Move selected index to specified index without animation.
     *
     * @param index A destination selected index.
     */
    public fun scrollToIndex(index: Int) {
        post {
            if (isCyclic) {
                val pos = cyclicPickerRepositionHelper.findApproximatelyCenterPosition(index)
                scrollToPosition(pos)
            } else {
                scrollToPosition(index)
            }
        }
    }

    /**
     * Adds a new listener to receive picker view scrolling event.
     */
    public fun addOnScrollListener(onScrollListener: OnScrollListener) {
        scrollListenerAdapter.addOnScrollListener(onScrollListener)
    }

    /**
     * Removes a listener that was received picker view scrolling event.
     */
    public fun removeOnScrollListener(onScrollListener: OnScrollListener) {
        scrollListenerAdapter.removeOnScrollListener(onScrollListener)
    }

    /**
     * Sets a listener to receive picker view's selected index changing event.
     */
    public fun setOnValueSelectedListener(onValueSelectedListener: OnValueSelectedListener?) {
        valueSelectedListenerAdapter.setOnValueSelectedListener(onValueSelectedListener)
    }

    public companion object {
        /**
         * Scroll state constant means this picker is not currently scrolling.
         */
        public const val SCROLL_STATE_IDLE: Int = RecyclerView.SCROLL_STATE_IDLE

        /**
         * Scroll state constant means this picker is currently dragged.
         */
        public const val SCROLL_STATE_DRAGGING: Int = RecyclerView.SCROLL_STATE_DRAGGING

        /**
         * Scroll state constant means this picker is currently animating to a final
         * position after drag finished.
         */
        public const val SCROLL_STATE_SETTLING: Int = RecyclerView.SCROLL_STATE_SETTLING

        public const val DEFAULT_CYCLIC_ENABLED: Boolean = false

        internal fun getDefaultItemSize(displayMetrics: DisplayMetrics): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                48f,
                displayMetrics
            ).roundToInt()
        }
    }

    /**
     * A listener class that can be added to a picker view to receive scrolling events.
     */
    public open class OnScrollListener {
        /**
         * Callback that invoked when [ValuePickerView]'s scroll state is changed.
         *
         * @param pickerView The picker view which scrolled.
         * @param newState New updated scroll state. one of [ValuePickerView.SCROLL_STATE_IDLE],
         * [ValuePickerView.SCROLL_STATE_DRAGGING] or [ValuePickerView.SCROLL_STATE_SETTLING].
         */
        public open fun onScrollStateChanged(pickerView: ValuePickerView, newState: Int) {}

        /**
         * Callback that invoked when [ValuePickerView] has been scrolled. It will be called
         * after the scroll is finished.
         *
         * @param pickerView The picker view which scrolled.
         * @param dx Amount of horizontal scroll.
         * @param dy Amount of vertical scroll.
         */
        public open fun onScrolled(pickerView: ValuePickerView, dx: Int, dy: Int) {}
    }

    /**
     * A listener that can be added to a picker view to receive value selecting event.
     */
    public fun interface OnValueSelectedListener {
        /**
         * Callback that invoked when picker's selected value is changed.
         *
         * @param pickerView The picker view that that selected value has been changed.
         * @param index The selected index.
         */
        public fun onValueSelected(pickerView: ValuePickerView, index: Int)
    }
}
