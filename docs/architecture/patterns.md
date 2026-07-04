# Patterns

This page collects the recurring patterns PromptKnob uses across its modules: the API/Impl split, Orbit MVI feature structure, Koin dependency injection, the repository pattern, and the atomic-design UI kit. For where the modules live, see [Module structure](module-structure.md).

## API / Impl split

Most data and domain layers are two modules — one holding the public contract, one holding the implementation:

- `*-api` — interfaces and models only. Public declarations are visible because [`explicitApi()`](convention-plugins.md#explicit-api-mode) forces explicit visibility and return types.
- `*-impl` — the concrete classes plus a Koin `module { }` that binds implementation → interface.

For example, `domain-repository-api` declares `CommandRepository`, and `data-repository-impl` provides `CommandRepositoryImpl` and registers it in `dataRepositoryImplModule`. Callers depend on the api module and receive the implementation through Koin; only the DI aggregation in `compose-application` references the impl module. This keeps the dependency graph acyclic and lets any implementation be swapped without touching callers.

The `*-core` modules (`domain-core`, `data-core`, `common-core`) hold pure types shared across the split:

- `domain-core` — domain models such as `CommandNodeModel`, plus the `Optional` and `Failure` monads used as use-case return types.
- `data-core` — the `Resource` marker interface implemented by database entities, preference models, and DTOs.
- `common-core` — architectural helpers: the `Mapper<I, O>` functional interface (`fun map(input: I): O`) used to convert `Resource` ↔ `Model`, and `Execute`.

## Orbit MVI feature structure

Every `presentation-feature-*` module follows the same [Orbit MVI](https://orbit-mvi.org/) shape. Using `presentation-feature-about` as the canonical example, a feature is made of:

| File | Responsibility |
|------|----------------|
| `*Contract.kt` | `State` (data class), `SideEffect` (sealed), `Intent` (sealed) |
| `*ViewModel.kt` | `ContainerHost<State, SideEffect>` — the Orbit container |
| `*Screen.kt` | Public composable; injects the ViewModel, collects state/side-effects |
| `*Content.kt` | `internal` composable that renders state and forwards intents |
| `di/*Module.kt` | Koin module registering the ViewModel |

The ViewModel is a `ContainerHost`. It exposes a `container` built with the initial state, routes intents through a `handleIntent` function, and mutates state or emits one-shot effects inside `intent { … }` blocks (`reduce { … }` for state, `postSideEffect(…)` for effects):

```kotlin
@OrbitExperimental
public class AboutViewModel : ContainerHost<AboutState, AboutSideEffect>, ViewModel() {

    override val container: Container<AboutState, AboutSideEffect> =
        container(AboutState())

    public fun handleIntent(intent: AboutIntent) {
        when (intent) {
            AboutIntent.OnBackClick -> intent {
                postSideEffect(AboutSideEffect.GoBackEffect)
            }
        }
    }
}
```

The screen collects state and side effects and delegates rendering:

```kotlin
@Composable
public fun AboutScreen(viewModel: AboutViewModel = koinViewModel()) {
    val state by viewModel.collectAsState()
    viewModel.collectSideEffect { effect -> /* handle GoBackEffect, ShowError … */ }
    AboutContent(state = state, onIntent = viewModel::handleIntent)
}
```

When a screen has loading and loaded states, the `Content` splits further — About uses `AboutSuccessContent` and a shimmer placeholder `AboutShimmerContent`. See [Adding a feature](../development/add-feature.md) for the full walkthrough.

## Navigation

Navigation is a thin custom layer over AndroidX Navigation 3. Screens are identified by a `@Serializable sealed class Destination : NavKey` in `presentation-core-navigation-api` with 12 members: `Splash`, `Onboarding`, `Home`, `Settings`, `About`, `Styleguide`, `Devices`, `Device`, `LanguagePicker`, `Presets`, `CommandList`, and `CommandForm`. Most are `data object`s; `CommandList` and `CommandForm` are `data class`es carrying arguments (parent id, command id, etc.).

`NavigationHost` (in `presentation-core-navigation-impl`) maps each `Destination` to its screen through an `entryProvider { }` block over a Navigation 3 `NavBackStack<NavKey>`. It provides an `AppNavigator` (implemented by `AppNavigatorImpl`) and a back action through `CompositionLocal`s (`LocalAppNavigator`, `LocalBackAction`). `AppNavigatorImpl.navigate` supports `Default`, `SingleTop`, and `ClearTask` options; `popBackStack` keeps at least one entry. All 12 destinations are also registered in `navSavedStateConfiguration` for process-death persistence.

## Koin dependency injection

Each impl module owns a Koin `module { }` and binds implementations to their api interfaces. Common idioms:

- `singleOf(::YamkCodecImpl) bind YamkCodec::class` — single instance bound to its interface.
- `single { YamkProtocolImpl(connection = get(), codec = get()) } bind YamkProtocol::class` — explicit construction with `get()` injection.
- `viewModelOf(::AboutViewModel)` — ViewModels in feature modules.
- `expect`/`actual` module extensions (e.g. `Module.provideBleAdapter()`) provide platform-specific bindings.

All modules are aggregated in dependency order in `initKoin()` (data → domain → presentation); macOS-only modules (`dataMcpImplModule`, `domainMcpUseCaseModule`) are loaded dynamically from the macOS entry point. See the [architecture overview](overview.md#dependency-injection-with-koin).

## Repository pattern

Repositories are the seam between domain use cases and the data sources. A repository interface lives in `domain-repository-api`; its implementation in `data-repository-impl` wraps one or more data sources and maps their `Resource` types to domain `Model`s via `Mapper`.

For example, `CommandRepositoryImpl` wraps `CommandNodeDataSource` (from `data-database-impl`), maps entities with a `CommandNodeMapper`, and exposes flows/suspend functions like `observeAll()`, `observeByParentId()`, `getById()`, `save()`, and `deleteById()`. Use cases (`domain-usecase-impl`) depend only on the repository interface, never on the data sources directly.

## Atomic-design UI kit

The shared UI lives in `presentation-core-ui` under `.../source/kit/`, organized by [atomic design](https://bradfrost.com/blog/post/atomic-web-design/):

- **atoms** — the smallest building blocks, grouped by kind: `button/`, `text/`, `icon/`, `input/`, `shape/` (e.g. `SquircleShape`), `indicator/`, `slider/`, `chip/`, `divider/`, `progress/`, `snackbar/`, and domain-flavored atoms under `ble/` (`SignalBars`) and `command/`.
- **molecules** — atoms composed into functional units: `bar/` (headers, toolbars), `item/` (`DeviceCard`, `PresetRow`, `SelectableCard`), `form/`, `state/` (`EmptyState`, `ErrorState`), `command/`, `device/`, `setting/`.
- **organisms** — larger, region-level components: `bottomsheet/` (`AppBottomSheet`, `ConfirmationSheet`), `ble/` (`BleDeviceContent`, `BluetoothDisabledSheet`), `device/` (`StatusBatteryCard`, `OtaOverlay`), `pulltorefresh/`.

Feature `Content` composables build their UI from these components rather than defining ad-hoc widgets, which keeps styling consistent across screens. Theme tokens (colors, typography using the Space Grotesk typeface) come from `presentation-core-styling`.

## Related pages

- [Architecture overview](overview.md)
- [Module structure](module-structure.md)
- [Convention plugins](convention-plugins.md)
- [Adding a feature](../development/add-feature.md)
