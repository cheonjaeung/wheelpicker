package io.woong.wheelpicker.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * A state object to handle [ValuePicker]. In most cases, it can be used with
 * [rememberValuePickerState].
 *
 * @param initialIndex The initial selected value of the picker.
 * @param onValueSelect Optional callback that invoked when selected value changed.
 */
@Stable
public class ValuePickerState<T>(
    internal val initialIndex: Int,
    internal val onValueSelect: (value: T) -> Unit,
) {
    /**
     * The current selected index of this picker.
     */
    public var currentIndex: Int by mutableStateOf(initialIndex)
        internal set

    /**
     * Whether this picker is currently scrolling.
     */
    public var isScrollInProgress: Boolean by mutableStateOf(false)
        internal set

    public companion object {
        /**
         * The default [Saver] implementation for [ValuePickerState].
         */
        public fun <T : Any> Saver(onValueSelect: (value: T) -> Unit): Saver<ValuePickerState<T>, Int> {
            return Saver(
                save = {
                    it.currentIndex
                },
                restore = {
                    ValuePickerState(it, onValueSelect)
                },
            )
        }
    }
}

/**
 * Creates and remembers a [ValuePickerState] to handle [ValuePicker].
 *
 * @param initialIndex The initial selected index of the picker.
 * @param onValueSelect Optional callback that invoked when selected value changed.
 */
@Stable
@Composable
public fun <T : Any> rememberValuePickerState(
    initialIndex: Int = 0,
    onValueSelect: (value: T) -> Unit = {},
): ValuePickerState<T> {
    return rememberSaveable(
        saver = ValuePickerState.Saver(onValueSelect = onValueSelect)
    ) {
        ValuePickerState(
            initialIndex = initialIndex,
            onValueSelect = onValueSelect,
        )
    }
}

/**
 * Creates and remembers a [ValuePickerState] to handle [ValuePicker].
 *
 * @param initialValue The initial value of the picker.
 * @param onValueSelect Optional callback that invoked when selected value changed.
 */
@Deprecated("Use rememberValuePickerState with initialIndex instead.")
@Stable
@Composable
public fun <T : Any> rememberValuePickerState(
    initialValue: T,
    onValueSelect: (value: T) -> Unit = {},
): ValuePickerState<T> {
    return rememberSaveable(
        saver = ValuePickerState.Saver(onValueSelect = onValueSelect)
    ) {
        ValuePickerState(
            initialIndex = 0,
            onValueSelect = onValueSelect,
        )
    }
}
