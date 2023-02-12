package io.woong.snappicker.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * A state object of the [SnapTimePicker].
 *
 * @param initialHour The initial selected hour of the picker.
 * @param initialMinute The initial selected minute of the picker.
 */
@ExperimentalSnapPickerApi
public class SnapTimePickerState(
    initialHour: Int,
    initialMinute: Int
) {
    public var currentHour: Int by mutableStateOf(initialHour)
        internal set

    public var currentMinute: Int by mutableStateOf(initialMinute)
        internal set

    public companion object {
        /**
         * The default saver of [SnapTimePickerState].
         */
        public val Saver: Saver<SnapTimePickerState, List<Int>> = Saver(
            save = { listOf(it.currentHour, it.currentMinute) },
            restore = {
                SnapTimePickerState(
                    initialHour = it[0],
                    initialMinute = it[1]
                )
            }
        )
    }
}

/**
 * Creates and remembers a [SnapTimePickerState].
 *
 * @param initialHour The initial selected hour of the picker.
 * @param initialMinute The initial selected minute of the picker.
 */
@ExperimentalSnapPickerApi
@Composable
public fun rememberSnapTimePickerState(
    initialHour: Int,
    initialMinute: Int
): SnapTimePickerState {
    return rememberSaveable(
        inputs = arrayOf(initialHour, initialMinute),
        saver = SnapTimePickerState.Saver
    ) {
        SnapTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute
        )
    }
}
