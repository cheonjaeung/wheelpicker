package io.woong.snappicker.compose

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import io.woong.snappicker.ValuePickerAdapter

/**
 * An internal adapter to use [ValuePickerAdapter] with Jetpack Compose.
 */
internal class ComposeValuePickerAdapter<T>(
    private val itemContent: @Composable (BoxScope.(value: T) -> Unit)
) : ValuePickerAdapter<T, View>() {
    override fun createItemView(context: Context): View {
        val composeItemView = ComposeView(context)
        composeItemView.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }
        return composeItemView
    }

    override fun bindItemView(itemView: View, position: Int) {
        val value = getValue(position)
        itemView as ComposeView
        itemView.setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                content = { itemContent(value) }
            )
        }
    }
}
