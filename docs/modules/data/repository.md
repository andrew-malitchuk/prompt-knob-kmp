# data-repository

`data-repository-impl` is the wiring hub of PromptKnob's data layer. It implements every repository interface declared in [domain-repository-api](../domain/repository-api.md) by orchestrating the various data sources (BLE, database, preferences, runtime) and mapping their resources to domain models.

## Purpose

The domain and presentation layers depend only on repository interfaces. This module supplies the concrete implementations, so it is where "which data source answers this call" is decided. Repositories combine sources — for example, `BleRepositoryImpl` coordinates the BLE connection, the YAMK protocol, the last-device preference, the runtime session cache, and the command executor.

## Repository implementations

All in `data.repository.impl.source`, each bound to its domain interface.

| Implementation | Interface | Collaborators |
|----------------|-----------|---------------|
| `BleRepositoryImpl` | `BleRepository` | `BleConnection`, `BleScanner`, `BlePermissionChecker`, `BleServiceController`, `YamkProtocol`, `LastDevicePreferenceSource`, `CommandExecutor`, `BleSessionDataSource`, `CommandCacheDataSource` |
| `CommandRepositoryImpl` | `CommandRepository` | `CommandNodeDataSource` |
| `CommandHistoryRepositoryImpl` | `CommandHistoryRepository` | `CommandHistoryDataSource` |
| `PresetRepositoryImpl` | `PresetRepository` | `PresetDataSource` |
| `ConfigureRepositoryImpl` | `ConfigureRepository` | `ThemePreferenceSource`, `OnboardingPreferenceSource`, `LanguagePreferenceSource`, `McpPreferenceSource`, `ClaudeHookPreferenceSource`, `MoveDetectorDataSource` |

## Mappers

Mappers translate between data-source resources and domain models. They live in `data.repository.impl.core.mapper` and extend the base `ModelResourceMapper<M, R>` (which pairs a model-to-resource and resource-to-model mapping).

| Mapper | Between |
|--------|---------|
| `CommandNodeMapper` | `CommandNodeResource` ↔ `CommandNodeModel` |
| `CommandHistoryMapper` | `CommandHistoryResource` ↔ `CommandHistoryEntryModel` |
| `PresetMapper` | `PresetResource` ↔ `PresetModel` |
| `BleSessionMapper` | `BleSessionResource` ↔ device model |
| `CommandNodeCacheMapper` | `CommandNodeCacheResource` ↔ `CommandNodeModel` |
| `FirmwareVersionMapper` | `FirmwareVersionResource` ↔ `FirmwareVersionModel` |
| `ThemePreferenceMapper` | `ThemePreference` ↔ `ThemeModel` |
| `OnboardingPreferenceMapper` | `OnboardingPreference` ↔ `OnboardingModel` |
| `LanguagePreferenceMapper` | `LanguagePreference` ↔ language code |

## Dependency injection

The Koin module `dataRepositoryImplModule` (`data.repository.impl.di`) registers all mappers and binds each `…RepositoryImpl` to its domain interface. This module depends on the data-source Koin modules (`dataBleImplModule`, `dataDatabaseImplModule`, `dataPreferenceImplModule`, `dataRuntimeImplModule`) being present in the graph.

## Platform notes

Everything is in `commonMain`. Platform differences are entirely contained within the underlying data-source modules; the repositories themselves are platform-agnostic.

## Dependencies

- `domain-repository-api`, `domain-core`
- `common-core` (`Mapper`)
- All data-source API modules: `data-ble-api`, `data-database-api`, `data-preference-api`, `data-runtime-api`, `data-executor-api`
- Koin
