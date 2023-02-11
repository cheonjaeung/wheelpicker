package io.woong.snappicker.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * A state object of the [SnapDatePicker].
 *
 * @param initialYear The initial selected year of the picker.
 * @param initialMonth The initial selected month of the picker.
 * @param initialDate The initial selected date of the picker.
 */
@ExperimentalSnapPickerApi
public class SnapDatePickerState(
    initialYear: Int,
    initialMonth: Int,
    initialDate: Int
) {
    public var currentYear: Int by mutableStateOf(initialYear)
        internal set

    public var currentMonth: Int by mutableStateOf(initialMonth)
        internal set

    public var currentDate: Int by mutableStateOf(initialDate)
        internal set

    public companion object {
        /**
         * The default saver of [SnapDatePickerState].
         */
        public val Saver: Saver<SnapDatePickerState, List<Int>> = Saver(
            save = { listOf(it.currentYear, it.currentMonth, it.currentDate) },
            restore = {
                SnapDatePickerState(
                    initialYear = it[0],
                    initialMonth = it[1],
                    initialDate = it[2]
                )
            }
        )
    }
}

/**
 * Creates and remembers a [SnapDatePickerState].
 *
 * @param initialYear The initial selected year of the picker.
 * @param initialMonth The initial selected month of the picker.
 * @param initialDate The initial selected date of the picker.
 */
@ExperimentalSnapPickerApi
@Composable
public fun rememberSnapDatePickerState(
    initialYear: Int,
    initialMonth: Int,
    initialDate: Int
): SnapDatePickerState {
    return rememberSaveable(
        inputs = arrayOf(initialYear, initialMonth, initialDate),
        saver = SnapDatePickerState.Saver
    ) {
        SnapDatePickerState(
            initialYear = initialYear,
            initialMonth = initialMonth,
            initialDate = initialDate
        )
    }
}
