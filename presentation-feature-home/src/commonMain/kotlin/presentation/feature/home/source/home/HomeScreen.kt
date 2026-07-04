package presentation.feature.home.source.home

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
import presentation.feature.home.source.home.content.HomeContent
import prompt_knob_kmp.presentation_core_localisation.generated.resources.Res
import prompt_knob_kmp.presentation_core_localisation.generated.resources.snackbar_error

/**
 * Entry-point composable for the Home screen.
 *
 * Injects [HomeViewModel] via Koin, observes MVI state and side effects,
 * and delegates rendering to [presentation.feature.home.source.home.content.HomeContent].
 *
 * @param viewModel ViewModel instance provided by Koin DI.
 *
 * @see presentation.feature.home.source.home.content.HomeContent
 * @see HomeViewModel
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
public fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val appNavigator = LocalAppNavigator.current
    val state = viewModel.collectAsState()
    val snackbarHostState = rememberStackedSnackbarHostState()
    val errorTitle = stringResource(Res.string.snackbar_error)

    // Handle one-shot navigation side effects.
    viewModel.collectSideEffect { effect ->
        when (effect) {
            HomeSideEffect.NavigateToSettings -> appNavigator?.navigate(Destination.Settings)
            HomeSideEffect.NavigateToStyleguide -> appNavigator?.navigate(Destination.Styleguide)
            // ClearTask removes Home from the back stack so the user cannot navigate back to it.
            HomeSideEffect.NavigateToDevices -> appNavigator?.navigate(
                Destination.Devices,
                AppNavigator.NavOptions.ClearTask,
            )
            // Device was already connected: add Devices below Device in the back stack so
            // the user can navigate back to the scan screen after a disconnect.
            HomeSideEffect.NavigateToDevice -> {
                appNavigator?.navigate(Destination.Devices, AppNavigator.NavOptions.ClearTask)
                appNavigator?.navigate(Destination.Device)
            }
            HomeSideEffect.NavigateToPresets -> appNavigator?.navigate(Destination.Presets)
            is HomeSideEffect.ShowError -> snackbarHostState.showSnackbar(
                title = errorTitle,
                duration = StackedSnackbarDuration.Short,
            )
        }
    }

    HomeContent(
        state = state.value,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )
}
