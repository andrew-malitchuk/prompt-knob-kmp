# compose-application

The shared Kotlin Multiplatform entry-point module. It defines the root `App()` composable, wires the entire Koin dependency graph through `initKoin`, and provides the platform-specific entry points for Android, iOS, macOS, and Desktop (JVM).

Package: `dev.prompt.knob.io`. Source sets: `commonMain`, `iosMain`, `macosMain`, `desktopMain` (the Android entry point lives in [`aos-application`](aos-application.md)).

## Key types

| Type | Source set / Package | Role |
|------|----------------------|------|
| `App()` | `commonMain` · `dev.prompt.knob.io.source.app` | Root composable. Wires locale + theme + navigation. |
| `initKoin(config)` | `commonMain` · `dev.prompt.knob.io.source.di` | Starts Koin and registers every application module. |
| `doInitKoin()` | `commonMain` · `dev.prompt.knob.io.source.di` | Zero-config wrapper over `initKoin` for the iOS entry point. |
| `MainViewController()` | `iosMain` · `dev.prompt.knob.io.source.viewcontroller` | Returns a `UIViewController` wrapping `App()`. |
| `main()` | `macosMain` · `dev.prompt.knob.io.source.entry` | macOS native entry point. |
| `main()` | `desktopMain` · `dev.prompt.knob.io.source.entry` | Desktop (JVM) entry point. |

## App()

The root composable observes the persisted language and theme reactively, then wraps the navigation graph in the locale and theme providers:

```kotlin
@Composable
public fun App() {
    val languageCode by observeLanguage().map { it.getOrNull() ?: "en" }.collectAsState("en")
    val themeMode by observeTheme().map { /* ThemeModel -> ThemeMode */ }.collectAsState(ThemeMode.Light)

    AppLocaleProvider(languageCode = languageCode) {
        AppTheme(mode = themeMode) {
            NavigationHost()
        }
    }
}
```

- Language comes from `ObserveApplicationLanguageUseCase` (`domain.usecase.api.source.usecase.configuration`), defaulting to `"en"`.
- Theme comes from `ObserveThemeUseCase`, mapping `ThemeModel` (`Light` / `Dark` / `MaterialU`) to the styling module's `ThemeMode` (`Light` / `Dark` / `System`), defaulting to `Light` on first launch.
- Providers: `AppLocaleProvider` (localisation), `AppTheme` (styling), then `NavigationHost` (navigation).
- A private `DEMO_MODE` flag (default `false`) can swap `NavigationHost` for `DemoHost` — the internal style-guide / UI-kit demo screens under `dev.prompt.knob.io.core.demo`.

## initKoin

`initKoin` starts Koin and registers modules in dependency order (data &rarr; domain &rarr; presentation). Module order is significant because domain modules depend on data contracts and presentation modules depend on domain use cases.

Registered in `commonMain`:

| Module | Package |
|--------|---------|
| `dataPreferenceImplModule` | `data.preference.impl.di` |
| `dataBleImplModule` | `data.ble.impl.di` |
| `dataDatabaseImplModule` | `data.database.impl.di` |
| `dataRuntimeImplModule` | `data.runtime.impl.di` |
| `dataRepositoryImplModule` | `data.repository.impl.di` |
| `dataExecutorImplModule` | `data.executor.impl.di` |
| `domainUseCaseImplModule` | `domain.usecase.impl.di` |
| `presentationFeatureAboutModule` | `presentation.feature.about.di` |
| `presentationFeatureCommandModule` | `presentation.feature.command.di` |
| `presentationFeatureDevicesModule` | `presentation.feature.devices.di` |
| `presentationFeatureDeviceModule` | `presentation.feature.device.di` |
| `presentationFeatureHomeModule` | `presentation.feature.home.di` |
| `presentationFeatureOnboardingModule` | `presentation.feature.onboarding.di` |
| `presentationFeaturePresetModule` | `presentation.feature.preset.di` |
| `presentationFeatureSettingsModule` | `presentation.feature.settings.di` |
| `presentationFeatureSplashModule` | `presentation.feature.splash.di` |

### Lazily-loaded MCP modules (macOS only)

The MCP data module and its domain use cases are **not** registered in the common `initKoin`. They are loaded lazily from the macOS entry point after the common graph starts, because MCP and the Claude hook server are macOS-only concerns:

| Module | Package |
|--------|---------|
| `dataMcpImplModule` | `data.mcp.impl.di` |
| `domainMcpUseCaseModule` | `domain.usecase.impl.di` |

## Platform entry points

### Android

Android has no entry point here — [`aos-application`](aos-application.md) supplies `PromptKnobApplication` (calls `initKoin`) and `MainActivity` (calls `setContent { App() }`).

### iOS (`iosMain`)

```kotlin
public fun MainViewController(): UIViewController = ComposeUIViewController { App() }
```

`MainViewController()` is hosted by SwiftUI in the Xcode project via `UIViewControllerRepresentable`. `doInitKoin()` bootstraps DI from Swift.

### macOS (`macosMain`)

```kotlin
public fun main() {
    initKoin()
    loadKoinModules(listOf(dataMcpImplModule, domainMcpUseCaseModule))
    // start MCP server; conditionally start Claude hook server
    NSApplication.sharedApplication()
    Window("PromptKnob") { App() }
    NSApplication.sharedApplication().run()
}
```

Uses Compose Multiplatform native rendering (Skiko/Metal) with real CoreBluetooth BLE via Kable. After starting the common graph it loads the MCP modules, launches `StartMcpServerUseCase`, and — when `GetClaudeHookEnabledUseCase` returns `true` — launches `StartClaudeHookServerUseCase`. `NSApplication.sharedApplication()` must be initialised before the Compose window; `.run()` starts the AppKit run loop and never returns.

### Desktop / JVM (`desktopMain`)

```kotlin
public fun main() {
    initKoin()
    application {
        Window(onCloseRequest = ::exitApplication, alwaysOnTop = true, title = "PromptKnob") {
            App()
        }
    }
}
```

Renders an always-on-top window so the companion panel stays visible while the user works in other applications. This target uses no-op BLE stubs (real CoreBluetooth support is macOS-only).

## Dependencies

- [Presentation core](../presentation/core/navigation.md) — `NavigationHost`, `AppLocaleProvider`, `AppTheme`.
- [Domain use cases](../domain/usecase.md) — configuration and MCP use cases observed by `App()` / macOS `main()`.
- All data `impl` modules — registered in `initKoin`.

## See also

- [aos-application](aos-application.md) — the Android host.
