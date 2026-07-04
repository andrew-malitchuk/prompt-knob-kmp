# data-repository-impl

> Repository implementations bridging domain repository interfaces to the data-layer sources (preferences, BLE, executor, runtime, database).

## Responsibility

Implements every repository interface declared in `domain-repository-api` by orchestrating calls to the data-layer API modules and converting between domain models and data-layer resources via internal mappers. All implementations and mappers are `internal`; only the Koin module is public.

## Dependencies

| Depends on | Purpose |
|---|---|
| `domain-repository-api` | Repository interfaces to implement |
| `domain-core` | Domain models (`BleDeviceModel`, `CommandNodeModel`, `PresetModel`, `ThemeModel`, …) |
| `data-preference-api` | Preference sources |
| `data-database-api` | Persistence sources |
| `data-ble-api` | BLE scan/connect sources |
| `data-executor-api` | Command executor sources |
| `data-runtime-api` | Claude hook runtime source |
| `data-core` | `Resource` marker interface |
| `common-core` | `Mapper` |
| kermit, kotlinx-datetime | Logging, timestamps |

## Public API

| Symbol | Description |
|---|---|
| `dataRepositoryImplModule` | Koin module binding all repository implementations as singletons |

## Repository Implementations (internal)

| Class | Implements |
|---|---|
| `BleRepositoryImpl` | `BleRepository` (delegates to `CommandRepository` + `CommandHistoryRepository`) |
| `CommandRepositoryImpl` | `CommandRepository` |
| `CommandHistoryRepositoryImpl` | `CommandHistoryRepository` |
| `PresetRepositoryImpl` | `PresetRepository` |
| `ConfigureRepositoryImpl` | `ConfigureRepository` |

## Mappers (internal, `core/mapper`)

`ModelResourceMapper<MODEL, RESOURCE>` is the base interface (bidirectional `toModel` / `toResource`, built on `common-core`'s `Mapper`). Concrete mappers:

| Object | Model ↔ Resource |
|---|---|
| `ThemePreferenceMapper` | `ThemeModel` ↔ `ThemePreference` |
| `OnboardingPreferenceMapper` | `OnboardingModel` ↔ `OnboardingPreference` |
| `LanguagePreferenceMapper` | `String?` ↔ `LanguagePreference` |
| `BleSessionMapper` | `BleDeviceModel` ↔ `BleSessionResource` |
| `CommandNodeMapper` | `CommandNodeModel` ↔ `CommandNodeResource` |
| `CommandNodeCacheMapper` | `CommandNodeModel` ↔ `CommandNodeCacheResource` |
| `CommandHistoryMapper` | command history model ↔ resource |
| `PresetMapper` | `PresetModel` ↔ `PresetResource` |
| `FirmwareVersionMapper` | `FirmwareVersionModel` ↔ `FirmwareVersionResource` |

> `ThemePreferenceMapper` and `OnboardingPreferenceMapper` implement `ModelResourceMapper`; the rest expose plain `toModel` / `toResource` `Mapper` fields on the object.

## Testing

```bash
./gradlew :data-repository-impl:test
```
