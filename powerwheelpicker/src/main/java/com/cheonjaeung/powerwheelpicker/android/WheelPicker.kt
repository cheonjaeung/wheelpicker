package com.cheonjaeung.powerwheelpicker.android

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.ClassLoaderCreator
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.cheonjaeung.simplecarousel.android.CarouselSnapHelper
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * [WheelPicker] is a view that allow user to select one item from multiple choices.
 *
 * [WheelPicker] is backed by [RecyclerView]. Set an [RecyclerView.Adapter] to handle dataset. Note that
 * the [RecyclerView.ViewHolder]s must have `match_parent` for their `layout_width` and `layout_height`.
 * The item width and height can be set to [itemWidth] and [itemHeight].
 *
 * [WheelPicker] has a selector area used to calculate the selected item position. When an item is positioned
 * within the center of the selector area, the [OnItemSelectedListener.onItemSelected] is called.
 * The size of selector area is determined by [selectorWidth] and [selectorHeight].
 *
 * [WheelPicker] supports customized item visual effect using animation properties via [ItemEffector].
 * [ItemEffector] provides multiple callbacks to supports various effect implementations.
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
    private val layoutBounds: Rect = Rect()

    /**
     * Reusable instance for the selector bounds.
     */
    private val selectorBounds: Rect = Rect()

    /**
     * A [RecyclerView.Adapter] to provide picker items on demand.
     */
    var adapter: RecyclerView.Adapter<*>?
        get() = recyclerView.adapter
        set(value) {
            recyclerView.adapter = value
            if (pendingCurrentPosition != NO_POSITION) {
                recyclerView.scrollToPosition(pendingCurrentPosition)
                pendingCurrentPosition = NO_POSITION
            } else {
                recyclerView.scrollToPosition(0)
            }
        }

    /**
     * Current orientation of this picker, Either [HORIZONTAL] or [VERTICAL].
     */
    @Orientation
    var orientation: Int
        @Orientation
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
     * A pixel width of the picker item. The size must not be a negative.
     * It will be ignored when orientation is vertical.
     */
    var itemWidth: Int
        get() = layoutManager.itemWidth
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("itemWidth must not be a negative: itemWidth=$value")
            }
            layoutManager.itemWidth = value
        }

    /**
     * A pixel height of the picker item. The size must not be a negative.
     * It will be ignored when orientation is horizontal.
     */
    var itemHeight: Int
        get() = layoutManager.itemHeight
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("itemHeight must not be a negative: itemHeight=$value")
            }
            layoutManager.itemHeight = value
        }

    /**
     * The [Drawable] for the background content of the selector area.
     */
    var selectorBackground: Drawable? = null
        private set

    /**
     * The tint to apply to the [selectorBackground] drawable.
     */
    var selectorBackgroundTintList: ColorStateList? = null
        set(value) {
            if (value != field) {
                field = value
                invalidate()
            }
        }

    /**
     * The blending mode to apply to the [selectorBackgroundTintList]. The default is [PorterDuff.Mode.SRC_IN].
     */
    var selectorBackgroundTintMode: PorterDuff.Mode? = null
        set(value) {
            if (value != field) {
                field = value
                invalidate()
            }
        }

    /**
     * The [Drawable] for the foreground content of the selector area.
     */
    var selectorForeground: Drawable? = null
        private set

    /**
     * The tint to apply to the [selectorForeground] drawable.
     */
    var selectorForegroundTintList: ColorStateList? = null
        set(value) {
            if (value != field) {
                field = value
                invalidate()
            }
        }

    /**
     * The blending mode to apply to the [selectorForegroundTintList]. The default is [PorterDuff.Mode.SRC_IN].
     */
    var selectorForegroundTintMode: PorterDuff.Mode? = null
        set(value) {
            if (value != field) {
                field = value
                invalidate()
            }
        }

    /**
     * A pixel width of the selector area in the picker. The size must be a positive.
     */
    var selectorWidth: Int = 0
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("selecorWidth must not be a negative: selectorWidth=$value")
            }
            if (value != field) {
                field = value
                invalidate()
            }
        }

    /**
     * A pixel height of the selector area in the picker. The size must be a positive.
     */
    var selectorHeight: Int = 0
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("selectorHeight must not be a negative: selectorHeight=$value")
            }
            if (value != field) {
                field = value
                invalidate()
            }
        }

    /**
     * The adapter position of the currently selected item. [NO_POSITION] if there is no selected item.
     */
    var currentPosition: Int
        get() = findCenterVisibleItemPosition()
        set(value) {
            setCurrentPosition(value, false)
        }

    internal val onScrollListeners: MutableList<OnScrollListener> = mutableListOf()
    internal val onItemSelectedListeners: MutableList<OnItemSelectedListener> = mutableListOf()
    internal val itemEffectors: MutableList<ItemEffector> = mutableListOf()

    private var scrollListenerAdapter: ScrollListenerAdapter? = null
    private var itemSelectedListenerAdapter: ItemSelectedListenerAdapter? = null
    private var itemEffectorAdapter: ItemEffectorAdapter? = null

    /**
     * A temporal value for restoring [currentPosition].
     */
    private var pendingCurrentPosition: Int = NO_POSITION

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
        val itemWidth = a.getDimensionPixelSize(R.styleable.WheelPicker_itemWidth, 0)
        val itemHeight = a.getDimensionPixelSize(R.styleable.WheelPicker_itemHeight, 0)
        selectorWidth = a.getDimensionPixelSize(
            R.styleable.WheelPicker_selector_width,
            a.getDimensionPixelSize(R.styleable.WheelPicker_selectorWidth, 0)
        )
        selectorHeight = a.getDimensionPixelSize(
            R.styleable.WheelPicker_selector_height,
            a.getDimensionPixelSize(R.styleable.WheelPicker_selectorHeight, 0)
        )
        setSelectorBackgroundDrawable(a.getDrawable(R.styleable.WheelPicker_selector_background))
        setSelectorForegroundDrawable(a.getDrawable(R.styleable.WheelPicker_selector_foreground))
        selectorBackgroundTintList = a.getColorStateList(R.styleable.WheelPicker_selector_backgroundTint)
        selectorForegroundTintList = a.getColorStateList(R.styleable.WheelPicker_selector_foregroundTint)
        selectorBackgroundTintMode = parseIntToTintMode(
            a.getInt(R.styleable.WheelPicker_selector_backgroundTintMode, -1)
        )
        selectorForegroundTintMode = parseIntToTintMode(
            a.getInt(R.styleable.WheelPicker_selector_foregroundTintMode, -1)
        )
        a.recycle()

        setWillNotDraw(false)
        recyclerView = RecyclerView(context)
        recyclerView.id = View.generateViewId()
        layoutManager = PickerLayoutManager(orientation, circular, itemWidth, itemHeight)
        recyclerView.layoutManager = layoutManager
        recyclerView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        recyclerView.clipToPadding = false
        recyclerView.addOnChildAttachStateChangeListener(ChildLayoutParamsEnforcer())
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

    private fun parseIntToTintMode(value: Int): PorterDuff.Mode? {
        return when (value) {
            0 -> PorterDuff.Mode.CLEAR
            1 -> PorterDuff.Mode.SRC
            2 -> PorterDuff.Mode.DST
            3 -> PorterDuff.Mode.SRC_OVER
            4 -> PorterDuff.Mode.DST_OVER
            5 -> PorterDuff.Mode.SRC_IN
            6 -> PorterDuff.Mode.DST_IN
            7 -> PorterDuff.Mode.SRC_OUT
            8 -> PorterDuff.Mode.DST_OUT
            9 -> PorterDuff.Mode.SRC_ATOP
            10 -> PorterDuff.Mode.DST_ATOP
            11 -> PorterDuff.Mode.XOR
            12 -> PorterDuff.Mode.ADD
            13 -> PorterDuff.Mode.MULTIPLY
            14 -> PorterDuff.Mode.SCREEN
            15 -> PorterDuff.Mode.OVERLAY
            16 -> PorterDuff.Mode.DARKEN
            17 -> PorterDuff.Mode.LIGHTEN
            else -> null
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val viewState = super.onSaveInstanceState() ?: return null
        val savedState = SavedState(viewState)
        savedState.recyclerViewId = recyclerView.id
        savedState.currentPosition = if (pendingCurrentPosition != NO_POSITION) {
            currentPosition
        } else {
            pendingCurrentPosition
        }
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null || state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        pendingCurrentPosition = state.currentPosition
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        val savedState = container?.get(id)
        if (container == null || savedState !is SavedState) {
            super.dispatchRestoreInstanceState(container)
            return
        }
        val previousRecyclerViewId = savedState.recyclerViewId
        val currentRecyclerViewId = recyclerView.id
        val parcelable = container.get(previousRecyclerViewId)
        container.put(currentRecyclerViewId, parcelable)
        container.remove(previousRecyclerViewId)
        super.dispatchRestoreInstanceState(container)
        restorePendingState()
    }

    /**
     * Restores [WheelPicker]'s state with pending values.
     */
    private fun restorePendingState() {
        if (pendingCurrentPosition == NO_POSITION) {
            return
        }
        val adapter = this.adapter ?: return
        val position = max(0, min(pendingCurrentPosition, adapter.itemCount - 1))
        pendingCurrentPosition = NO_POSITION
        recyclerView.scrollToPosition(position)
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
        layoutBounds.set(
            paddingLeft,
            paddingTop,
            right - left - paddingRight,
            bottom - top - paddingBottom
        )

        when (orientation) {
            HORIZONTAL -> {
                val innerPadding = (layoutBounds.width() / 2) - (itemWidth / 2)
                recyclerView.setPadding(innerPadding, 0, innerPadding, 0)
                if (itemWidth == 0) {
                    Log.w(TAG, "itemWidth should be set bigger than 0")
                }
            }

            VERTICAL -> {
                val innerPadding = (layoutBounds.height() / 2) - (itemHeight / 2)
                recyclerView.setPadding(0, innerPadding, 0, innerPadding)
                if (itemHeight == 0) {
                    Log.w(TAG, "itemHeight should be set bigger than 0")
                }
            }
        }

        recyclerView.layout(layoutBounds.left, layoutBounds.top, layoutBounds.right, layoutBounds.bottom)
    }

    override fun onDraw(canvas: Canvas) {
        when (orientation) {
            HORIZONTAL -> {
                if (selectorWidth == 0) {
                    Log.d(TAG, "Skipping selector drawing: selectorWidth=0")
                }
                selectorBounds.set(
                    (width / 2) - (selectorWidth / 2),
                    layoutBounds.top,
                    (width / 2) + (selectorWidth / 2),
                    layoutBounds.bottom
                )
            }

            VERTICAL -> {
                if (selectorHeight == 0) {
                    Log.d(TAG, "Skipping selector drawing: selectorHeight=0")
                }
                selectorBounds.set(
                    layoutBounds.left,
                    (height / 2) - (selectorHeight / 2),
                    layoutBounds.right,
                    (height / 2) + (selectorHeight / 2)
                )
            }
        }

        selectorBackground?.let { background ->
            background.bounds = selectorBounds
            selectorBackgroundTintList?.let { tint ->
                background.setTintList(tint)
                selectorBackgroundTintMode?.let { background.setTintMode(it) }
            }
            background.draw(canvas)
        }

        super.onDraw(canvas)

        selectorForeground?.let { foreground ->
            foreground.bounds = selectorBounds
            selectorForegroundTintList?.let { tint ->
                foreground.setTintList(tint)
                selectorForegroundTintMode?.let { foreground.setTintMode(it) }
            }
            foreground.draw(canvas)
        }
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
     * Moves current position to an adapter position.
     *
     * @param position The position to select.
     * @param smoothScroll if `true`, enable scrolling animation, move immediately otherwise.
     */
    fun setCurrentPosition(position: Int, smoothScroll: Boolean) {
        val adapter = this.adapter
        if (adapter == null) {
            pendingCurrentPosition = position
            return
        }
        if (adapter.itemCount == 0) {
            return
        }
        if (position == currentPosition) {
            return
        }

        if (smoothScroll) {
            recyclerView.smoothScrollToPosition(position)
        } else {
            recyclerView.scrollToPosition(position)
        }
    }

    /**
     * Sets a [Drawable] for the background content of the selector area.
     */
    fun setSelectorBackgroundDrawable(drawable: Drawable?) {
        selectorBackground = drawable
        invalidate()
    }

    /**
     * Sets a [Drawable] from specific resource for the background content of the selector area.
     */
    fun setSelectorBackgroundDrawableResource(@DrawableRes resId: Int) {
        if (resId == 0) {
            return
        }
        val drawable = AppCompatResources.getDrawable(context, resId)
        setSelectorBackgroundDrawable(drawable)
    }

    /**
     * Sets a color for the background of the selector area.
     */
    fun setSelectorBackgroundColor(@ColorInt color: Int) {
        setSelectorBackgroundDrawable(ColorDrawable(color))
    }

    /**
     * Sets a color from specific resource for the background of the selector area.
     */
    fun setSelectorBackgroundColorResource(@ColorRes resId: Int) {
        if (resId == 0) {
            return
        }
        val color = ContextCompat.getColor(context, resId)
        setSelectorBackgroundColor(color)
    }

    /**
     * Sets a [Drawable] for the foreground content of the selector area.
     */
    fun setSelectorForegroundDrawable(drawable: Drawable?) {
        selectorForeground = drawable
        invalidate()
    }

    /**
     * Sets a [Drawable] from specific resource for the foreground content of the selector area.
     */
    fun setSelectorForegroundDrawableResource(@DrawableRes resId: Int) {
        if (resId == 0) {
            return
        }
        val drawable = AppCompatResources.getDrawable(context, resId)
        setSelectorForegroundDrawable(drawable)
    }

    /**
     * Sets a color for the foreground of the selector area.
     */
    fun setSelectorForegroundColor(@ColorInt color: Int) {
        setSelectorForegroundDrawable(ColorDrawable(color))
    }

    /**
     * Sets a color from specific resource for the foreground of the selector area.
     */
    fun setSelectorForegroundColorResource(@ColorRes resId: Int) {
        if (resId == 0) {
            return
        }
        val color = ContextCompat.getColor(context, resId)
        setSelectorForegroundColor(color)
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
    internal fun findCenterVisibleItemPosition(): Int {
        val center = findCenterVisibleView()
        if (center != null) {
            return layoutManager.getPosition(center)
        }
        return NO_POSITION
    }

    /**
     * Find a view at the center.
     */
    internal fun findCenterVisibleView(): View? {
        val centerOffset = when (orientation) {
            HORIZONTAL -> {
                val start = layoutManager.paddingLeft
                val end = layoutManager.width - layoutManager.paddingRight
                val width = end - start
                start + width / 2f
            }

            VERTICAL -> {
                val top = layoutManager.paddingTop
                val bottom = layoutManager.height - layoutManager.paddingBottom
                val height = bottom - top
                top + height / 2f
            }

            else -> throw IllegalStateException("Invalid orientation: $orientation")
        }

        for (i in 0 until layoutManager.childCount) {
            val child = layoutManager.getChildAt(i) ?: continue
            val params = child.layoutParams as RecyclerView.LayoutParams
            val childStart: Int
            val childEnd: Int
            when (orientation) {
                HORIZONTAL -> {
                    childStart = layoutManager.getDecoratedLeft(child) - params.leftMargin
                    childEnd = layoutManager.getDecoratedRight(child) + params.rightMargin
                }

                VERTICAL -> {
                    childStart = layoutManager.getDecoratedTop(child) - params.topMargin
                    childEnd = layoutManager.getDecoratedBottom(child) + params.bottomMargin
                }

                else -> throw IllegalStateException("Invalid orientation: $orientation")
            }
            if (centerOffset >= childStart && centerOffset <= childEnd) {
                return child
            }
        }

        return null
    }

    /**
     * Calculates the center position of the given child view within the [WheelPicker] view.
     */
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

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(HORIZONTAL, VERTICAL)
    annotation class Orientation

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING)
    annotation class ScrollState

    private class SavedState : BaseSavedState {
        var recyclerViewId: Int
        var currentPosition: Int

        constructor(parcel: Parcel) : super(parcel) {
            recyclerViewId = parcel.readInt()
            currentPosition = parcel.readInt()
        }

        constructor(viewState: Parcelable) : super(viewState) {
            recyclerViewId = 0
            currentPosition = NO_POSITION
        }

        @RequiresApi(24)
        constructor(parcel: Parcel, classLoader: ClassLoader) : super(parcel, classLoader) {
            recyclerViewId = parcel.readInt()
            currentPosition = parcel.readInt()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeInt(recyclerViewId)
            parcel.writeInt(currentPosition)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : ClassLoaderCreator<SavedState> {
            override fun createFromParcel(parcel: Parcel, classLoader: ClassLoader): SavedState {
                return if (Build.VERSION.SDK_INT >= 24) {
                    SavedState(parcel, classLoader)
                } else {
                    SavedState(parcel)
                }
            }

            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
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
        open fun onScrollStateChanged(wheelPicker: WheelPicker, @ScrollState newState: Int) {}

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
         * Callback that invoked when an item is positioned at the center of selector area.
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
        open fun applyEffectOnScrollStateChanged(
            view: View,
            @ScrollState newState: Int,
            positionOffset: Int,
            centerOffset: Int
        ) {}

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

        /**
         * Apply a visual effect to the item view of the [WheelPicker]. This callback will be called after
         * an item is selected.
         *
         * @param view The item view to apply visual effect.
         * @param position The selected item adapter position.
         */
        open fun applyEffectOnItemSelected(view: View, position: Int) {}
    }
}
