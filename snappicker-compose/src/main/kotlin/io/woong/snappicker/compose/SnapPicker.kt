package io.woong.snappicker.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

/**
 * The vertical scrollable picker component.
 *
 * @param state The state object to manage this picker's state.
 * @param modifier The modifier to apply to this composable.
 * @param itemContent The content composable of the single item.
 */
@OptIn(ExperimentalSnapperApi::class)
@ExperimentalSnapPickerApi
@Composable
public fun <T> SnapPicker(
    state: SnapPickerState<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (value: T) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val lazyState = rememberLazyListState(initialFirstVisibleItemIndex = state.currentIndex)
        var itemHeightPx by remember { mutableStateOf(0) }
        val density = LocalDensity.current
        val verticalPadding by remember {
            derivedStateOf {
                val fullHeightPx = lazyState.layoutInfo.viewportSize.height
                val marginPx = (fullHeightPx / 2) - (itemHeightPx / 2)
                return@derivedStateOf with(density) { marginPx.toDp() }
            }
        }
        LazyColumn(
            modifier = Modifier.size(width = maxWidth, height = maxHeight),
            state = lazyState,
            contentPadding = PaddingValues(vertical = verticalPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState = lazyState)
        ) {
            items(count = state.values.size) { index ->
                Box(modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                    itemHeightPx = layoutCoordinates.size.height
                }) {
                    itemContent(state.values[index])
                }
            }
        }
    }
}
