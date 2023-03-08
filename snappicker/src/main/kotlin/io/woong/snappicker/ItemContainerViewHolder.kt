package io.woong.snappicker

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
            @RecyclerView.Orientation orientation: Int,
            crossAxisSize: Int,
            itemView: View
        ): ItemContainerViewHolder<V> {
            val containerView = FrameLayout(parent.context)
            val itemWidth = if (orientation == RecyclerView.VERTICAL) {
                FrameLayout.LayoutParams.MATCH_PARENT
            } else {
                crossAxisSize
            }
            val itemHeight = if (orientation == RecyclerView.VERTICAL) {
                crossAxisSize
            } else {
                FrameLayout.LayoutParams.MATCH_PARENT
            }
            containerView.layoutParams = FrameLayout.LayoutParams(itemWidth, itemHeight)
            itemView.id = ViewCompat.generateViewId()
            containerView.addView(itemView)
            return ItemContainerViewHolder(containerView, itemView.id)
        }
    }
}
