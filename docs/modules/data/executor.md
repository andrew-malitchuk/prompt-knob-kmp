# data-executor

The `data-executor` modules run the action attached to a command node on the host platform — a system media/brightness control, a shell command, a Siri Shortcut, or an on-device assistant prompt. Each platform executes commands in its own native way behind one shared interface.

The pair splits into:

- **data-executor-api** — the execution contract and a system-settings helper.
- **data-executor-impl** — the per-platform routers and executors.

## Purpose

When the knob selects a command, it must be carried out on the connected device. This module abstracts "carry out this command" into a single `CommandExecutor` and provides platform-appropriate implementations. The command's `commandType` decides which underlying executor a platform router dispatches to.

## Key types (data-executor-api)

| Type | Kind | Responsibility |
|------|------|----------------|
| `CommandExecutor` | interface | `execute(command: CommandNodeModel): Optional` — run a command, returning success/failure. |
| `SystemSettingsOpener` | interface | Query and open OS settings: `isAccessibilityServiceEnabled()`, `openAccessibilitySettings()`, `isNotificationPermissionGranted()`, `openNotificationSettings()`. |

Both live in `commonMain`; there is no platform-specific API.

## Key types (data-executor-impl)

Each platform provides a router implementing `CommandExecutor` plus the executors it delegates to. The Koin module `dataExecutorImplModule` (`data.executor.impl.di`) delegates registration to a per-platform `CommandExecutorProvider`.

| Platform | Router | Executors |
|----------|--------|-----------|
| Android | `AndroidCommandRouter` | `AssistantPromptExecutor` (drives `AssistantRobotService`, an `AccessibilityService`), `AndroidSystemCommandExecutor` |
| macOS | `MacOsCommandRouter` | `ShellCommandExecutor` (runs `/bin/zsh -l -c` to load the login profile), `MacOsSystemCommandExecutor` |
| iOS | `IosCommandRouter` | `SiriShortcutExecutor` (opens `shortcuts://run-shortcut?name=…`), `IosSystemCommandExecutor` |
| Desktop (JVM) | `NoOpCommandExecutor` | none (logging only) |

### Platform capabilities

- **Android** — `AndroidSystemCommandExecutor` handles system actions; `AssistantPromptExecutor` sends a prompt to the on-device assistant via the `AssistantRobotService` accessibility service. The Android `SystemSettingsOpener` can open accessibility and notification settings.
- **macOS** — `MacOsSystemCommandExecutor` covers media, volume, brightness, Mission Control, lock/sleep, screenshot, and similar system actions; `ShellCommandExecutor` runs arbitrary shell commands under the user's login shell.
- **iOS** — `IosSystemCommandExecutor` maps system actions (including directional VOLUME/BRIGHTNESS) to named Shortcuts, launched through `SiriShortcutExecutor`.
- **Desktop** — every operation is a no-op; `NoOpSystemSettingsOpener` is registered for the settings helper.

On iOS, macOS, and Desktop the `SystemSettingsOpener` binding is `NoOpSystemSettingsOpener`.

## Dependencies

- `data-executor-api` and, transitively, `domain-core` (`CommandNodeModel`, `Optional`)
- Koin (with `koin-android` on Android)
