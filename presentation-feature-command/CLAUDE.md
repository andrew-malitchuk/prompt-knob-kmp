# presentation-feature-command — AI agent guide

Two Orbit MVI screens under `presentation.feature.command`: `source/list` (`CommandList*`) and `source/form` (`CommandForm*`). Manages the command/folder tree of a device.

Applies `dev.prompt.knob.io.convention.feature` + `dev.prompt.knob.io.convention.di`.

## Structure (per screen)
- `*Screen()` — collects state, handles `*SideEffect` (navigation via `LocalAppNavigator.current`, errors via snackbar), delegates to `*Content`.
- `*Content` — stateless UI, emits `*Intent`.
- `*ViewModel : ContainerHost<*State, *SideEffect>` — `reduce {}` for state, `postSideEffect {}` for one-shot events.

## Gotchas
- Args flow from `Destination.CommandList/CommandForm` → `NavigationHost` `entry<>` `key` → `*Screen(...)` params → ViewModel factory params (Koin `parametersOf`). Keep the chain consistent.
- Form create-vs-edit: `commandId = -1` means create; `sessionId` forces a fresh ViewModel on repeated "create" navigations.
- Rotary folders: `isParentRotary` drives CW/CCW (`sortOrder` 0=CW, 1=CCW) labelling — do not drop it when adding leaf commands.
- Register ViewModels only in `presentationFeatureCommandModule` (factories with params).
