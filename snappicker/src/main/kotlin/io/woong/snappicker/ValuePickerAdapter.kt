package io.woong.snappicker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * An adapter to handle data set of a [ValuePickerView] and item views associated with specified
 * data.
 *
 * To display data into item view, override [createItemView] and [bindItemView]. Create item view
 * using [createItemView] and set on [bindItemView].
 *
 * All item views must have same size. To constraint item view size, it use internal item container
 * frame. Item views should be placed into the container frame. To change max size of item view,
 * override [getMaxItemSize] method.
 */
public abstract class ValuePickerAdapter<T, V : View> :
    RecyclerView.Adapter<ItemContainerViewHolder<V>>() {

    internal var pickerViewRef: ValuePickerView? = null

    /**
     * Value list to display into this picker.
     */
    public var values: List<T> = emptyList()

    @RecyclerView.Orientation
    private val orientation: Int
        get() = pickerViewRef?.orientation ?: ValuePickerView.DEFAULT_ORIENTATION

    private val isCyclic: Boolean
        get() = pickerViewRef?.isCyclic ?: ValuePickerView.DEFAULT_CYCLIC_ENABLED

    /**
     * Returns a value at specified position. Note that it finds right value when this picker
     * is cyclic for avoiding [IndexOutOfBoundsException].
     */
    public fun getValue(position: Int): T = values[position % values.size]

    /**
     * Returns the total number of items in this adapter. Note that this count is affected by
     * cyclic option. If the picker is cyclic, it will returns [Int.MAX_VALUE].
     */
    public final override fun getItemCount(): Int = if (isCyclic) Int.MAX_VALUE else values.size

    /**
     * Returns the total number of values in this picker. Note that this count is real values
     * of the picker.
     */
    public fun getValueCount(): Int = values.size

    public final override fun getItemViewType(position: Int): Int = super.getItemViewType(position)

    public final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemContainerViewHolder<V> {
        return ItemContainerViewHolder.create(
            parent = parent,
            orientation = orientation,
            crossAxisSize = getMaxItemSize(parent.context),
            itemView = createItemView(parent.context)
        )
    }

    /**
     * Returns item's cross axis max size (Width when horizontal, height when vertical).
     * Default is 48DP.
     */
    public open fun getMaxItemSize(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            48f,
            context.resources.displayMetrics
        ).roundToInt()
    }

    /**
     * Creates a new item view associated with specified position.
     */
    public abstract fun createItemView(context: Context): V

    public final override fun onBindViewHolder(
        holder: ItemContainerViewHolder<V>,
        position: Int
    ) {
        if (position != RecyclerView.NO_POSITION) {
            val itemView = holder.getItemView()
            bindItemView(itemView, position)
        }
    }

    /**
     * Binds value to item view that associated with specified position.
     */
    public abstract fun bindItemView(itemView: V, position: Int)
}
