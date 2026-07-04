# presentation-core-ui — AI agent guide

Shared component library for PromptKnob, organised by atomic design (atoms → molecules). Every feature screen composes UI from here; features should not reinvent primitives.

Package root: `presentation.core.ui`. Applies `dev.prompt.knob.io.convention.feature`.

## Responsibility
- **Atoms** — buttons (`Button`, `IconButton`, `TacticalButton` + `ButtonStyle`), containers (`SafeContainer`, `IconContainer`), chips, toggles, inputs, sliders, progress, snackbars, dividers, shapes, shimmer, plus domain atoms (`SignalBars`, `RotaryDirectionBadge`, `SaveMacroButton`, `TestRunButton`, etc.) and the icon set.
- **Molecules** — cards/rows (`DeviceCard`, `PresetRow`, `MacroKeyCard`, `FolderNavigationCard`, `LanguageOptionRow`, `InfoRow`…), state views (`ErrorState`, `EmptyState`), BLE views (`ScanStatusCard`, `PermissionRequestContent`, `BleUnavailableContent`), and form/section helpers.
- `StyleguideScreen(onBack)` — scrollable showcase of all tokens and components (dev utility, routed from `Destination.Styleguide`); `IconCatalogue` lists the icon set.

## Gotchas
- Style everything through `Theme` from `presentation-core-styling` — never hardcode dp/sp/Color in components.
- No DI module and no ViewModels here — pure stateless composables. Hoist state to feature ViewModels.
- Uses stately-collections / atomicfu for immutable UI collections; prefer those over mutable state in shared components.
