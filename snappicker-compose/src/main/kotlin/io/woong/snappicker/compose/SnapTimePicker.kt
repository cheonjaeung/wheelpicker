package io.woong.snappicker.compose

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The vertical scrollable time picker that allows user to select one hour/minute.
 *
 * @param state The state object of this time picker.
 * @param modifier The modifier to apply to this composable.
 * @param itemHeight The height size of each item composable's container.
 * @param itemContent The content composable of the single item.
 */
@ExperimentalSnapPickerApi
@Composable
public fun SnapTimePicker(
    state: SnapTimePickerState,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 48.dp,
    itemContent: @Composable BoxScope.(value: Int) -> Unit
) {
    val hourState = rememberSnapPickerState(
        values = (0..23).map { it },
        initialIndex = state.currentHour
    )
    val minuteState = rememberSnapPickerState(
        values = (0..59).map { it },
        initialIndex = state.currentMinute
    )
    LaunchedEffect(hourState.currentIndex) {
        state.currentHour = hourState.currentIndex
    }
    LaunchedEffect(minuteState.currentIndex) {
        state.currentMinute = minuteState.currentIndex
    }
    Row(modifier = modifier) {
        VerticalSnapPicker(
            state = hourState,
            modifier = Modifier.weight(0.5f).fillMaxHeight(),
            itemHeight = itemHeight,
            itemContent = itemContent
        )
        VerticalSnapPicker(
            state = minuteState,
            modifier = Modifier.weight(0.5f).fillMaxHeight(),
            itemHeight = itemHeight,
            itemContent = itemContent
        )
    }
}
