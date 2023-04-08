package io.woong.snappicker

/**
 * A listener to receive picker's selected item changed event.
 */
public fun interface OnPickerValueSelectedListener {
    /**
     * Callback that invoked when selected item is changed.
     *
     * @param pickerView The picker view that selected value has been changed.
     * @param index The selected item's index.
     */
    public fun onValueSelected(pickerView: ValuePickerView, index: Int)
}
