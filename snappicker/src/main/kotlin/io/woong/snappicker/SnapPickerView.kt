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
 * [SnapPickerView] displays multiple items as a linear list, vertical or horizontal, with
 * snapping fling behavior.
 */
public class SnapPickerView<T> : FrameLayout {

    private val recyclerView: RecyclerView

    /**
     * Adapter to manage child view and data.
     */
    @Suppress("UNCHECKED_CAST")
    public var adapter: SnapPickerAdapter<T, *>
        get() = recyclerView.adapter as SnapPickerAdapter<T, *>
        set(value) {
            value.orientation = this.orientation
            recyclerView.adapter = value
        }

    /**
     * Orientation of this picker. Either [VERTICAL][RecyclerView.VERTICAL] or
     * [HORIZONTAL][RecyclerView.HORIZONTAL].
     */
    @RecyclerView.Orientation
    private var orientation: Int
        get() = (recyclerView.layoutManager as LinearLayoutManager).orientation
        set(value) {
            adapter.orientation = value
            (recyclerView.layoutManager as LinearLayoutManager).orientation = value
        }

    /**
     * Whether this picker has infinity length or not.
     */
    public var isCyclic: Boolean
        get() = adapter.isCyclic
        set(value) {
            adapter.isCyclic = value
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
        val a = context.obtainStyledAttributes(attrs, R.styleable.SnapPickerView, defStyleAttr, defStyleRes)
        val orientation = a.getInt(R.styleable.SnapPickerView_android_orientation, RecyclerView.VERTICAL)
        val isCyclic = a.getBoolean(R.styleable.SnapPickerView_isCyclic, true)
        a.recycle()

        recyclerView = RecyclerView(context)
        val pickerLayoutManager = LinearLayoutManager(context, orientation, false)
        recyclerView.layoutManager = pickerLayoutManager
        val pickerAdapter = DefaultSnapPickerAdapter<Any>()
        pickerAdapter.orientation = orientation
        pickerAdapter.isCyclic = isCyclic
        recyclerView.adapter = pickerAdapter
        LinearSnapHelper().attachToRecyclerView(recyclerView)
        addView(recyclerView)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val innerPadding: Int
        if (orientation == RecyclerView.VERTICAL) {
            innerPadding = (measuredHeight / 2) - (adapter.getItemSize(context) / 2)
            recyclerView.setPadding(0, innerPadding, 0, innerPadding)
        } else {
            innerPadding = (measuredWidth / 2) - (adapter.getItemSize(context) / 2)
            recyclerView.setPadding(innerPadding, 0, innerPadding, 0)
        }
        if (recyclerView.clipToPadding) {
            recyclerView.clipToPadding = false
        }
    }
}
