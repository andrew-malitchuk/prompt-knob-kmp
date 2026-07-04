package presentation.feature.preset.source.list

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarDuration
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState

/**
 * Entry point composable for the preset management screen.
 *
 * Collects [PresetListState] from [PresetListViewModel], handles navigation back on
 * [PresetListSideEffect.NavigateBack], shows error snackbars on [PresetListSideEffect.ShowError],
 * and triggers the system share sheet on [PresetListSideEffect.ShareExport].
 *
 * @param viewModel Orbit MVI ViewModel injected via Koin.
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@OptIn(OrbitExperimental::class)
@Composable
public fun PresetListScreen(
    viewModel: PresetListViewModel = koinViewModel(),
) {
    val appNavigator = LocalAppNavigator.current
    val state = viewModel.collectAsState()
    val snackbarHostState = rememberStackedSnackbarHostState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            PresetListSideEffect.NavigateBack -> appNavigator?.popBackStack()
            is PresetListSideEffect.ShowError -> snackbarHostState.showSnackbar(
                title = effect.message,
                duration = StackedSnackbarDuration.Short,
            )
            is PresetListSideEffect.ShareExport -> {
                // Platform share sheet handled by caller via side-effect in NavigationHost
            }
        }
    }

    PresetListContent(
        state = state.value,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )
}
