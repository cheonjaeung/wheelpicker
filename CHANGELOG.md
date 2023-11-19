# WheelPicker Changelog

## 0.2.0

_Published at 2023_11_19_

### Breaking Changes

- `ValuePickerState.currentValue` is now removed. Please use `ValuePickerState.currentIndex` instead.

### Added

- `ValuePickerView` now supports scroll to a specified selected index using `ValuePickerView.scrollToIndex()`.
- `rememberValuePickerState` with `initialIndex` is added.

### Deprecation

- `rememberValuePickerState` with `initialValue` is deprecated. Please migrate to `rememberValuePickerState` with `initialIndex`.

### Bug Fix

- Fix initial index is not work when using `rememberValuePickerState`. (#13)

## 0.1.1

_Published at 2023-07-29_

### Bug Fix

- Fix `ValuePicker` composable's unexpected recomposition occurred when scroll is finished.

## 0.1.0

_Published at 2023-06-21_

Release the first version.
