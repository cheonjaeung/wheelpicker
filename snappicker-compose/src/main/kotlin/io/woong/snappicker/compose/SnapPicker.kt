package io.woong.snappicker.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.LazyListSnapperLayoutInfo
import dev.chrisbanes.snapper.rememberLazyListSnapperLayoutInfo
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlin.math.abs

/**
 * The horizontal scrollable picker that allows user to select one item from multiple items.
 *
 * @param state The state object to manage this picker's state.
 * @param modifier The modifier to apply to this composable.
 * @param itemWidth The width size of each item composable's container.
 * @param repeated Whether this picker has repeating list.
 * When `true`, user can scroll continuously over the end of list.
 * The first item will displayed before first and the last will displayed after first.
 * @param itemContent The content composable of the single item.
 */
@ExperimentalSnapPickerApi
@Composable
public fun <T> HorizontalSnapPicker(
    state: SnapPickerState<T>,
    modifier: Modifier = Modifier,
    itemWidth: Dp = 48.dp,
    repeated: Boolean = false,
    itemContent: @Composable BoxScope.(value: T) -> Unit
) {
    SnapPicker(
        state = state,
        isVertical = false,
        itemSize = DpSize(width = itemWidth, height = 0.dp),
        repeated = repeated,
        modifier = modifier,
        itemContent = itemContent
    )
}

/**
 * The vertical scrollable picker that allows user to select one item from multiple items.
 *
 * @param state The state object to manage this picker's state.
 * @param modifier The modifier to apply to this composable.
 * @param itemHeight The height size of each item composable's container.
 * @param repeated Whether this picker has repeating list.
 * When `true`, user can scroll continuously over the end of list.
 * The first item will displayed before first and the last will displayed after first.
 * @param itemContent The content composable of the single item.
 */
@ExperimentalSnapPickerApi
@Composable
public fun <T> VerticalSnapPicker(
    state: SnapPickerState<T>,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 48.dp,
    repeated: Boolean = false,
    itemContent: @Composable BoxScope.(value: T) -> Unit
) {
    SnapPicker(
        state = state,
        isVertical = true,
        itemSize = DpSize(width = 0.dp, height = itemHeight),
        repeated = repeated,
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
    itemSize: DpSize,
    repeated: Boolean,
    modifier: Modifier,
    itemContent: @Composable BoxScope.(value: T) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val snapperLayoutInfo = rememberLazyListSnapperLayoutInfo(lazyListState = lazyListState)
    LaunchedEffect(Unit) {
        lazyListState.animateScrollToItem(
            if (repeated) {
                calculateAroundMidIndex(
                    index = state.currentIndex,
                    valuesCount = state.values.size
                )
            } else {
                state.currentIndex
            }
        )
    }
    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress) {
            val visibleItemsInfo = lazyListState.layoutInfo.visibleItemsInfo
            val centerItemInfo = visibleItemsInfo.find { it.offset in -1..1 }
            if (centerItemInfo != null) {
                val newIndex = centerItemInfo.index % state.values.size
                state.currentIndex = newIndex
                if (repeated) {
                    lazyListState.scrollToAroundMidIndex(
                        index = newIndex,
                        valuesCount = state.values.size
                    )
                }
            }
        }
    }
    BoxWithConstraints(modifier = modifier) {
        if (isVertical) {
            LazyColumn(
                modifier = Modifier.size(width = maxWidth, height = maxHeight),
                state = lazyListState,
                contentPadding = PaddingValues(vertical = (maxHeight / 2) - (itemSize.height / 2)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState = lazyListState)
            ) {
                val itemBoxModifier = Modifier
                    .fillMaxWidth()
                    .height(itemSize.height)
                if (repeated) {
                    items(count = Int.MAX_VALUE) { index ->
                        Box(
                            modifier = itemBoxModifier.pickerAlpha(
                                isVertical = false,
                                index = index,
                                lazyListState = lazyListState,
                                snapperLayoutInfo = snapperLayoutInfo
                            ),
                            content = { itemContent(state.values[index % state.values.size]) }
                        )
                    }
                } else {
                    items(count = state.values.size) { index ->
                        Box(
                            modifier = itemBoxModifier.pickerAlpha(
                                isVertical = false,
                                index = index,
                                lazyListState = lazyListState,
                                snapperLayoutInfo = snapperLayoutInfo
                            ),
                            content = { itemContent(state.values[index]) }
                        )
                    }
                }
            }
        } else {
            LazyRow(
                modifier = Modifier.size(width = maxWidth, height = maxHeight),
                state = lazyListState,
                contentPadding = PaddingValues(horizontal = (maxWidth / 2) - (itemSize.width) / 2),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState = lazyListState)
            ) {
                val itemBoxModifier = Modifier
                    .width(itemSize.width)
                    .fillMaxHeight()
                if (repeated) {
                    items(count = Int.MAX_VALUE) { index ->
                        Box(
                            modifier = itemBoxModifier.pickerAlpha(
                                isVertical = false,
                                index = index,
                                lazyListState = lazyListState,
                                snapperLayoutInfo = snapperLayoutInfo
                            ),
                            content = { itemContent(state.values[index % state.values.size]) }
                        )
                    }
                } else {
                    items(count = state.values.size) { index ->
                        Box(
                            modifier = itemBoxModifier.pickerAlpha(
                                isVertical = false,
                                index = index,
                                lazyListState = lazyListState,
                                snapperLayoutInfo = snapperLayoutInfo
                            ),
                            content = { itemContent(state.values[index]) }
                        )
                    }
                }
            }
        }
    }
}

private fun calculateAroundMidIndex(index: Int, valuesCount: Int): Int {
    return ((Int.MAX_VALUE - (Int.MAX_VALUE % valuesCount)) / 2) + index
}

private suspend fun LazyListState.scrollToAroundMidIndex(index: Int, valuesCount: Int) {
    this.scrollToItem(index = calculateAroundMidIndex(index, valuesCount))
}

@OptIn(ExperimentalSnapperApi::class)
@Stable
private fun Modifier.pickerAlpha(
    isVertical: Boolean,
    index: Int,
    lazyListState: LazyListState,
    snapperLayoutInfo: LazyListSnapperLayoutInfo
): Modifier {
    return this.composed(
        inspectorInfo = {
            debugInspectorInfo {
                name = "pickerAlpha"
                properties["lazyListState"] = lazyListState
                properties["snapperLayoutInfo"] = snapperLayoutInfo
                properties["index"] = index
                properties["isVertical"] = isVertical
            }
        },
        factory = {
            val layoutInfo = remember { lazyListState.layoutInfo }
            val visibleItemCount = layoutInfo.visibleItemsInfo.size
            val viewPortSize: Float
            val singleItemSize: Float
            if (isVertical) {
                viewPortSize = layoutInfo.viewportSize.height.toFloat()
                singleItemSize = viewPortSize / visibleItemCount
            } else {
                viewPortSize = layoutInfo.viewportSize.width.toFloat()
                singleItemSize = viewPortSize / visibleItemCount
            }
            val absoluteDistanceToIndexSnap = abs(snapperLayoutInfo.distanceToIndexSnap(index))
            Modifier.alpha(
                alpha = if (absoluteDistanceToIndexSnap < singleItemSize) {
                    1f - (absoluteDistanceToIndexSnap / singleItemSize) + 0.66f
                } else {
                    (0.66f - (absoluteDistanceToIndexSnap / viewPortSize)).coerceIn(0.1f, 0.66f)
                }
            )
        }
    )
}
