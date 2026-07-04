# domain-repository-api

`domain-repository-api` declares the repository contracts that separate PromptKnob's business logic from its data sources. Use cases depend on these interfaces; [data-repository-impl](../data/repository.md) provides the implementations.

## Purpose

Five interfaces cover the app's persistent and live data. They deal purely in [domain models](core.md) and the `Optional` (`Result<Unit>`) result type — no resources, entities, or platform types leak through.

## Repository interfaces

All in `domain.repository.api.source.repository`.

| Interface | Responsibility |
|-----------|----------------|
| `BleRepository` | Everything BLE: connection state, scanning, connect/disconnect, command sync, permissions, last-device memory, firmware/battery observation, OTA, and the knob-facing sends (approval, choice, notify, Claude state). |
| `CommandRepository` | CRUD over the command tree. |
| `CommandHistoryRepository` | Read and append the execution log. |
| `PresetRepository` | CRUD over saved presets. |
| `ConfigureRepository` | App configuration: theme, onboarding, language, MCP/Claude-hook toggles, and move-detector motion. |

### BleRepository (selected members)

```kotlin
fun observeConnectionState(): Flow<DeviceConnectionModel>
fun scanForDevices(): Flow<BleDeviceModel>
suspend fun connect(address: String, name: String? = null): Optional
suspend fun disconnect(): Optional
suspend fun syncCommands(commands: List<CommandNodeModel>): Optional
fun observeSelectedCommand(): Flow<CommandNodeModel>
fun observeFirmwareVersion(): Flow<FirmwareVersionModel?>
fun observeBatteryLevel(): Flow<Int?>
suspend fun sendShowApproval(): Optional
suspend fun sendShowChoice(options: List<String>): Optional
suspend fun sendNotify(message: String, level: Int = 0): Optional
suspend fun sendClaudeState(state: ClaudeState): Optional
fun startOta(firmware: ByteArray, version: FirmwareVersionModel): Flow<OtaStateModel>
```

It also exposes last-device helpers (`getLastConnectedDevice`, `clearLastConnectedDevice`), permission checks, the auto-reconnect skip flag (`markSkipAutoReconnect` / `consumeSkipAutoReconnect`), and the raw selected-command id stream.

### CommandRepository

```kotlin
fun observeAll(): Flow<List<CommandNodeModel>>
fun observeByParentId(parentId: Int): Flow<List<CommandNodeModel>>
suspend fun getById(id: Int): CommandNodeModel?
suspend fun save(command: CommandNodeModel): Result<Int>
suspend fun deleteById(id: Int): Optional
suspend fun deleteAll(): Optional
suspend fun deleteByParentIdRecursive(parentId: Int): Optional
```

### CommandHistoryRepository

```kotlin
fun observeAll(): Flow<List<CommandHistoryEntryModel>>
suspend fun logExecution(node: CommandNodeModel, success: Boolean)
suspend fun clearAll(): Optional
```

### PresetRepository

```kotlin
fun observeAll(): Flow<List<PresetModel>>
suspend fun getById(id: Int): PresetModel?
suspend fun save(preset: PresetModel): Result<Int>
suspend fun deleteById(id: Int): Optional
```

### ConfigureRepository

Getters/setters/observers for `ThemeModel`, `OnboardingModel`, application language, and the `mcpEnabled` / `claudeHookEnabled` toggles, plus `observeMoveDetectorMotion()` / `emitMoveDetectorMotion()`.

## Dependencies

- `domain-core` (models, `Optional`)
- kotlinx-coroutines (`Flow`)

## Platform notes

`commonMain` only — the interfaces are platform-agnostic. Platform behaviour lives in the implementations.
