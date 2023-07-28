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

    /**
     * Picker view reference to get some picker's options.
     */
    internal var pickerView: ValuePickerView? = null

    /**
     * Value list to display into this picker.
     */
    public var values: List<T> = emptyList()

    /**
     * Returns a value at specified position. Note that it finds right value when this picker
     * is cyclic for avoiding [IndexOutOfBoundsException].
     */
    public fun getValue(position: Int): T = values[position % values.size]

    /**
     * Returns the total number of items in this adapter. Note that this count is affected by
     * cyclic option. If the picker is cyclic, it will returns [Int.MAX_VALUE].
     */
    public final override fun getItemCount(): Int {
        val picker = this.pickerView ?: return 0
        return if (picker.isCyclic) {
            Int.MAX_VALUE
        } else {
            values.size
        }
    }

    public final override fun getItemViewType(position: Int): Int = super.getItemViewType(position)

    public final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemContainerViewHolder<V> {
        // onCreateViewHolder() will be called when adapter is attached to view.
        val picker = this.pickerView ?: throw IllegalStateException("Impossible")
        return ItemContainerViewHolder.create(
            parent = parent,
            itemHeight = picker.itemHeight,
            itemView = createItemView(parent)
        )
    }

    /**
     * Creates a new item view associated with specified position.
     */
    public abstract fun createItemView(parent: ViewGroup): V

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
