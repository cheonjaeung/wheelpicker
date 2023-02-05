package io.woong.snappicker.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * A state object of the [SnapPicker].
 *
 * @param values Possible values of this [SnapPicker].
 * @param initialIndex Initial selected item index.
 */
@ExperimentalSnapPickerApi
public class SnapPickerState<T>(
    internal val values: List<T>,
    initialIndex: Int = 0
) {
    /**
     * The index of the current selected item.
     */
    public var currentIndex: Int by mutableStateOf(initialIndex)
        private set

    public companion object {
        /**
         * The default saver for [SnapPickerState].
         */
        @Suppress("FunctionName", "UNCHECKED_CAST")
        public fun <T> Saver(): Saver<SnapPickerState<T>, List<*>> {
            return Saver(
                save = { listOf(it.values, it.currentIndex) },
                restore = {
                    SnapPickerState(
                        values = it[0] as List<T>,
                        initialIndex = it[1] as Int
                    )
                }
            )
        }
    }
}

/**
 * Creates and remembers a [SnapPickerState].
 *
 * @param values Possible values of this [SnapPicker].
 * @param initialIndex Initial selected item index.
 */
@ExperimentalSnapPickerApi
@Composable
public fun <T> rememberSnapPickerState(
    values: List<T>,
    initialIndex: Int = 0
): SnapPickerState<T> {
    return rememberSaveable(
        inputs = arrayOf(values, initialIndex),
        saver = SnapPickerState.Saver()
    ) {
        SnapPickerState(
            values = values,
            initialIndex = initialIndex
        )
    }
}
