# presentation-core-ui

The shared component library, organised by **atomic design**: atoms &rarr; molecules &rarr; organisms. Every feature builds its screens from these components, all styled from the [styling](styling.md) tokens.

Package root: `presentation.core.ui.source.kit`.

## Atoms

The smallest building blocks, under `presentation.core.ui.source.kit.atom.*`.

| Group | Components |
|-------|-----------|
| Buttons | `Button`, `IconButton`, `TacticalButton`, `Toggle` |
| Input | `Input`, `FilledTextField`, `MultilineInput`, `CommandStringField` |
| Text | `AutoSizeText`, `SectionHeader`, `SectionHeaderWithIcon` |
| Icons | `AppIcon`, `AlertTriangle`, `Battery`, `BookOpen`, `ChevronLeft`, `ChevronRight`, `Clock`, `FileText`, `Folder`, `HardDrive`, `Home`, `Image`, `Lock`, `MoreVertical`, `Plus`, `RefreshCcw`, `Settings`, `Smartphone`, `Sun`, `Type`, `Wifi`, `Wizard` |
| Layout | `IconContainer`, `SafeContainer`, `MaxSideDetector`, `CornerFrame`, `CropBox` |
| Status | `StatusIndicator`, `SignalBars`, `WavyProgressIndicator`, `WavySlider`, `HorizontalAnimatedDivider` |
| Chips | `Chip` |
| Command controls | `BrowseSystemCommandButton`, `DeleteActionButton`, `SaveMacroButton`, `SystemStatusBar`, `RotaryDirectionBadge`, `TestRunButton` |
| Snackbars | `StackedSnackbarHost` |

## Molecules

Composites of atoms, under `presentation.core.ui.source.kit.molecule.*`.

| Group | Components |
|-------|-----------|
| Headers | `ScreenHeader`, `NavigationHeader`, `ActionHeader`, `SimpleHeader` |
| Cards & rows | `DeviceCard`, `ItemCard`, `SelectableCard`, `FolderNavigationCard`, `PresetRow`, `MacroKeyCard`, `InfoRow`, `LanguageOptionRow`, `SelectionRow` |
| Forms & sections | `FormSection`, `ButtonPreviewSection`, `IconPickerSection` |
| Commands | `CommandItemRow`, `CommandModeToggle`, `CommandTypePicker`, `CreatorToolbar`, `SystemCommandRow` |
| BLE | `ScanStatusCard`, `BleUnavailableContent`, `PermissionRequestContent`, `ScanTimeoutContent` |
| Device | `DeviceHeader`, `ProfileLoadSection` |
| Groups | `ChipGroup`, `SegmentedButtonGroup`, `TabBar` |
| States | `EmptyState`, `ErrorState` |
| Settings | `SettingRow` |

## Organisms

Full sections and interactive composites, under `presentation.core.ui.source.kit.organism.*`.

| Group | Components |
|-------|-----------|
| Sheets & modals | `AppBottomSheet`, `ConfirmationSheet`, `BluetoothDisabledSheet` |
| Device | `BleDeviceContent`, `StatusBatteryCard`, `MacroSection`, `HistorySection`, `OtaOverlay` |
| Interactions | `AppPullToRefreshBox`, `AnimatedSequenceHost` |

## Utilities

| Area | Location |
|------|----------|
| Animation, shimmer, modifier extensions, wavy-path & draw helpers | `presentation.core.ui.core.*` |
| Icon catalogue | `presentation.core.ui.source.icon.IconCatalogue` |
| Style-guide showcase | `presentation.core.ui.source.showcase.StyleguideScreen` |
| Custom shapes | `SquircleShape`, `GentleSquircleShape`, `CornerSmoothing` |

## DI

None — this is a pure composable library.

## See also

- [styling](styling.md) — the tokens these components consume.
- Feature docs assemble screens from this kit.
