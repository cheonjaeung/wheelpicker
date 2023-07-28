/*
 * Copyright 2023 Jaewoong Cheon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * @param initialValue The initial value of the picker.
 * @param onValueSelect Optional callback that invoked when selected value changed.
 */
@Stable
public class ValuePickerState<T>(
    initialValue: T,
    internal val onValueSelect: (value: T) -> Unit = {},
) {
    /**
     * The current value of this picker.
     */
    public var currentValue: T by mutableStateOf(initialValue)
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
        public fun <T : Any> Saver(onValueSelect: (value: T) -> Unit): Saver<ValuePickerState<T>, T> {
            return Saver(
                save = { it.currentValue },
                restore = { ValuePickerState(it, onValueSelect) },
            )
        }
    }
}

/**
 * Creates and remembers a [ValuePickerState] to handle [ValuePicker].
 *
 * @param initialValue The initial value of the picker.
 * @param onValueSelect Optional callback that invoked when selected value changed.
 */
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
            initialValue = initialValue,
            onValueSelect = onValueSelect,
        )
    }
}
