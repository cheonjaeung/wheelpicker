package io.woong.snappicker.compose

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * A state object of the picker.
 *
 * @param values Possible values of this picker.
 * @param initialIndex Initial selected item index.
 */
@ExperimentalSnapPickerApi
@Stable
public class SnapPickerState<T>(
    internal val values: List<T>,
    internal val initialIndex: Int = 0
) : ScrollableState {

    /**
     * The state for controlling internal [LazyRow][androidx.compose.foundation.lazy.LazyRow]
     * and [LazyColumn][androidx.compose.foundation.lazy.LazyColumn].
     */
    internal val lazyListState = LazyListState()

    /**
     * The holder to store previous selected item index.
     */
    private var prevIndex: Int = initialIndex

    /**
     * The current selected item index.
     */
    public val index: Int
        get() {
            val visibleItemsInfo = lazyListState.layoutInfo.visibleItemsInfo
            val centerItemInfo = visibleItemsInfo.find { it.offset in -1..1 }
            return if (centerItemInfo != null) {
                val newIndex = centerItemInfo.index % values.size
                prevIndex = newIndex
                return newIndex
            } else {
                prevIndex
            }
        }

    /**
     * The current selected item value.
     */
    public val value: T
        get() = values[index]

    public override val isScrollInProgress: Boolean
        get() = lazyListState.isScrollInProgress

    public override fun dispatchRawDelta(delta: Float): Float =
        lazyListState.dispatchRawDelta(delta)

    public override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ): Unit = lazyListState.scroll(scrollPriority, block)

    /**
     * Move to the given item without animation.
     * It means the scroll position will move to target index instantly.
     *
     * @param index The destination item index. It must be non-negative.
     */
    public suspend fun scrollToItem(index: Int): Unit =
        lazyListState.scrollToItem(index, scrollOffset = 0)

    /**
     * Scroll to the given item with animation (smooth scrolling).
     *
     * @param index The destination item index. It must be non-negative.
     */
    public suspend fun animateScrollToItem(index: Int): Unit =
        lazyListState.animateScrollToItem(index, scrollOffset = 0)

    public companion object {
        /**
         * The default saver for [SnapPickerState].
         */
        @Suppress("UNCHECKED_CAST")
        public fun <T> Saver(): Saver<SnapPickerState<T>, List<*>> {
            return Saver(
                save = { listOf(it.values, it.index) },
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
 * @param values Possible values of this picker.
 * @param initialIndex Initial selected item index.
 */
@ExperimentalSnapPickerApi
@Composable
@Stable
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
