package presentation.feature.command.source.form

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import presentation.core.navigation.api.core.composition.LocalAppNavigator
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarDuration
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState

/**
 * Entry-point composable for the command / folder create-or-edit form.
 *
 * @param commandId Id of the item to edit, or -1 to create a new item.
 * @param isFolder True when the form targets a folder; false for a leaf command.
 * @param parentId Parent folder id for newly created items.
 * @param viewModel MVI host injected by Koin; override in tests.
 */
@OptIn(OrbitExperimental::class)
@Composable
public fun CommandFormScreen(
    commandId: Int = -1,
    isFolder: Boolean = false,
    parentId: Int = 0,
    sessionId: String = "",
    sortOrder: Int = 0,
    isParentRotary: Boolean = false,
    viewModel: CommandFormViewModel = koinViewModel(
        key = if (sessionId.isNotEmpty()) "command_form_$sessionId" else "command_form_${commandId}_${isFolder}",
        parameters = { parametersOf(commandId, isFolder, parentId, sortOrder, isParentRotary) },
    ),
) {
    val appNavigator = LocalAppNavigator.current
    val state = viewModel.collectAsState()
    val snackbarHostState = rememberStackedSnackbarHostState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            CommandFormSideEffect.NavigateBack -> appNavigator?.popBackStack()
            is CommandFormSideEffect.ShowError -> snackbarHostState.showSnackbar(
                title = effect.message,
                duration = StackedSnackbarDuration.Short,
            )
        }
    }

    CommandFormContent(
        state = state.value,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )
}
