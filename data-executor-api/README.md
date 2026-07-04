# data-executor-api

> Contracts for executing a selected command on the host platform and for opening the relevant system settings.

## Responsibility

Declares the platform-agnostic executor surface. Given a `CommandNodeModel` (from `domain-core`), a `CommandExecutor` performs the platform action and returns an `Optional` outcome. `SystemSettingsOpener` lets the app check and deep-link into the OS permission screens the executor needs. Implementations live in `data-executor-impl`.

## Dependencies

| Depends on | Purpose |
|---|---|
| `domain-core` | `CommandNodeModel`, `Optional` monad |
| `data-core` | shared data-layer types |

## Public API

| Interface | Description |
|---|---|
| `CommandExecutor` | `suspend fun execute(command: CommandNodeModel): Optional` — runs a command on the host |
| `SystemSettingsOpener` | `isAccessibilityServiceEnabled()`, `openAccessibilitySettings()`, `isNotificationPermissionGranted()`, `openNotificationSettings()` |

## Platform notes

Pure `commonMain` contracts — no platform code. Backends (accessibility, shell, Siri Shortcuts, no-op) are supplied by `data-executor-impl`.

## Testing

```bash
./gradlew :data-executor-api:test
```
