# Changelog

## 0.2.1

_2023.02.20_

SnapPicker 0.2.1 is released.
Version 0.2.1 contains [these commits](https://github.com/cheonjaewoong/snappicker/compare/v0.2.0...v0.2.1).

### Bug Fixes

- Fix that scrolling is sometimes not working when `DateSnapPicker` have to force scrolling to the last date.
- `rememberDateSnapPicker` and `rememberTimeSnapPicker` was recreate its state on unintended time.

## 0.2.0

_2023.02.19_

SnapPicker 0.2.0 is released.
Version 0.2.0 contains [these commits](https://github.com/cheonjaewoong/snappicker/compare/v0.1.0...v0.2.0).

### Features

- Add time picker and date picker, state classes.
- New parameter `itemWidth` for `HorizontalSnapPicker` and `itemHeight` for `VerticalSnapPicker`.
- New `decorationBox` composable lambda parameter to allow to add decoration around picker.
- `itemContent` of `HorizontalSnapPicker` and `VerticalSnapPicker` is now extends `BoxScope`.
- `SnapPickerState` now has `isScrollInProgress`, `scrollToItem` and `animateScrollToItem` API.

## 0.1.0

_2023.02.10_

The first version of SnapPicker library, `io.woong.snappicker:snappicker-compose:0.1.0`, is released.
Version 0.1.0 contains [these commits](https://github.com/cheonjaewoong/snappicker/commits/v0.1.0).
