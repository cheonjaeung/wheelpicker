package io.woong.wheelpicker

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

public class ItemContainerViewHolder<V : View> private constructor(
    containerView: View,
    private val itemViewId: Int
): RecyclerView.ViewHolder(containerView) {
    internal fun getItemView(): V = itemView.findViewById(itemViewId)

    public companion object {
        internal fun <V : View> create(
            parent: ViewGroup,
            itemView: View,
            itemHeight: Int,
        ): ItemContainerViewHolder<V> {
            val containerView = FrameLayout(parent.context)
            containerView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                itemHeight
            )
            itemView.id = ViewCompat.generateViewId()
            containerView.addView(itemView)
            return ItemContainerViewHolder(containerView, itemView.id)
        }
    }
}
