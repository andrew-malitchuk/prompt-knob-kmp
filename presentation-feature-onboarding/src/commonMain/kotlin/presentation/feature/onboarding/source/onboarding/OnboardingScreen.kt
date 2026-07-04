package presentation.feature.onboarding.source.onboarding

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.navigation.api.source.destination.AppNavigator
import presentation.core.navigation.api.source.destination.Destination
import org.jetbrains.compose.resources.stringResource
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarDuration
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import presentation.feature.onboarding.source.onboarding.content.OnboardingContent
import prompt_knob_kmp.presentation_core_localisation.generated.resources.Res
import prompt_knob_kmp.presentation_core_localisation.generated.resources.snackbar_error

/**
 * Entry-point composable for the Onboarding feature.
 *
 * Collects Orbit MVI state and side-effects from [OnboardingViewModel],
 * delegates rendering to [presentation.feature.onboarding.source.onboarding.content.OnboardingContent], and handles navigation
 * side-effects via [LocalAppNavigator].
 *
 * @param viewModel Koin-provided [OnboardingViewModel] instance.
 *
 * @see presentation.feature.onboarding.source.onboarding.content.OnboardingContent
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
public fun OnboardingScreen(viewModel: OnboardingViewModel = koinViewModel()) {
    val appNavigator = LocalAppNavigator.current
    val state = viewModel.collectAsState()
    val snackbarHostState = rememberStackedSnackbarHostState()
    val errorTitle = stringResource(Res.string.snackbar_error)

    // Handle one-shot side-effects: navigation and error display
    viewModel.collectSideEffect { effect ->
        when (effect) {
            OnboardingSideEffect.NavigateToHome -> appNavigator?.navigate(
                Destination.Home,
                AppNavigator.NavOptions.ClearTask,
            )
            is OnboardingSideEffect.ShowError -> snackbarHostState.showSnackbar(
                title = errorTitle,
                duration = StackedSnackbarDuration.Short,
            )
        }
    }

    OnboardingContent(
        state = state.value,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )
}
