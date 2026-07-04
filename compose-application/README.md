# compose-application

Shared KMP application module for PromptKnob. Holds the root `App()` composable, the `initKoin` DI bootstrap, and the platform entry points (Android via `MainActivity`, iOS via `MainViewController`, macOS/Desktop via `main()`).

## Public API

| Symbol | Source set | Description |
|--------|-----------|-------------|
| `App()` | commonMain | Root composable; observes language + theme reactively, wraps `NavigationHost` in `AppLocaleProvider` and `AppTheme`. A `DEMO_MODE` flag swaps in `DemoHost`. |
| `initKoin(config)` | commonMain | Starts Koin with the full application graph (see below) |
| `doInitKoin()` | commonMain | No-arg wrapper used by the iOS entry point |
| `MainViewController()` | iosMain | `ComposeUIViewController { App() }` |
| `main()` | macosMain / desktopMain | Native/JVM window hosting `App()` |

## Koin modules registered by `initKoin`

Registered in dependency order (data → domain → presentation):

- **Data:** `dataPreferenceImplModule`, `dataBleImplModule`, `dataDatabaseImplModule`, `dataRuntimeImplModule`, `dataRepositoryImplModule`, `dataExecutorImplModule`
- **Domain:** `domainUseCaseImplModule`
- **Presentation (feature):** `presentationFeatureAboutModule`, `presentationFeatureCommandModule`, `presentationFeatureDevicesModule`, `presentationFeatureDeviceModule`, `presentationFeatureHomeModule`, `presentationFeatureOnboardingModule`, `presentationFeaturePresetModule`, `presentationFeatureSettingsModule`, `presentationFeatureSplashModule`

**macOS-only, lazily loaded:** `dataMcpImplModule` and `domainMcpUseCaseModule` are loaded via `loadKoinModules(...)` in the macOS `main()` after `initKoin`, then the MCP / Claude-hook servers are started. They are NOT part of the common graph.

## Platform notes

- macOS uses real CoreBluetooth BLE (Kable) and hosts the MCP servers; JVM desktop uses no-op BLE stubs and does not load MCP.
- iOS: static frameworks; entry via `MainViewController()`.

## Dependencies

`domain-core`, all `data-*-impl` modules (`dataMcpImpl` macOS-only), `domain-usecase-impl`, and every `presentation-*` module. Applies `dev.prompt.knob.io.convention.application` + `dev.prompt.knob.io.convention.di`.
