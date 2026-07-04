package presentation.feature.command.source.list

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.navigation.api.source.destination.Destination
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarDuration
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState

/**
 * Entry-point composable for the command/folder list screen.
 *
 * Shows all commands and folders at [parentId] level.
 * Navigating into a folder pushes [Destination.CommandList] with the folder's id.
 *
 * @param parentId The folder level to display (0 = root).
 * @param viewModel MVI host injected by Koin; override in tests.
 */
@OptIn(OrbitExperimental::class)
@Composable
public fun CommandListScreen(
    parentId: Int = 0,
    isParentRotary: Boolean = false,
    viewModel: CommandListViewModel = koinViewModel(
        key = "command_list_$parentId",
        parameters = { parametersOf(parentId, isParentRotary) },
    ),
) {
    val appNavigator = LocalAppNavigator.current
    val state = viewModel.collectAsState()
    val snackbarHostState = rememberStackedSnackbarHostState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            CommandListSideEffect.NavigateBack -> appNavigator?.popBackStack()
            is CommandListSideEffect.NavigateToFolder ->
                appNavigator?.navigate(
                    Destination.CommandList(
                        parentId = effect.parentId,
                        isParentRotary = effect.isRotary,
                    )
                )
            is CommandListSideEffect.NavigateToForm ->
                appNavigator?.navigate(
                    Destination.CommandForm(
                        commandId = effect.commandId,
                        isFolder = effect.isFolder,
                        parentId = effect.parentId,
                        sessionId = effect.sessionId,
                        sortOrder = effect.sortOrder,
                        isParentRotary = effect.isParentRotary,
                    )
                )
            is CommandListSideEffect.ShowError -> snackbarHostState.showSnackbar(
                title = effect.message,
                duration = StackedSnackbarDuration.Short,
            )
        }
    }

    CommandListContent(
        state = state.value,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )
}
