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
 * The vertical scrollable date picker that allows user to select one year/month/date.
 *
 * @param state The state object of this date picker.
 * @param modifier The modifier to apply to this composable.
 * @param itemHeight The height size of each item composable's container.
 * @param itemContent The content composable of the single item.
 */
@ExperimentalSnapPickerApi
@Composable
public fun SnapDatePicker(
    state: SnapDatePickerState,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 48.dp,
    itemContent: @Composable BoxScope.(value: Int) -> Unit
) {
    val yearState = rememberSnapPickerState(
        values = (0..9999).map { it },
        initialIndex = state.currentYear
    )
    val monthState = rememberSnapPickerState(
        values = (1..12).map { it },
        initialIndex = state.currentMonth - 1
    )
    val dateState = rememberSnapPickerState(
        values = (1..calculateLastDate(state.currentYear, state.currentMonth)).map { it },
        initialIndex = state.currentDate - 1
    )
    LaunchedEffect(yearState.index) {
        state.currentYear = yearState.index
    }
    LaunchedEffect(monthState.index) {
        state.currentMonth = monthState.index + 1
        val lastDate = calculateLastDate(state.currentYear, state.currentMonth)
        if (state.currentDate > lastDate) {
//            dateState.index = lastDate
            state.currentDate = lastDate
        }
    }
    LaunchedEffect(dateState.index) {
        state.currentDate = dateState.index + 1
    }
    Row(modifier = modifier) {
        VerticalSnapPicker(
            state = yearState,
            modifier = Modifier.weight(1f / 3f).fillMaxHeight(),
            itemHeight = itemHeight,
            itemContent = itemContent
        )
        VerticalSnapPicker(
            state = monthState,
            modifier = Modifier.weight(1f / 3f).fillMaxHeight(),
            itemHeight = itemHeight,
            itemContent = itemContent
        )
        VerticalSnapPicker(
            state = dateState,
            modifier = Modifier.weight(1f / 3f).fillMaxHeight(),
            itemHeight = itemHeight,
            itemContent = itemContent
        )
    }
}

private fun calculateLastDate(year: Int, month: Int): Int {
    return when (month) {
        1 -> 31
        2 -> if (isLeafYear(year)) 29 else 28
        3 -> 31
        4 -> 30
        5 -> 31
        6 -> 30
        7 -> 31
        8 -> 31
        9 -> 30
        10 -> 31
        11 -> 30
        12 -> 31
        else -> throw IllegalStateException("Unreachable $year, $month")
    }
}

private fun isLeafYear(year: Int): Boolean {
    return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)
}
