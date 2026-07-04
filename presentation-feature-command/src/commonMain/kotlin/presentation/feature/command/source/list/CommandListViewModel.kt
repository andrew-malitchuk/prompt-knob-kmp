package presentation.feature.command.source.list

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import domain.core.source.model.CommandNodeModel
import kotlin.random.Random
import domain.usecase.api.source.usecase.ble.SyncCommandsUseCase
import domain.usecase.api.source.usecase.command.DeleteCommandUseCase
import domain.usecase.api.source.usecase.command.ObserveCommandsUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container

/**
 * ViewModel for the Command List screen.
 *
 * Loads and observes commands at [parentId] from the local database. On init,
 * [observeItems] subscribes to [ObserveCommandsUseCase] so the list stays in sync
 * with local mutations and BLE sync results.
 *
 * @param parentId Database id of the folder whose children are displayed (0 = root).
 * @param isParentRotary True when [parentId] belongs to a rotary folder — affects add-command behaviour.
 * @param observeCommands Use case for observing live [CommandNodeModel] changes at [parentId].
 * @param deleteCommand Use case for deleting a command or folder subtree.
 * @param syncCommands Use case for syncing the command tree to the connected BLE device after mutations.
 *
 * @see CommandListScreen
 * @see CommandListContract
 */
@OrbitExperimental
public class CommandListViewModel(
    private val parentId: Int,
    private val isParentRotary: Boolean = false,
    private val observeCommands: ObserveCommandsUseCase,
    private val deleteCommand: DeleteCommandUseCase,
    private val syncCommands: SyncCommandsUseCase,
) : ContainerHost<CommandListState, CommandListSideEffect>, ViewModel() {

    private val log = Logger.withTag("CommandListViewModel")

    override val container: Container<CommandListState, CommandListSideEffect> =
        container(CommandListState(parentId = parentId, isParentRotary = isParentRotary)) {
            observeItems()
        }

    public fun handleIntent(intent: CommandListIntent) {
        when (intent) {
            CommandListIntent.OnBackClick -> onBackClick()
            is CommandListIntent.OnItemClick -> onItemClick(intent.id, intent.isFolder)
            CommandListIntent.OnAddCommandClick -> onAddCommandClick()
            CommandListIntent.OnAddFolderClick -> onAddFolderClick()
            is CommandListIntent.OnDeleteClick -> onDeleteClick(intent.id, intent.label, intent.isFolder)
            CommandListIntent.OnDeleteFolderClick -> onDeleteFolderClick()
            CommandListIntent.OnConfirmDelete -> onConfirmDelete()
            CommandListIntent.OnDismissDelete -> onDismissDelete()
        }
    }


    private fun observeItems() = intent {
        observeCommands(parentId)
            .onEach { nodes ->
                reduce {
                    state.copy(
                        isLoading = false,
                        items = nodes.map { it.toUiModel() },
                    )
                }
            }
            .catch { t ->
                reduce { state.copy(isLoading = false) }
                postSideEffect(CommandListSideEffect.ShowError(t.message ?: "Failed to load commands"))
            }
            .collect {}
    }


    private fun onBackClick() = intent {
        postSideEffect(CommandListSideEffect.NavigateBack)
    }

    private fun onItemClick(id: Int, isFolder: Boolean) = intent {
        if (isFolder) {
            val isRotary = state.items.firstOrNull { it.id == id }?.isRotary ?: false
            postSideEffect(CommandListSideEffect.NavigateToFolder(id, isRotary))
        } else {
            postSideEffect(CommandListSideEffect.NavigateToForm(commandId = id, isFolder = false, parentId = parentId))
        }
    }

    private fun onAddCommandClick() = intent {
        // Inside a rotary folder: auto-assign sortOrder based on existing leaf count
        // (0 = first/CW, 1 = second/CCW). Caps at 1 so we don't exceed the firmware limit.
        val leafCount = state.items.count { !it.isFolder }
        val sortOrder = if (state.isParentRotary) leafCount.coerceAtMost(1) else 0
        postSideEffect(
            CommandListSideEffect.NavigateToForm(
                commandId = -1,
                isFolder = false,
                parentId = parentId,
                sessionId = Random.nextLong().toString(),
                sortOrder = sortOrder,
                isParentRotary = state.isParentRotary,
            )
        )
    }

    private fun onAddFolderClick() = intent {
        postSideEffect(CommandListSideEffect.NavigateToForm(commandId = -1, isFolder = true, parentId = parentId, sessionId = Random.nextLong().toString()))
    }

    private fun onDeleteClick(id: Int, label: String, isFolder: Boolean) = intent {
        reduce { state.copy(pendingDeleteId = id, pendingDeleteLabel = label) }
    }

    private fun onDeleteFolderClick() = intent {
        // Confirm deletion of the currently open folder (parentId).
        reduce { state.copy(pendingDeleteId = parentId, pendingDeleteLabel = state.parentLabel) }
    }

    private fun onConfirmDelete() = intent {
        val id = state.pendingDeleteId ?: return@intent
        val isDeletingCurrentFolder = id == parentId
        val isFolder = isDeletingCurrentFolder || (state.items.firstOrNull { it.id == id }?.isFolder ?: false)
        reduce { state.copy(pendingDeleteId = null, pendingDeleteLabel = null) }
        deleteCommand(id, isFolder)
            .onSuccess {
                if (isDeletingCurrentFolder) {
                    postSideEffect(CommandListSideEffect.NavigateBack)
                }
                syncCommands().onFailure { t ->
                    log.w { "SYNC failed after delete — ${t.message}" }
                }
            }
            .onFailure { t ->
                postSideEffect(CommandListSideEffect.ShowError(t.message ?: "Failed to delete"))
            }
    }

    private fun onDismissDelete() = intent {
        reduce { state.copy(pendingDeleteId = null, pendingDeleteLabel = null) }
    }


    private fun CommandNodeModel.toUiModel(): CommandItemUiModel = CommandItemUiModel(
        id = id,
        label = label,
        isFolder = isFolder,
        isRotary = isRotary,
        isMediaScreen = isMediaScreen,
        isAgentScreen = isAgentScreen,
        icon = icon,
        command = command,
    )
}
