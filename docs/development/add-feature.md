# Adding a Feature

A "feature" in PromptKnob is a `presentation-feature-*` module that owns one screen (or a small cluster of screens). Every feature follows the same Orbit MVI structure and is wired into the app through a `Destination`, the `NavigationHost`, and a Koin module.

This guide uses `presentation-feature-about` as the reference example. For the general mechanics of creating a Gradle module see [Adding a module](add-module.md); for the patterns behind the code see [Patterns](../architecture/patterns.md).

## 1. Create the module

Add the module to `settings.gradle.kts` in the `presentation-feature` group:

```kotlin
// presentation-feature
include(":presentation-feature-about")
include(":presentation-feature-example") // <- your new module
```

Create `presentation-feature-example/build.gradle.kts` applying the feature convention plugin (and `di`, since a feature registers a ViewModel with Koin). Match the real About module's build script — plugins are applied by `id(...)` and dependencies are declared inside `kotlin { sourceSets { … } }`:

```kotlin
plugins {
    id("dev.prompt.knob.io.convention.feature")
    id("dev.prompt.knob.io.convention.di")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.orbit.core)
            implementation(libs.orbit.viewmodel)
            implementation(libs.orbit.compose)

            implementation(projects.domainCore)
            implementation(projects.domainUsecaseApi) // if the screen needs use cases
            implementation(projects.presentationCoreLocalisation)
            implementation(projects.presentationCoreNavigationApi)
            implementation(projects.presentationCoreStyling)
            implementation(projects.presentationCoreUi)
        }
    }
}
```

The `feature` plugin brings in Compose, coroutines, and lifecycle, sets up all platform targets, and enables `explicitApi()`. The `di` plugin adds Koin. The Orbit dependencies are declared per-module. See [Convention plugins](../architecture/convention-plugins.md).

## 2. Write the Orbit MVI files

`presentation-feature-about` keeps its screen files under
`src/commonMain/kotlin/presentation/feature/about/source/about/` and its DI under
`.../about/di/`. Mirror that layout for your feature. The pieces are:

### Contract (`ExampleContract.kt`)

State, side effects, and intents:

```kotlin
public data class ExampleState(
    val isLoading: Boolean = false,
)

public sealed class ExampleSideEffect {
    public data object GoBackEffect : ExampleSideEffect()
    public data class ShowError(val messageId: Int) : ExampleSideEffect()
}

public sealed class ExampleIntent {
    public data object OnBackClick : ExampleIntent()
}
```

### ViewModel (`ExampleViewModel.kt`)

A `ContainerHost` over the state and side-effect types. Intents are routed through a public `handleIntent`, and side effects are posted with `postSideEffect`:

```kotlin
@OrbitExperimental
public class ExampleViewModel : ContainerHost<ExampleState, ExampleSideEffect>, ViewModel() {

    override val container: Container<ExampleState, ExampleSideEffect> =
        container(ExampleState())

    public fun handleIntent(intent: ExampleIntent) {
        when (intent) {
            ExampleIntent.OnBackClick -> onBackClick()
        }
    }

    private fun onBackClick() = intent {
        postSideEffect(ExampleSideEffect.GoBackEffect)
    }
}
```

### Screen (`ExampleScreen.kt`)

The public entry point. It injects the ViewModel via `koinViewModel()`, collects state and side effects, and delegates rendering to a `Content`:

```kotlin
@Composable
public fun ExampleScreen(viewModel: ExampleViewModel = koinViewModel()) {
    val state by viewModel.collectAsState()
    val backAction = LocalBackAction.current

    viewModel.collectSideEffect { effect ->
        when (effect) {
            ExampleSideEffect.GoBackEffect -> backAction()
            is ExampleSideEffect.ShowError -> { /* show error */ }
        }
    }

    ExampleContent(state = state, onIntent = viewModel::handleIntent)
}
```

### Content (`ExampleContent.kt`)

An `internal` composable that renders state and forwards intents. The About feature further splits this into `AboutSuccessContent` and `AboutShimmerContent` for loaded/loading states — follow that split when a screen has a loading state.

```kotlin
@Composable
internal fun ExampleContent(
    state: ExampleState,
    onIntent: (ExampleIntent) -> Unit,
) {
    // build UI from presentation-core-ui atoms/molecules/organisms
}
```

## 3. Register the Koin module

Under `.../example/di/PresentationFeatureExampleModule.kt`, register the ViewModel:

```kotlin
public val presentationFeatureExampleModule: Module = module {
    viewModelOf(::ExampleViewModel)
}
```

Then add it to the app graph in `initKoin()`
(`compose-application/src/commonMain/kotlin/dev/prompt/knob/io/source/di/InitKoin.kt`), alongside the other feature modules:

```kotlin
modules(
    // ...data + domain modules...
    presentationFeatureAboutModule,
    presentationFeatureExampleModule, // <- add here
    // ...other feature modules...
)
```

## 4. Add a Destination

Add your screen to the sealed `Destination` in
`presentation-core-navigation-api/src/commonMain/kotlin/presentation/core/navigation/api/source/destination/Destination.kt`.
Use a `data object` for a parameterless screen, or a `data class` if it carries arguments:

```kotlin
@Serializable
public sealed class Destination : NavKey {
    // ...existing destinations...
    public data object Example : Destination()
}
```

Because destinations are persisted across process death, also register the new subtype in `navSavedStateConfiguration`
(`presentation-core-navigation-impl/.../source/serialization/NavSavedStateConfig.kt`), following the existing entries.

## 5. Wire it into NavigationHost

In `presentation-core-navigation-impl/.../source/host/NavigationHost.kt`, map the destination to your screen inside the `entryProvider { }` block:

```kotlin
entry<Destination.Example> {
    ExampleScreen()
}
```

Navigate to it from another screen using the `AppNavigator` from `LocalAppNavigator`:

```kotlin
val navigator = LocalAppNavigator.current
navigator.navigate(Destination.Example)
```

`AppNavigatorImpl` supports navigation options `Default`, `SingleTop`, and `ClearTask`; `popBackStack()` handles back navigation (and always keeps at least one entry).

## Checklist

- [ ] Module added to `settings.gradle.kts`
- [ ] `build.gradle.kts` applies the `feature` (and `di`) convention plugins
- [ ] Contract, ViewModel, Screen, Content written following the Orbit MVI structure
- [ ] Koin module created and added to `initKoin()`
- [ ] `Destination` subtype added and registered in `navSavedStateConfiguration`
- [ ] Screen mapped in `NavigationHost`

## Related pages

- [Patterns](../architecture/patterns.md) — Orbit MVI, API/Impl split, atomic-design UI.
- [Adding a module](add-module.md)
- [Convention plugins](../architecture/convention-plugins.md)
