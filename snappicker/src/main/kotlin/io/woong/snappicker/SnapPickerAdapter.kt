package io.woong.snappicker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * An adapter to handle picker values and item views associated with data.
 *
 * @param T Type of the item value.
 * @param V Type of the item view.
 */
public abstract class SnapPickerAdapter<T, V : View> :
    RecyclerView.Adapter<ItemContainerViewHolder<V>>() {

    private var values: List<T> = emptyList()

    @RecyclerView.Orientation
    internal var orientation: Int = RecyclerView.VERTICAL

    internal var isCyclic: Boolean = true

    public fun setValues(values: List<T>) {
        this.values = values
    }

    public fun getValue(position: Int): T = values[position % values.size]

    public final override fun getItemCount(): Int = if (isCyclic) Int.MAX_VALUE else values.size

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
