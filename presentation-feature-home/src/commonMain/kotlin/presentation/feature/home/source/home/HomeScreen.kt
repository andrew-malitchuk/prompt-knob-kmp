package presentation.feature.home.source.home

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.navigation.api.source.destination.Destination

/**
 * Entry-point composable for the Home screen.
 *
 * Injects [HomeViewModel] via Koin, observes MVI state and side effects,
 * and delegates rendering to [HomeContent].
 *
 * @param viewModel ViewModel instance provided by Koin DI.
 *
 * @see HomeContent
 * @see HomeViewModel
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
public fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val appNavigator = LocalAppNavigator.current
    val state = viewModel.collectAsState()

    // Handle one-shot navigation side effects.
    viewModel.collectSideEffect { effect ->
        when (effect) {
            HomeSideEffect.NavigateToSettings -> appNavigator?.navigate(Destination.Settings)
            HomeSideEffect.NavigateToStyleguide -> appNavigator?.navigate(Destination.Styleguide)
            is HomeSideEffect.ShowError -> {
                // TODO: show snackbar with effect.messageId
            }
        }
    }

    HomeContent(
        state = state.value,
        onIntent = viewModel::handleIntent,
    )
}
