package io.woong.wheelpicker

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * An adapter to handle data set of a [ValuePickerView] and item views associated with specified
 * data.
 *
 * To display data into item view, override [createItemView] and [bindItemView]. Create item view
 * using [createItemView] and set on [bindItemView].
 */
public abstract class ValuePickerAdapter<T, V : View> :
    RecyclerView.Adapter<ItemContainerViewHolder<V>>() {

    internal var pickerView: ValuePickerView? = null

    /**
     * Value list to display into this picker.
     */
    public var values: List<T> = emptyList()

    private val isCyclic: Boolean
        get() = pickerView?.isCyclic ?: ValuePickerView.DEFAULT_CYCLIC_ENABLED

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
            itemHeight = pickerView?.itemHeight
                ?: ValuePickerView.getDefaultItemSize(parent.resources.displayMetrics),
            itemView = createItemView(parent.context)
        )
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
