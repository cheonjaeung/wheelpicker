package io.woong.snappicker

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
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
 * This picker works with several items:
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

    private val recyclerView: RecyclerView

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
     * Orientation of this picker. Either [VERTICAL][RecyclerView.VERTICAL] or
     * [HORIZONTAL][RecyclerView.HORIZONTAL].
     */
    @RecyclerView.Orientation
    public var orientation: Int
        get() = (recyclerView.layoutManager as LinearLayoutManager).orientation
        set(value) {
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

    private var onScrollListener: OnScrollListener? = null
    private var onValueSelectedListener: OnValueSelectedListener? = null

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
        val orientation = a.getInt(R.styleable.ValuePickerView_android_orientation, RecyclerView.VERTICAL)
        isCyclic = a.getBoolean(R.styleable.ValuePickerView_isCyclic, true)
        a.recycle()

        recyclerView = RecyclerView(context)
        val pickerLayoutManager = LinearLayoutManager(context, orientation, false)
        recyclerView.layoutManager = pickerLayoutManager
        LinearSnapHelper().attachToRecyclerView(recyclerView)
        attachOnScrollListenerToRecyclerView(recyclerView)
        attachOnValueSelectedListenerToRecyclerView(recyclerView)
        addView(recyclerView)
    }

    private fun attachOnScrollListenerToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                onScrollListener?.onScrollStateChanged(this@ValuePickerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                onScrollListener?.onScrolled(this@ValuePickerView, dx, dy)
            }
        })
    }

    private fun attachOnValueSelectedListenerToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            /**
             * Previous selected value index container for avoiding unnecessary calls.
             */
            private var prevIndex = RecyclerView.NO_POSITION

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val adapter = this@ValuePickerView.adapter
                if (adapter != null) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    // Find visible item position methods return central position.
                    // Because, position is calculated considering padding.
                    val position = layoutManager.findFirstVisibleItemPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        val index = position % adapter.getValueCount()
                        if (index != prevIndex) {
                            prevIndex = index
                            onValueSelectedListener?.onValueSelected(this@ValuePickerView, index)
                        }
                    }
                }
            }
        })
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val adapter = this.adapter
        if (adapter != null) {
            val innerPadding: Int
            if (orientation == RecyclerView.VERTICAL) {
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
     * Sets a [OnScrollListener] to receive scroll event.
     */
    public fun setOnScrollListener(onScrollListener: OnScrollListener?) {
        this.onScrollListener = onScrollListener
    }

    /**
     * Sets a [OnValueSelectedListener] to receive value selected event.
     */
    public fun setOnValueSelectedListener(onValueSelectedListener: OnValueSelectedListener?) {
        this.onValueSelectedListener = onValueSelectedListener
    }

    public companion object {
        public const val DEFAULT_ORIENTATION: Int = RecyclerView.VERTICAL
        public const val DEFAULT_CYCLIC_ENABLED: Boolean = false

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
         * position while not under outside control.
         */
        public const val SCROLL_STATE_SETTLING: Int = RecyclerView.SCROLL_STATE_SETTLING
    }

    /**
     * A listener to receive [ValuePickerView]'s scroll event.
     */
    public open class OnScrollListener {
        /**
         * Callback that invoked when [ValuePickerView]'s scroll state is changed.
         *
         * @param pickerView The picker view which scrolled.
         * @param newState New updated scroll state, one of [SCROLL_STATE_IDLE], [SCROLL_STATE_DRAGGING]
         * or [SCROLL_STATE_SETTLING].
         */
        public open fun onScrollStateChanged(pickerView: ValuePickerView, newState: Int) {}

        /**
         * Callback that invoked when [ValuePickerView] has been scrolled. It will be called
         * after the scroll is finished.
         *
         * @param pickerView The picker view which scrolled.
         * @param dx Horizontal scroll delta.
         * @param dy Vertical scroll delta.
         */
        public open fun onScrolled(pickerView: ValuePickerView, dx: Int, dy: Int) {}
    }

    /**
     * A listener to receive picker item selected event.
     */
    public fun interface OnValueSelectedListener {
        /**
         * Callback that invoked when selected value is changed.
         *
         * @param pickerView The picker view that selected value has been changed.
         * @param index The selected item's index.
         */
        public fun onValueSelected(pickerView: ValuePickerView, index: Int)
    }
}
