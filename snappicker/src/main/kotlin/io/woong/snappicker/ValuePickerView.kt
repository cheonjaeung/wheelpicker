package io.woong.snappicker

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * A scrollable picker to allow user to select one value from multiple items.
 *
 * [ValuePickerView] displays multiple items as a linear list, vertical or horizontal, with
 * snap fling behavior. The picker scroll position will stop at specified item, not ambiguous
 * position.
 *
 * This picker works with several components:
 * - [ValuePickerView]: A view class to be placed into view hierarchy.
 * - [ValuePickerAdapter]: An adapter class to handle data set and item view associated with
 *   specified data.
 * - Listeners: Listener classes or interfaces to receive specified events from this picker.
 *
 * To display values into picker view, at least, places a view to layout and adds adapter
 * to the picker view. And set values to the adapter.
 *
 * @see ValuePickerAdapter
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
                currentAdapter.pickerViewRef = null
            }
            if (value != null) {
                value.pickerViewRef = this
            }
            recyclerView.adapter = value
            field = value
            requestLayout()
        }

    /**
     * Orientation of this picker. Either [ORIENTATION_VERTICAL] or [ORIENTATION_HORIZONTAL].
     */
    @RecyclerView.Orientation
    public var orientation: Int
        get() = (recyclerView.layoutManager as LinearLayoutManager).orientation
        set(value) {
            if (value != ORIENTATION_HORIZONTAL && value != ORIENTATION_VERTICAL) {
                throw IllegalArgumentException("Orientation value must be one of 0 or 1")
            }
            (recyclerView.layoutManager as LinearLayoutManager).orientation = value
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
        val orientation = a.getInt(R.styleable.ValuePickerView_android_orientation, ORIENTATION_VERTICAL)
        isCyclic = a.getBoolean(R.styleable.ValuePickerView_isCyclic, DEFAULT_CYCLIC_ENABLED)
        val initialIndex = a.getInt(R.styleable.ValuePickerView_initialIndex, 0)
        a.recycle()

        recyclerView = RecyclerView(context)
        recyclerViewId = ViewCompat.generateViewId()
        recyclerView.id = recyclerViewId
        val pickerLayoutManager = LinearLayoutManager(context, orientation, false)
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
        postMoveToInitialPosition(initialIndex)
    }

    private fun postMoveToInitialPosition(initialIndex: Int) = post {
        if (isCyclic) {
            val pos = cyclicPickerRepositionHelper.findApproximatelyCenterPosition(initialIndex)
            scrollToPosition(pos)
        } else {
            scrollToPosition(initialIndex)
        }
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
            val innerPadding: Int
            if (orientation == ORIENTATION_VERTICAL) {
                innerPadding = (measuredHeight / 2) - (adapter.getMaxItemSize(context) / 2)
                recyclerView.setPadding(0, innerPadding, 0, innerPadding)
            } else {
                innerPadding = (measuredWidth / 2) - (adapter.getMaxItemSize(context) / 2)
                recyclerView.setPadding(innerPadding, 0, innerPadding, 0)
            }
            if (recyclerView.clipToPadding) {
                recyclerView.clipToPadding = false
            }
        }
    }

    /**
     * Move this picker's scroll position to the given position without animation.
     *
     * @param position The destination index.
     */
    public fun scrollToPosition(position: Int) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        layoutManager.scrollToPosition(position)
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
        public const val ORIENTATION_HORIZONTAL: Int = RecyclerView.HORIZONTAL
        public const val ORIENTATION_VERTICAL: Int = RecyclerView.VERTICAL

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

        public const val DEFAULT_ORIENTATION: Int = ORIENTATION_VERTICAL
        public const val DEFAULT_CYCLIC_ENABLED: Boolean = false
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
