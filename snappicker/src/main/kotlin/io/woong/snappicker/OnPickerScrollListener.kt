package io.woong.snappicker

/**
 * A listener to receive [ValuePickerView]'s scroll event.
 */
public open class OnPickerScrollListener {
    /**
     * Callback that invoked when [ValuePickerView]'s scroll state is changed.
     *
     * @param pickerView The picker view which scrolled.
     * @param newState New updated scroll state, one of [ValuePickerView.SCROLL_STATE_IDLE],
     * [ValuePickerView.SCROLL_STATE_DRAGGING] or [ValuePickerView.SCROLL_STATE_SETTLING].
     */
    public open fun onScrollStateChanged(pickerView: ValuePickerView, newState: Int) {}

    /**
     * Callback that invoked when [ValuePickerView] has been scrolled. It will be called
     * after the scroll is finished.
     *
     * @param pickerView The picker view which scrolled.
     * @param dx Horizontal scroll delta.
     * @param dy Vertical scroll delta.
     */
    public open fun onScrolled(pickerView: ValuePickerView, dx: Int, dy: Int) {}
}
