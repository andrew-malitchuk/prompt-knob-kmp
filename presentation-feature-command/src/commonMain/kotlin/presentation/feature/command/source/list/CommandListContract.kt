package presentation.feature.command.source.list

/**
 * UI model for a single row in the command/folder list.
 *
 * @property id Database id of the node.
 * @property label Display name.
 * @property isFolder True if this node is a folder.
 * @property icon Optional icon identifier.
 * @property command The command string (empty for folders).
 */
public data class CommandItemUiModel(
    val id: Int,
    val label: String,
    val isFolder: Boolean,
    val isRotary: Boolean,
    val isMediaScreen: Boolean = false,
    val isAgentScreen: Boolean = false,
    val icon: String?,
    val command: String,
)

/**
 * UI state for [CommandListScreen].
 *
 * @property isLoading True while the initial data load is in progress.
 * @property parentId The currently displayed folder level (0 = root).
 * @property parentLabel Label of the parent folder, or empty string at root.
 * @property items Ordered list of commands and folders at [parentId].
 * @property pendingDeleteId Id of the item awaiting deletion confirmation, or null.
 * @property pendingDeleteLabel Label of the item awaiting deletion confirmation, or null.
 */
public data class CommandListState(
    val isLoading: Boolean = true,
    val parentId: Int = 0,
    val isParentRotary: Boolean = false,
    val parentLabel: String = "",
    val items: List<CommandItemUiModel> = emptyList(),
    val pendingDeleteId: Int? = null,
    val pendingDeleteLabel: String? = null,
)

/** One-shot side effects emitted by [CommandListViewModel]. */
public sealed class CommandListSideEffect {
    public data object NavigateBack : CommandListSideEffect()
    public data class NavigateToFolder(val parentId: Int, val isRotary: Boolean = false) : CommandListSideEffect()
    public data class NavigateToForm(
        val commandId: Int,
        val isFolder: Boolean,
        val parentId: Int,
        val sessionId: String = "",
        val sortOrder: Int = 0,
        val isParentRotary: Boolean = false,
    ) : CommandListSideEffect()
    public data class ShowError(val message: String) : CommandListSideEffect()
}

/** User actions dispatched from the UI to [CommandListViewModel]. */
public sealed class CommandListIntent {
    public data object OnBackClick : CommandListIntent()
    public data class OnItemClick(val id: Int, val isFolder: Boolean) : CommandListIntent()
    public data object OnAddCommandClick : CommandListIntent()
    public data object OnAddFolderClick : CommandListIntent()
    public data class OnDeleteClick(val id: Int, val label: String, val isFolder: Boolean) : CommandListIntent()
    public data object OnDeleteFolderClick : CommandListIntent()
    public data object OnConfirmDelete : CommandListIntent()
    public data object OnDismissDelete : CommandListIntent()
}
