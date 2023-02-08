package io.woong.snappicker.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
 * The horizontal scrollable picker that allows user to select one item from multiple items.
 *
 * @param state The state object to manage this picker's state.
 * @param modifier The modifier to apply to this composable.
 * @param itemContent The content composable of the single item.
 */
@ExperimentalSnapPickerApi
@Composable
public fun <T> HorizontalSnapPicker(
    state: SnapPickerState<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (value: T) -> Unit
) {
    SnapPicker(
        state = state,
        isVertical = false,
        modifier = modifier,
        itemContent = itemContent
    )
}

/**
 * The vertical scrollable picker that allows user to select one item from multiple items.
 *
 * @param state The state object to manage this picker's state.
 * @param modifier The modifier to apply to this composable.
 * @param itemContent The content composable of the single item.
 */
@ExperimentalSnapPickerApi
@Composable
public fun <T> VerticalSnapPicker(
    state: SnapPickerState<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (value: T) -> Unit
) {
    SnapPicker(
        state = state,
        isVertical = true,
        modifier = modifier,
        itemContent = itemContent
    )
}

@OptIn(ExperimentalSnapperApi::class)
@ExperimentalSnapPickerApi
@Composable
private fun <T> SnapPicker(
    state: SnapPickerState<T>,
    isVertical: Boolean,
    modifier: Modifier = Modifier,
    itemContent: @Composable (value: T) -> Unit
) {
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = state.currentIndex)
    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress) {
            val visibleItemsInfo = lazyListState.layoutInfo.visibleItemsInfo
            val centerItemInfo = visibleItemsInfo.find { it.offset == 0 }
            if (centerItemInfo != null) {
                state.currentIndex = centerItemInfo.index
            }
        }
    }
    BoxWithConstraints(modifier = modifier) {
        if (isVertical) {
            val density = LocalDensity.current
            var itemHeightPx by remember { mutableStateOf(0) }
            val verticalPadding by remember {
                derivedStateOf {
                    val fullHeightPx = lazyListState.layoutInfo.viewportSize.height
                    val marginPx = (fullHeightPx / 2) - (itemHeightPx / 2)
                    return@derivedStateOf with(density) { marginPx.toDp() }
                }
            }
            LazyColumn(
                modifier = Modifier.size(width = maxWidth, height = maxHeight),
                state = lazyListState,
                contentPadding = PaddingValues(vertical = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState = lazyListState)
            ) {
                items(count = state.values.size) { index ->
                    Box(modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        itemHeightPx = layoutCoordinates.size.height
                    }) {
                        itemContent(state.values[index])
                    }
                }
            }
        } else {
            val density = LocalDensity.current
            var itemWidthPx by remember { mutableStateOf(0) }
            val horizontalPadding by remember {
                derivedStateOf {
                    val fullWidthPx = lazyListState.layoutInfo.viewportSize.width
                    val marginPx = (fullWidthPx / 2) - (itemWidthPx / 2)
                    return@derivedStateOf with(density) { marginPx.toDp() }
                }
            }
            LazyRow(
                modifier = Modifier.size(width = maxWidth, height = maxHeight),
                state = lazyListState,
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState = lazyListState)
            ) {
                items(count = state.values.size) { index ->
                    Box(modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        itemWidthPx = layoutCoordinates.size.width
                    }) {
                        itemContent(state.values[index])
                    }
                }
            }
        }
    }
}
