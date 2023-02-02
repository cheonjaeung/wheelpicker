package io.woong.snappicker.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

/**
 * The vertical scrollable picker component.
 *
 * @param state The state object to manage this picker's state.
 * @param modifier The modifier to apply to this composable.
 * @param contentPadding Padding values around the whole picker.
 * @param itemContent The content composable of the single item.
 */
@OptIn(ExperimentalSnapperApi::class)
@ExperimentalSnapPickerApi
@Composable
public fun <T> SnapPicker(
    state: SnapPickerState<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
    itemContent: @Composable (value: T) -> Unit
) {
    val lazyState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = lazyState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        flingBehavior = rememberSnapperFlingBehavior(lazyListState = lazyState)
    ) {
        items(count = state.values.size) { index ->
            itemContent(state.values[index])
        }
    }
}
