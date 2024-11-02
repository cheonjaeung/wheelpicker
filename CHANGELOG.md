# WheelPicker Changelog

## 1.0.0

_2024.11.02_

**The whole library is reimplemented and renamed to PowerWheelPicker.**
**Library group and package is change to `com.cheonjaeung.powerwheelpicker.android` from `io.woong.wheelpicker`.**
**Please download this library via `implementation("com.cheonjaeung.powerwheelpicker.android:powerwheelpicker:1.0.0")`.**

### Changes

- `ValuePickerView` is now replaced to `WheelPicker`.
    - `WheelPicker` uses `RecyclerView.Adapter` instead of `ValuePickerAdapter`. And `ValuePickerAdapter` is removed.
    - `WheelPicker` supports both horizontal and vertical orientation.
    - `isCyclic` option is renamed to `circular`.
    - `OnValueSelectedListener` is renamed to `OnItemSelectedListener`.
    - New `ItemEffector` is added to support customized visual effects.
- Artifact for Jetpack Compose is not ready yet. Please use Android View or 0.2.0 version.

## 0.2.0

_2023.11.19_

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

_2023.07.29_

### Bug Fix

- Fix `ValuePicker` composable's unexpected recomposition occurred when scroll is finished.

## 0.1.0

_Published at 2023-06-21_

Release the first version.
