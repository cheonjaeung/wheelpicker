package io.woong.wheelpicker.compose

import android.view.ViewGroup
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.woong.wheelpicker.ValuePickerView

/**
 * A wheel picker composable to allow user to select one value from multiple choices.
 *
 * @param values The list of values to be displayed into this picker.
 * @param modifier The modifier instance to be applied to this picker's outer layout.
 * @param state The state to control this picker.
 * @param contentPadding The padding around the picker.
 * @param itemHeight The size of item content.
 * @param isCyclic Whether this picker should displays values repeatedly.
 * @param decorationBox The decoration composable around the picker, such as indicator.
 * This composable lambda has one parameter called `innerPicker`. You must call `innerPicker`
 * exactly once.
 * @param itemContent The picker's items content composable.
 */
@Composable
public fun <T : Any> ValuePicker(
    values: List<T>,
    modifier: Modifier = Modifier,
    state: ValuePickerState<T> = rememberValuePickerState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    itemHeight: Dp = 48.dp,
    isCyclic: Boolean = false,
    decorationBox: @Composable BoxScope.(innerPicker: @Composable () -> Unit) -> Unit =
        @Composable { innerPicker -> innerPicker() },
    itemContent: @Composable BoxScope.(value: T) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val itemHeightPx = with(LocalDensity.current) { itemHeight.roundToPx() }

        val valueSelectedListener = remember {
            ValuePickerView.OnValueSelectedListener { _, index ->
                state.currentIndex = index
            }
        }

        val scrollStateListener = remember {
            object : ValuePickerView.OnScrollListener() {
                override fun onScrollStateChanged(pickerView: ValuePickerView, newState: Int) {
                    state.isScrollInProgress = newState != ValuePickerView.SCROLL_STATE_IDLE
                }
            }
        }

        decorationBox {
            AndroidView(
                modifier = Modifier
                    .padding(contentPadding)
                    .matchParentSize(),
                factory = { context ->
                    val androidValuePickerView = ValuePickerView(context)
                    val pickerAdapter = ComposeValuePickerAdapter(itemContent)
                    pickerAdapter.values = values
                    androidValuePickerView.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    androidValuePickerView.adapter = pickerAdapter
                    androidValuePickerView.itemHeight = itemHeightPx
                    androidValuePickerView.isCyclic = isCyclic
                    androidValuePickerView.setOnValueSelectedListener(valueSelectedListener)
                    androidValuePickerView.scrollToIndex(state.initialIndex)
                    androidValuePickerView.addOnScrollListener(scrollStateListener)
                    androidValuePickerView
                },
            )
        }
    }
}
