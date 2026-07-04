# data-executor-impl

> Per-platform implementations of `CommandExecutor` and `SystemSettingsOpener`, wired into a single Koin module.

## Responsibility

Implements the executor contracts with a platform-appropriate backend and exposes them through `dataExecutorImplModule`. Each platform has a command router that dispatches a `CommandNodeModel` to the right mechanism (Android accessibility/assistant, macOS shell, iOS Siri Shortcuts) or a no-op on unsupported platforms.

## Dependencies

| Depends on | Purpose |
|---|---|
| `data-executor-api` | Contracts to implement (`api`) |
| `domain-core` | `CommandNodeModel`, `Optional` |
| Koin, Kermit | DI, logging |

Applies both the `library` and `di` convention plugins.

## Public API

| Symbol | Description |
|---|---|
| `dataExecutorImplModule` | Koin module binding `CommandExecutor` and `SystemSettingsOpener` per platform via `provideCommandExecutor()` |
| `AssistantRobotService` | **Android** `AccessibilityService` that performs assistant/robot actions |
| `PromptKnobDeviceAdminReceiver` | **Android** `DeviceAdminReceiver` for privileged system commands |

### Backends per platform

| Platform | `CommandExecutor` | `SystemSettingsOpener` | Notable helpers |
|---|---|---|---|
| Android | `AndroidCommandRouter` | `AndroidSystemSettingsOpener` | `AssistantPromptExecutor`, `AndroidSystemCommandExecutor` |
| iOS | `IosCommandRouter` | `NoOpSystemSettingsOpener` | `SiriShortcutExecutor`, `IosSystemCommandExecutor` |
| macOS | `MacOsCommandRouter` | `NoOpSystemSettingsOpener` | `ShellCommandExecutor`, `MacOsSystemCommandExecutor` |
| Desktop | `NoOpCommandExecutor` | `NoOpSystemSettingsOpener` | — |

## Platform notes

- **Android** — needs the accessibility service (`assistant_accessibility_config.xml`) and, for admin commands, the device-admin receiver (`device_admin_receiver.xml`); both must be declared in the app manifest.
- **iOS** — routes to Siri Shortcuts; settings deep-linking is a no-op.
- **macOS** — runs shell commands via `ShellCommandExecutor`.
- **Desktop** — no-op executor and settings opener.

## Testing

```bash
./gradlew :data-executor-impl:test
```
