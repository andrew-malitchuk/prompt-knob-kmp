# domain-repository-api

> Repository interfaces defining the contract between the domain and data layers.

## Responsibility

Declares the repository abstractions that use cases consume and the data layer implements. Enforces dependency inversion — upper layers depend only on these interfaces, never on concrete data implementations.

## Dependencies

| Depends on | Purpose |
|---|---|
| `domain-core` | Domain models, `Optional`, and `Failure` types |

## Public API

| Interface | Responsibility |
|---|---|
| `BleRepository` | BLE scanning, connect/disconnect, command sync, permission checks, last-device tracking, and connection-state / selected-command / battery / firmware / OTA flows |
| `CommandRepository` | CRUD over the persisted command tree — observe all / by parent, get, save, delete (incl. recursive by parent) |
| `CommandHistoryRepository` | Observe execution history, log executions, clear history |
| `PresetRepository` | Persisted presets — observe all, get by id, save, delete |
| `ConfigureRepository` | App configuration: theme, onboarding, language, MCP / Claude-hook toggles, and move-detector motion events |

### Representative method shapes

```kotlin
public interface BleRepository {
    public fun observeConnectionState(): Flow<DeviceConnectionModel>
    public fun scanForDevices(): Flow<BleDeviceModel>
    public suspend fun connect(address: String, name: String? = null): Optional
    public suspend fun syncCommands(commands: List<CommandNodeModel>): Optional
    // …
}
```

Flows are used for observable state; one-shot operations return `Optional` (`Result<Unit>`) or `Result<T>` and never throw.

## Testing

```bash
./gradlew :domain-repository-api:test
```
