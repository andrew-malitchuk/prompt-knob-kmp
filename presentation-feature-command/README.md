# presentation-feature-command

Command and folder management for a PromptKnob device: browse the command/folder tree and create/edit individual commands (including rotary CW/CCW leaf commands). Two Orbit MVI screens — a list and a form.

## Public API

| Screen | Content | ViewModel | Contract |
|--------|---------|-----------|----------|
| `CommandListScreen(parentId, isParentRotary)` | `CommandListContent` | `CommandListViewModel` | `CommandListState`, `CommandListIntent`, `CommandListSideEffect` |
| `CommandFormScreen(commandId, isFolder, parentId, sessionId, sortOrder, isParentRotary)` | `CommandFormContent` | `CommandFormViewModel` | `CommandFormState`, `CommandFormIntent`, `CommandFormSideEffect` |

Supporting types: `CommandItemUiModel` (list), `SystemCommandTarget` enum (form).

Side effects: list → `NavigateBack`, `NavigateToFolder`, `NavigateToForm`, `ShowError`; form → `NavigateBack`, `ShowError`.

## Destinations

Reachable via `Destination.CommandList(parentId, isParentRotary)` and `Destination.CommandForm(...)`. The list navigates deeper (folders) and into the form; the form returns to the list on save.

## Dependencies

Orbit (core/viewmodel/compose), Compose Material3, `domain-core`, `domain-usecase-api`, `presentation-core-localisation`, `presentation-core-navigation-api`, `presentation-core-styling`, `presentation-core-ui`, Kermit.

Applies `dev.prompt.knob.io.convention.feature` + `dev.prompt.knob.io.convention.di`. DI module: `presentationFeatureCommandModule`.
