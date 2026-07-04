# presentation-feature-command

The command management feature. It contains **two** screens: a hierarchical command/folder browser (`CommandList`) and a create/edit form (`CommandForm`). Both follow Orbit MVI and map to the two data-class destinations of the same names.

Package root: `presentation.feature.command`.

## Screens

| Destination | Screen | ViewModel |
|-------------|--------|-----------|
| `CommandList(parentId, isParentRotary)` | `CommandListScreen` | `CommandListViewModel` |
| `CommandForm(commandId, isFolder, parentId, sessionId, sortOrder, isParentRotary)` | `CommandFormScreen` | `CommandFormViewModel` |

DI module: `presentationFeatureCommandModule` (`presentation.feature.command.di`) — both ViewModels are registered as **factories** so they can receive runtime parameters via `parametersOf(...)`.

## Command list

`CommandListScreen(parentId = 0, isParentRotary = false, viewModel = koinViewModel(...))` browses the command tree at the given folder level (`parentId = 0` is root). The ViewModel is keyed `"command_list_$parentId"` so each folder level gets its own instance. Content: `CommandListContent(state, onIntent, snackbarHostState, modifier)`.

`CommandListViewModel` constructor: `parentId`, `isParentRotary`, plus `ObserveCommandsUseCase` (live commands at `parentId`), `DeleteCommandUseCase`, and `SyncCommandsUseCase` (sync after delete).

`CommandListState`: `isLoading`, `parentId`, `isParentRotary`, `parentLabel`, `items: List<CommandItemUiModel>`, `pendingDeleteId`, `pendingDeleteLabel`.

`CommandListSideEffect`: `NavigateBack`, `NavigateToFolder(parentId, isRotary)`, `NavigateToForm(commandId, isFolder, parentId, sessionId, sortOrder, isParentRotary)`, `ShowError(message)`.

`CommandListIntent`: `OnBackClick`, `OnItemClick(id, isFolder)`, `OnAddCommandClick`, `OnAddFolderClick`, `OnDeleteClick(id, label, isFolder)`, `OnDeleteFolderClick`, `OnConfirmDelete`, `OnDismissDelete`.

## Command form

`CommandFormScreen(commandId = -1, isFolder = false, parentId = 0, sessionId = "", sortOrder = 0, isParentRotary = false, viewModel = koinViewModel(...))` creates or edits a command/folder. The ViewModel key prefers `"command_form_$sessionId"` (a fresh instance per create session) and falls back to `"command_form_${commandId}_${isFolder}"`. Content: `CommandFormContent(state, onIntent, snackbarHostState, modifier)`, which shows different fields depending on the `isFolder` / `isRotary` / `isMediaScreen` / `isAgentScreen` flags and can present a system-command picker.

`CommandFormViewModel` constructor: `commandId` (`-1` = create), `isFolder`, `parentId`, `initialSortOrder`, `isParentRotary`, plus `GetCommandByIdUseCase`, `SaveCommandUseCase`, `DeleteCommandUseCase`, `ExecuteCommandUseCase` (test run), and `SyncCommandsUseCase`.

`CommandFormState` (selected fields):

| Group | Fields |
|-------|--------|
| Core | `isLoading`, `isEditMode`, `isFolder`, `isRotary`, `isMediaScreen`, `isAgentScreen`, `sortOrder`, `isParentRotary`, `name`, `command`, `icon`, `parentId`, `commandType`, `availableCommandTypeModels`, `hasNameError`, `hasCommandError` |
| Rotary | `cwCommand`, `ccwCommand`, `cwCmdId`, `ccwCmdId`, `hasCwCommandError`, `hasCcwCommandError` |
| Media | `playPauseCommand`, `prevCommand`, `nextCommand`, `playPauseCmdId`, `prevCmdId`, `nextCmdId` |
| UI | `showDeleteConfirm`, `showSystemCommandPicker`, `systemCommandTarget` |

`CommandFormSideEffect`: `NavigateBack`, `ShowError(message)`.

`CommandFormIntent`: `OnBackClick`, `OnNameChanged`, `OnCommandChanged`, `OnIconChanged`, `OnCommandTypeModelChanged`, `OnTestRunClick`, `OnSaveClick`, `OnDeleteClick`, `OnDeleteConfirm`, `OnDeleteDismiss`, `OnIsRotaryChanged`, `OnIsMediaScreenChanged`, `OnIsAgentScreenChanged`, `OnBrowseSystemCommandClick(target)`, `OnSystemCommandSelected(command)`, `OnSystemCommandPickerDismiss`, and the per-direction command changes (`OnCwCommandChanged`, `OnCcwCommandChanged`, `OnPlayPauseCommandChanged`, `OnPrevCommandChanged`, `OnNextCommandChanged`).

### Save behavior

- **Standard leaf** — a single save.
- **Rotary leaf** — a three-step save: main entry, then CW and CCW virtual children, then update the main entry with their ids.
- **Media-screen leaf** — a multi-step save creating virtual children for the media controls, then updating the main entry.
- **Agent-screen leaf** — a single save with `isAgentScreen = true`.

## Navigation

- **In:** `Destination.CommandList` (from the device dashboard or from folder navigation) and `Destination.CommandForm` (create/edit).
- **Out:** `CommandForm` (create/edit), deeper `CommandList` folders, and back on save/delete/cancel.

## Dependencies

- Command use cases from `domain.usecase.api`.
- [navigation](../core/navigation.md), [styling](../core/styling.md), [ui](../core/ui.md).

## See also

- [device](device.md) · [preset](preset.md)
