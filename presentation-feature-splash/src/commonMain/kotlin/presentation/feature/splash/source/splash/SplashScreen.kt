package presentation.feature.splash.source.splash

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.navigation.api.source.destination.AppNavigator
import presentation.core.navigation.api.source.destination.Destination

/**
 * Entry-point composable for the splash screen.
 *
 * Observes [SplashViewModel] side effects to determine the initial navigation
 * destination: Onboarding on first launch, Home on subsequent launches.
 *
 * @param viewModel ViewModel instance provided by Koin DI.
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
public fun SplashScreen(viewModel: SplashViewModel = koinViewModel()) {
    val appNavigator = LocalAppNavigator.current
    val state = viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SplashSideEffect.NavigateToOnboarding -> appNavigator?.navigate(
                Destination.Onboarding,
                AppNavigator.NavOptions.ClearTask,
            )
            SplashSideEffect.NavigateToHome -> appNavigator?.navigate(
                Destination.Home,
                AppNavigator.NavOptions.ClearTask,
            )
            is SplashSideEffect.ShowError -> {
                // TODO: show snackbar with effect.messageId
            }
        }
    }

    SplashContent(
        state = state.value,
        onIntent = viewModel::handleIntent,
    )
}
