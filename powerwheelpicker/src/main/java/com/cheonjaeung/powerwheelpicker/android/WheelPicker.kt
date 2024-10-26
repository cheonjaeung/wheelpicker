package com.cheonjaeung.powerwheelpicker.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.cheonjaeung.simplecarousel.android.CarouselSnapHelper
import kotlin.math.roundToInt

/**
 * [WheelPicker] is a view that allow user to select one item from multiple choices.
 *
 * [WheelPicker] needs some components to work correctly.
 *
 * - Adapter: An adapter implements [RecyclerView.Adapter] to handle items.
 * This view is implemented on top of the [RecyclerView]. Set adapter into [adapter] property or `setAdapter`
 * method in Java.
 * - Selector Size: A pixel size of the selector at center of the picker. The selector must be greater than 0.
 * The selector is also used for selection detecting. When an item is positioned within the selector,
 * selected callback will be triggered. Set selector size into [selectorWidth] and [selectorHeight] or
 * set `selector_width` and `selector_height` property in the XML.
 */
class WheelPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * The underlying [RecyclerView].
     */
    internal val recyclerView: RecyclerView

    /**
     * The underlying layout manager for [recyclerView].
     */
    internal val layoutManager: PickerLayoutManager

    /**
     * Reusable instance to layout internal views.
     */
    private val layoutRect: Rect = Rect()

    /**
     * Reusable bounds to center position when finding selected item.
     */
    private val recyclerViewRect: Rect = Rect()

    /**
     * Reusable bounds to check visible item position when finding selected item.
     */
    private val visibleItemRect: Rect = Rect()

    /**
     * A [RecyclerView.Adapter] to provide picker items on demand.
     */
    var adapter: RecyclerView.Adapter<*>?
        get() = recyclerView.adapter
        set(value) {
            recyclerView.adapter = value
        }

    /**
     * Current orientation of this picker, Either [HORIZONTAL] or [VERTICAL].
     */
    @RecyclerView.Orientation
    var orientation: Int
        get() = layoutManager.orientation
        set(value) {
            if (value != HORIZONTAL && value != VERTICAL) {
                throw IllegalArgumentException("Invalid orientation: $value")
            }
            layoutManager.orientation = value
        }

    /**
     * Enable circular mode which means that the first/last item will be connected to the last/first.
     */
    var circular: Boolean
        get() = layoutManager.circular
        set(value) {
            layoutManager.circular = value
        }

    /**
     * A pixel width of the selector area in the picker. The size must be a positive.
     */
    var selectorWidth: Int = 0
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("selectorWidth must be a positive value")
            }
            field = value
        }

    /**
     * A pixel height of the selector area in the picker. The size must be a positive.
     */
    var selectorHeight: Int = 0
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("selectorHeight must be a positive value")
            }
            field = value
        }

    /**
     * The adapter position of the currently selected item. [NO_POSITION] if there is no selected item.
     */
    val currentPosition: Int
        get() = findCenterVisibleItemPosition()

    internal val onScrollListeners: MutableList<OnScrollListener> = mutableListOf()
    internal val onItemSelectedListeners: MutableList<OnItemSelectedListener> = mutableListOf()
    internal val itemEffectors: MutableList<ItemEffector> = mutableListOf()

    private var scrollListenerAdapter: ScrollListenerAdapter? = null
    private var itemSelectedListenerAdapter: ItemSelectedListenerAdapter? = null
    private var itemEffectorAdapter: ItemEffectorAdapter? = null

    /**
     * Returns the number of [RecyclerView.ItemDecoration] currently added to this [WheelPicker].
     */
    val itemDecorationCount: Int
        get() = recyclerView.itemDecorationCount

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.WheelPicker)
        ViewCompat.saveAttributeDataForStyleable(
            this,
            context,
            R.styleable.WheelPicker,
            attrs,
            a,
            defStyleAttr,
            defStyleRes
        )

        val orientation = a.getInt(R.styleable.WheelPicker_android_orientation, DEFAULT_ORIENTATION)
        val circular = a.getBoolean(R.styleable.WheelPicker_circular, DEFAULT_CIRCULAR)
        selectorWidth = a.getDimensionPixelSize(R.styleable.WheelPicker_selector_width, 0)
        selectorHeight = a.getDimensionPixelSize(R.styleable.WheelPicker_selector_height, 0)
        a.recycle()

        recyclerView = RecyclerView(context)
        layoutManager = PickerLayoutManager(orientation, circular)
        recyclerView.layoutManager = layoutManager
        recyclerView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        recyclerView.clipToPadding = false
        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        scrollListenerAdapter = ScrollListenerAdapter()
        scrollListenerAdapter?.attachToWheelPicker(this)
        itemSelectedListenerAdapter = ItemSelectedListenerAdapter()
        itemSelectedListenerAdapter?.attachToWheelPicker(this)
        itemEffectorAdapter = ItemEffectorAdapter()
        itemEffectorAdapter?.attachToWheelPicker(this)

        attachViewToParent(recyclerView, 0, recyclerView.layoutParams)
        recyclerView.scrollToPosition(0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(recyclerView, widthMeasureSpec, heightMeasureSpec)

        val width = recyclerView.measuredWidth
        val height = recyclerView.measuredHeight
        val measuredState = recyclerView.measuredState

        setMeasuredDimension(
            resolveSizeAndState(width, widthMeasureSpec, measuredState),
            resolveSizeAndState(
                height,
                heightMeasureSpec,
                measuredState shl MEASURED_HEIGHT_STATE_SHIFT
            )
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        layoutRect.set(
            paddingLeft,
            paddingTop,
            right - left - paddingRight,
            bottom - top - paddingBottom
        )

        @SuppressLint("WrongConstant")
        when (orientation) {
            HORIZONTAL -> {
                val innerPadding = (layoutRect.width() / 2) - (selectorWidth / 2)
                recyclerView.setPadding(innerPadding, 0, innerPadding, 0)
                if (selectorWidth == 0) {
                    Log.w(TAG, "selectorWidth should be set bigger than 0")
                }
            }

            VERTICAL -> {
                val innerPadding = (layoutRect.height() / 2) - (selectorHeight / 2)
                recyclerView.setPadding(0, innerPadding, 0, innerPadding)
                if (selectorHeight == 0) {
                    Log.w(TAG, "selectorHeight should be set bigger than 0")
                }
            }
        }

        recyclerView.layout(layoutRect.left, layoutRect.top, layoutRect.right, layoutRect.bottom)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scrollListenerAdapter?.detachFromWheelPicker()
        itemSelectedListenerAdapter?.detachFromWheelPicker()
        itemEffectorAdapter?.detachFromWheelPicker()
        scrollListenerAdapter = null
        itemSelectedListenerAdapter = null
        itemEffectorAdapter = null
        onScrollListeners.clear()
        onItemSelectedListeners.clear()
        itemEffectors.clear()
    }

    /**
     * Adds a listener to receive scrolling events.
     */
    fun addOnScrollListener(listener: OnScrollListener) {
        onScrollListeners.add(listener)
    }

    /**
     * Removes a listener that was added to the [WheelPicker].
     */
    fun removeOnScrollListener(listener: OnScrollListener) {
        onScrollListeners.remove(listener)
    }

    /**
     * Removes all listeners that were added to the [WheelPicker].
     */
    fun clearOnScrollListeners() {
        onScrollListeners.clear()
    }

    /**
     * Adds a listener to receive item selected event.
     */
    fun addOnItemSelectedListener(listener: OnItemSelectedListener) {
        onItemSelectedListeners.add(listener)
    }

    /**
     * Removes a listener that was added to the [WheelPicker].
     */
    fun removeOnItemSelectedListener(listener: OnItemSelectedListener) {
        onItemSelectedListeners.remove(listener)
    }

    /**
     * Removes all listeners that were added to the [WheelPicker].
     */
    fun clearOnItemSelectedListeners() {
        onItemSelectedListeners.clear()
    }

    /**
     * Adds a [ItemEffector] to apply visual effect to the [WheelPicker]'s item views.
     */
    fun addItemEffector(effector: ItemEffector) {
        itemEffectors.add(effector)
    }

    /**
     * Removes a [ItemEffector] that was added to the [WheelPicker].
     */
    fun removeItemEffector(effector: ItemEffector) {
        itemEffectors.remove(effector)
    }

    /**
     * Removes all [ItemEffector] that were added to the [WheelPicker].
     */
    fun clearItemEffectors() {
        itemEffectors.clear()
    }

    /**
     * Returns a [RecyclerView.ItemDecoration] at the given position.
     *
     * @param index The position of a decoration.
     * @return The decoration at the given position.
     * @throws IndexOutOfBoundsException On invalid position.
     */
    fun getItemDecorationAt(index: Int) {
        recyclerView.getItemDecorationAt(index)
    }

    /**
     * Adds a [RecyclerView.ItemDecoration] to this [WheelPicker]. Item decorations can affect both
     * measurement and drawing of individual item views.
     */
    fun addItemDecoration(decoration: RecyclerView.ItemDecoration) {
        recyclerView.addItemDecoration(decoration)
    }

    /**
     * Adds a [RecyclerView.ItemDecoration] to this [WheelPicker]. Item decorations can affect both
     * measurement and drawing of individual item views.
     *
     * @param decoration The decoration to add.
     * @param index The position where this decoration will be inserted. If the index is negative,
     * the decoration will be added at the end.
     */
    fun addItemDecoration(decoration: RecyclerView.ItemDecoration, index: Int) {
        recyclerView.addItemDecoration(decoration, index)
    }

    /**
     * Removes a [RecyclerView.ItemDecoration] from this [WheelPicker].
     */
    fun removeItemDecoration(decoration: RecyclerView.ItemDecoration) {
        recyclerView.removeItemDecoration(decoration)
    }

    /**
     * Removes a [RecyclerView.ItemDecoration] at the given position.
     */
    fun removeItemDecorationAt(index: Int) {
        recyclerView.removeItemDecorationAt(index)
    }

    /**
     * Invalidates all [RecyclerView.ItemDecoration] in this [WheelPicker]. It triggers
     * [requestLayout] call.
     */
    fun invalidateItemDecorations() {
        recyclerView.invalidateItemDecorations()
    }

    /**
     * Finds the adapter position of the item at the center.
     */
    @SuppressLint("WrongConstant")
    internal fun findCenterVisibleItemPosition(): Int {
        val firstPosition = layoutManager.findFirstVisibleItemPosition()
        val lastPosition = layoutManager.findLastVisibleItemPosition()
        if (firstPosition == NO_POSITION || lastPosition == NO_POSITION) {
            return NO_POSITION
        }

        if (firstPosition == lastPosition) {
            return firstPosition
        }

        recyclerView.getGlobalVisibleRect(recyclerViewRect)
        val centerX = recyclerViewRect.centerX()
        val centerY = recyclerViewRect.centerY()
        for (i in firstPosition..lastPosition) {
            val item = layoutManager.findViewByPosition(i) ?: continue
            item.getGlobalVisibleRect(visibleItemRect)
            if (visibleItemRect.contains(centerX, centerY)) {
                return i
            }
        }

        return NO_POSITION
    }

    /**
     * Calculates the center position of the given child view within the [WheelPicker] view.
     */
    @SuppressLint("WrongConstant")
    internal fun calculateChildCenter(child: View): Int {
        val params = child.layoutParams as RecyclerView.LayoutParams
        when (orientation) {
            HORIZONTAL -> {
                val left = layoutManager.getDecoratedLeft(child) - params.leftMargin
                val right = layoutManager.getDecoratedRight(child) + params.rightMargin
                val width = right - left
                return (left + width / 2f).roundToInt()
            }

            VERTICAL -> {
                val top = layoutManager.getDecoratedTop(child) - params.topMargin
                val bottom = layoutManager.getDecoratedBottom(child) + params.bottomMargin
                val height = bottom - top
                return (top + height / 2f).roundToInt()
            }

            else -> throw IllegalStateException("Invalid orientation: $orientation")
        }
    }

    companion object {
        private const val TAG: String = "WheelPicker"

        const val HORIZONTAL: Int = RecyclerView.HORIZONTAL
        const val VERTICAL: Int = RecyclerView.VERTICAL

        const val NO_POSITION: Int = RecyclerView.NO_POSITION

        /**
         * Scroll state means that the [WheelPicker] is not currently scrolling.
         */
        const val SCROLL_STATE_IDLE: Int = RecyclerView.SCROLL_STATE_IDLE

        /**
         * Scroll state means that the [WheelPicker] is currently dragging by outside control
         * such as user interaction.
         */
        const val SCROLL_STATE_DRAGGING: Int = RecyclerView.SCROLL_STATE_DRAGGING

        /**
         * Scroll state means that the [WheelPicker] is currently animating to a final position
         * after outside control such as user interaction.
         */
        const val SCROLL_STATE_SETTLING: Int = RecyclerView.SCROLL_STATE_SETTLING

        private const val DEFAULT_ORIENTATION: Int = VERTICAL
        private const val DEFAULT_CIRCULAR: Boolean = true
    }

    /**
     * A listener can be added to [WheelPicker] to receive scrolling events.
     */
    open class OnScrollListener {
        /**
         * Callback that invoked when [WheelPicker]'s scroll state is changed.
         *
         * @param wheelPicker The [WheelPicker] view which scrolled.
         * @param newState The new scroll state. One of [SCROLL_STATE_IDLE], [SCROLL_STATE_DRAGGING]
         * and [SCROLL_STATE_SETTLING].
         */
        open fun onScrollStateChanged(wheelPicker: WheelPicker, newState: Int) {}

        /**
         * Callback that invoked when [WheelPicker] has been scrolled. This callback will be called
         * after the scroll is finished.
         *
         * @param wheelPicker The [WheelPicker] view which scrolled.
         * @param dx The amount of horizontal scroll.
         * @param dy The amount of vertical scroll.
         */
        open fun onScrolled(wheelPicker: WheelPicker, dx: Int, dy: Int) {}
    }

    /**
     * A listener can be added to [WheelPicker] to receive item selected event.
     */
    fun interface OnItemSelectedListener {
        /**
         * Callback that invoked when an item is positioned within the selector area.
         *
         * @param wheelPicker The [WheelPicker] view which scrolled.
         * @param position The selected item index.
         */
        fun onItemSelected(wheelPicker: WheelPicker, position: Int)
    }

    /**
     * A class that allows item views of this [WheelPicker] can have special visual effects like
     * transformation, alpha, rotating using animation properties.
     */
    open class ItemEffector {
        /**
         * Apply a visual effect to the item view of the [WheelPicker]. This callback will be called when
         * scroll state is changed.
         *
         * @param view The item view to apply visual effect.
         * @param newState The new scroll state. One of [SCROLL_STATE_IDLE], [SCROLL_STATE_DRAGGING] and
         * [SCROLL_STATE_SETTLING].
         * @param positionOffset The position difference from selected item. Negative for start, positive
         * for end direction. 0 means the selected item.
         * @param centerOffset The pixel offset how far it is from center of the [WheelPicker].
         */
        open fun applyEffectOnScrollStateChanged(view: View, newState: Int, positionOffset: Int, centerOffset: Int) {}

        /**
         * Apply a visual effect to the item view of the [WheelPicker]. This callback will be called after
         * the scroll is consumed.
         *
         * @param view The item view to apply visual effect.
         * @param delta The amount of scroll.
         * @param positionOffset The position difference from selected item. Negative for start, positive
         * for end direction. 0 means the selected item.
         * @param centerOffset The pixel offset how far it is from center of the [WheelPicker].
         */
        open fun applyEffectOnScrolled(view: View, delta: Int, positionOffset: Int, centerOffset: Int) {}
    }
}
