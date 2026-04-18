# compose-application

> Shared KMP application module — root composable, Koin initialization, and desktop entry point.

## Responsibility

Contains the shared `App()` composable that serves as the root of the UI hierarchy for all platforms. Observes language and theme preferences reactively, wraps content in `AppLocaleProvider` and `AppTheme`, and hosts the `NavigationHost`. Provides `initKoin()` which assembles the full dependency graph. Also includes a demo mode (`DEMO_MODE = true`) for previewing the design system and UI kit.

## Dependencies

| Depends on | Purpose |
|---|---|
| All `data-*-impl` modules | Data layer implementations |
| All `domain-*-impl` modules | Domain layer implementations |
| All `presentation-*` modules | UI screens, navigation, theming, localisation |

## Public API

| Function / Composable | Description |
|---|---|
| `App()` | Root composable — observes locale/theme, provides navigation host |
| `initKoin(config: KoinAppDeclaration)` | Starts Koin with all module registrations |
| `DemoHost` | Demo/style guide composable with tabs for design tokens and UI kit |

### Koin Modules Registered

Data: `dataPreferenceImplModule`, `dataRepositoryImplModule`
Domain: `domainUseCaseImplModule`
Presentation: All feature DI modules (splash, onboarding, home, settings, about)

## Usage

```kotlin
// Android — called from MainActivity
setContent {
    App()
}

// iOS — called from MainViewController
fun MainViewController() = ComposeUIViewController { App() }

// Koin initialization
initKoin {
    androidLogger()
    androidContext(this@PromptKnobApplication) // Android-specific
}
```

## Testing

```bash
./gradlew :compose-application:test
```
