# compose-application — AI agent guide

The composition root. Owns `App()`, the `initKoin` graph, and per-platform entry points. Every other module is wired together here (via `initKoin`) or in `presentation-core-navigation-impl` (via `NavigationHost`).

Package root: `dev.prompt.knob.io`. Applies `dev.prompt.knob.io.convention.application` + `dev.prompt.knob.io.convention.di`.

## Responsibility
- `source/app/App.kt` — observes `ObserveApplicationLanguageUseCase` + `ObserveThemeUseCase`, maps `ThemeModel`→`ThemeMode`, and provides `AppLocaleProvider` + `AppTheme` around `NavigationHost`. `DEMO_MODE` (const) swaps to `DemoHost`.
- `source/di/InitKoin.kt` — `initKoin(config)`; module order is load-bearing (data → domain → presentation). `KoinHelper.kt` exposes `doInitKoin()` for iOS.
- Entry points: iOS `MainViewController`, macOS/Desktop `main()`.

## Gotchas
- When you add a feature/data/domain impl module, register its Koin module in `initKoin` AND add the Gradle dependency here — otherwise Koin throws "No definition found" at first injection.
- **MCP is macOS-only and lazy**: `dataMcpImplModule` + `domainMcpUseCaseModule` are loaded with `loadKoinModules(...)` in `macosMain/main.kt`, not in `initKoin`. Do not add them to the common graph.
- macOS `main()` must call `NSApplication.sharedApplication()` before creating the Compose `Window`, and `.run()` last.
- Build note: `:compose-application:build` can OOM; prefer `:aos-application:assembleDebug` / `:compose-application:compileCommonMainKotlinMetadata`.
