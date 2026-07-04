# aos-application — AI agent guide

Android app entry point. Thin host — the actual UI is the shared `App()` composable from `compose-application`.

Package root: `dev.prompt.knob.io`.

## Responsibility
- `core/PromptKnobApplication` — `Application`; the single place that starts Koin: `initKoin { androidLogger(); androidContext(this) }`. Must run before any injection.
- `source/activity/MainActivity` — `enableEdgeToEdge()` (before `super.onCreate`) then `setContent { App() }`. No business logic here.
- `source/tile/QuickCommandTileService` — Quick Settings `TileService`; injects BLE use cases (`ObserveConnectionStateUseCase`, `ConnectToDeviceUseCase`, `DisconnectDeviceUseCase`, `GetLastDeviceUseCase`) + `BleServiceController` via Koin `by inject()`.

## Gotchas
- Correct class locations: Application is under `.core`, Activity/Tile under `.source.*` — NOT at the package root.
- `QuickCommandTileService` recreates its `CoroutineScope` in `onStartListening` and cancels in `onStopListening`; `qsTile` may be null after stop — the null-guard in `updateTile` is required.
- Uses raw Android/Compose Gradle plugins, not the `dev.prompt.knob.io.convention.application` plugin. Build with `./gradlew :aos-application:assembleDebug`.
- `initKoin` itself lives in `compose-application`; add new DI modules there, not here.
