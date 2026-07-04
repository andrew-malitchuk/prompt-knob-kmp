# data-executor-impl — agent guide

Concrete executor layer. Implements `data-executor-api` per platform and exposes one Koin module. Applies the `dev.prompt.knob.io.convention.library` **and** `dev.prompt.knob.io.convention.di` convention plugins.

## Responsibility

- Shared (`commonMain`): the Koin module `dataExecutorImplModule` and the `internal expect fun Module.provideCommandExecutor()` seam.
- Per-platform command routers (`AndroidCommandRouter`, `IosCommandRouter`, `MacOsCommandRouter`, `NoOpCommandExecutor`) plus their `SystemSettingsOpener` backends.
- Android framework entry points: `AssistantRobotService` (accessibility) and `PromptKnobDeviceAdminReceiver` (device admin).

## Rules

- API/Impl split: this is the **impl** side. Router/executor classes are `internal`; only `dataExecutorImplModule` and the Android framework classes are `public`.
- Platform DI goes through the `actual fun Module.provideCommandExecutor()` in `CommandExecutorProvider.<platform>.kt` — register new per-platform singletons there.
- `explicitApi` is enforced.

## Gotchas

- Adding a capability to `CommandExecutor` means updating **all four** platform routers or the build breaks on the missing `actual`.
- Android accessibility (`AssistantRobotService`) and device-admin (`PromptKnobDeviceAdminReceiver`) require the XML configs in `androidMain/res/xml` and manifest declarations — the executor silently no-ops if the service isn't enabled by the user.
- iOS/macOS `SystemSettingsOpener` is intentionally a no-op; don't rely on it to open OS screens there.
