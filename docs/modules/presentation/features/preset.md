# presentation-feature-preset

The preset gallery. It shows bundled read-only gallery presets and the user's saved presets, and supports saving the current command tree, loading a preset onto the knob, exporting to JSON (share sheet), importing from JSON, and deleting. Orbit MVI structure.

Package root: `presentation.feature.preset`.

## Structure

| Layer | Type | Package |
|-------|------|---------|
| Screen | `PresetListScreen(viewModel = koinViewModel())` | `presentation.feature.preset.source` |
| Content | `PresetListContent(state, onIntent, snackbarHostState)` | `presentation.feature.preset.source` |
| ViewModel | `PresetListViewModel : ContainerHost<PresetListState, PresetListSideEffect>` | `presentation.feature.preset.source` |
| Contract | `PresetListState`, `PresetListSideEffect` | `presentation.feature.preset.source` |
| Intent | `PresetListIntent` | `presentation.feature.preset.source` |
| DI | `presentationFeaturePresetModule` | `presentation.feature.preset.di` |

The Screen uses `LocalAppNavigator.current`. The Content renders gallery cards, the user's saved presets, and an apply-confirmation sheet.

## Contract

`PresetListState`: `isLoading`, `gallery: List<GalleryPreset>`, `presets: List<PresetUiModel>`, `pendingDeleteId`, `pendingDeleteLabel`, `showSaveDialog`, `pendingGalleryPreset`.

`PresetListSideEffect` (sealed): `NavigateBack`, `ShowError(message)`, `ShareExport(json)` (triggers the platform share sheet).

`PresetListIntent` (sealed): `OnBackClick`, `OnGalleryPresetClick(preset)`, `OnConfirmGalleryPreset`, `OnDismissGalleryPreset`, `OnSaveClick`, `OnSaveConfirm(name)`, `OnSaveDismiss`, `OnLoadPreset(id)`, `OnDeleteClick(id, label)`, `OnConfirmDelete`, `OnDismissDelete`, `OnExportPreset(id)`.

## ViewModel

`PresetListViewModel` injects:

| Dependency | Role |
|------------|------|
| `GetGalleryPresetsUseCase` | Bundled read-only gallery. |
| `ObservePresetsUseCase` | User-saved presets (reactive). |
| `SavePresetUseCase` | Save the current command tree as a preset. |
| `LoadPresetUseCase` | Activate a preset (replaces the command tree). |
| `DeletePresetUseCase` | Delete a saved preset. |
| `ExportPresetUseCase` | Serialize a preset to JSON. |
| `ImportPresetUseCase` | Deserialize a preset from JSON. |
| `SyncCommandsUseCase` | Push the new layout to the device after load/import. |

Flows:

- **Gallery** — tap a gallery preset &rarr; confirmation sheet &rarr; import from JSON &rarr; load &rarr; sync.
- **Save** — tap Save &rarr; name dialog &rarr; persist the current tree.
- **Load** — tap a saved preset &rarr; load &rarr; sync.
- **Export** — tap Export &rarr; serialize to JSON &rarr; `ShareExport` triggers the platform share sheet.
- **Delete** — confirm &rarr; remove from the database.

## Navigation

- **In:** `Destination.Presets` (from the device dashboard).
- **Out:** back on cancel / after applying a preset.

## Dependencies

- Preset and command use cases from `domain.usecase.api`.
- [navigation](../core/navigation.md), [styling](../core/styling.md), [ui](../core/ui.md).

## See also

- [device](device.md) · [command](command.md)
