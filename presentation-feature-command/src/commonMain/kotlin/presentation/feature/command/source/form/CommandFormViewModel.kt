package presentation.feature.command.source.form

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import domain.core.source.model.CommandNodeModel
import domain.core.source.model.CommandTypeModel
import domain.usecase.api.source.command.availableCommandTypes
import domain.usecase.api.source.usecase.ble.SyncCommandsUseCase
import domain.usecase.api.source.usecase.command.DeleteCommandUseCase
import domain.usecase.api.source.usecase.command.GetCommandByIdUseCase
import domain.usecase.api.source.usecase.command.SaveCommandUseCase
import domain.usecase.api.source.usecase.executor.ExecuteCommandUseCase
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container

/**
 * ViewModel for the Command Form screen (create / edit mode).
 *
 * Loads an existing command when [commandId] != -1 (edit mode), or starts with a blank
 * form (create mode). Handles name/command validation, save (including the three-step
 * rotary-leaf multi-save), test-run, and delete flows. After any mutation, [syncCommands]
 * is invoked to keep the connected BLE device in sync.
 *
 * @param commandId Database id of the command to edit, or -1 to create a new entry.
 * @param isFolder True when the form targets a folder node; false for a leaf command.
 * @param parentId Parent folder id used when inserting a new item.
 * @param initialSortOrder Initial sort order for new commands (determined by the caller for rotary slots).
 * @param isParentRotary True when creating inside a rotary folder.
 * @param getCommandById Use case to load a single command by id.
 * @param saveCommand Use case to persist a [CommandNodeModel].
 * @param deleteCommand Use case to delete a command (and optionally its subtree).
 * @param executeCommand Use case to test-run the current command string immediately on the host platform.
 * @param syncCommands Use case to sync the full command tree to the connected BLE device.
 *
 * @see CommandFormScreen
 * @see CommandFormContract
 */
@OrbitExperimental
public class CommandFormViewModel(
    private val commandId: Int,
    private val isFolder: Boolean,
    private val parentId: Int,
    private val initialSortOrder: Int = 0,
    private val isParentRotary: Boolean = false,
    private val getCommandById: GetCommandByIdUseCase,
    private val saveCommand: SaveCommandUseCase,
    private val deleteCommand: DeleteCommandUseCase,
    private val executeCommand: ExecuteCommandUseCase,
    private val syncCommands: SyncCommandsUseCase,
) : ContainerHost<CommandFormState, CommandFormSideEffect>, ViewModel() {

    private val log = Logger.withTag("CommandFormViewModel")

    override val container: Container<CommandFormState, CommandFormSideEffect> =
        container(
            CommandFormState(
                isEditMode = commandId != -1,
                isFolder = isFolder,
                parentId = parentId,
                sortOrder = initialSortOrder,
                isParentRotary = isParentRotary,
                availableCommandTypeModels = availableCommandTypes(),
            )
        ) {
            if (commandId != -1) loadExisting()
        }

    public fun handleIntent(intent: CommandFormIntent) {
        when (intent) {
            CommandFormIntent.OnBackClick -> onBackClick()
            is CommandFormIntent.OnNameChanged -> onNameChanged(intent.value)
            is CommandFormIntent.OnCommandChanged -> onCommandChanged(intent.value)
            is CommandFormIntent.OnIconChanged -> onIconChanged(intent.value)
            is CommandFormIntent.OnCommandTypeModelChanged -> onCommandTypeModelChanged(intent.value)
            CommandFormIntent.OnTestRunClick -> onTestRunClick()
            CommandFormIntent.OnSaveClick -> onSaveClick()
            CommandFormIntent.OnDeleteClick -> onDeleteClick()
            CommandFormIntent.OnDeleteConfirm -> onDeleteConfirm()
            CommandFormIntent.OnDeleteDismiss -> onDeleteDismiss()
            is CommandFormIntent.OnIsRotaryChanged -> onIsRotaryChanged(intent.value)
            is CommandFormIntent.OnIsMediaScreenChanged -> onIsMediaScreenChanged(intent.value)
            is CommandFormIntent.OnIsAgentScreenChanged -> onIsAgentScreenChanged(intent.value)
            is CommandFormIntent.OnBrowseSystemCommandClick -> onBrowseSystemCommandClick(intent.target)
            is CommandFormIntent.OnSystemCommandSelected -> onSystemCommandSelected(intent.command)
            CommandFormIntent.OnSystemCommandPickerDismiss -> onSystemCommandPickerDismiss()
            is CommandFormIntent.OnCwCommandChanged -> onCwCommandChanged(intent.value)
            is CommandFormIntent.OnCcwCommandChanged -> onCcwCommandChanged(intent.value)
            is CommandFormIntent.OnPlayPauseCommandChanged -> onPlayPauseCommandChanged(intent.value)
            is CommandFormIntent.OnPrevCommandChanged -> onPrevCommandChanged(intent.value)
            is CommandFormIntent.OnNextCommandChanged -> onNextCommandChanged(intent.value)
        }
    }


    private fun loadExisting() = intent {
        reduce { state.copy(isLoading = true) }
        getCommandById(commandId)
            .onSuccess { node ->
                var cwCommand = ""
                var ccwCommand = ""
                var playPauseCommand = ""
                var prevCommand = ""
                var nextCommand = ""
                if (node.isRotary && !node.isFolder) {
                    if (node.cwCmdId != 0) {
                        getCommandById(node.cwCmdId).onSuccess { cwCommand = it.command }
                    }
                    if (node.ccwCmdId != 0) {
                        getCommandById(node.ccwCmdId).onSuccess { ccwCommand = it.command }
                    }
                }
                if (node.isMediaScreen && !node.isFolder) {
                    if (node.cwCmdId != 0) {
                        getCommandById(node.cwCmdId).onSuccess { cwCommand = it.command }
                    }
                    if (node.ccwCmdId != 0) {
                        getCommandById(node.ccwCmdId).onSuccess { ccwCommand = it.command }
                    }
                    if (node.playPauseCmdId != 0) {
                        getCommandById(node.playPauseCmdId).onSuccess { playPauseCommand = it.command }
                    }
                    if (node.prevCmdId != 0) {
                        getCommandById(node.prevCmdId).onSuccess { prevCommand = it.command }
                    }
                    if (node.nextCmdId != 0) {
                        getCommandById(node.nextCmdId).onSuccess { nextCommand = it.command }
                    }
                }
                reduce {
                    state.copy(
                        isLoading = false,
                        isFolder = node.isFolder,
                        isRotary = node.isRotary,
                        isMediaScreen = node.isMediaScreen,
                        isAgentScreen = node.isAgentScreen,
                        sortOrder = node.sortOrder,
                        name = node.label,
                        command = node.command,
                        icon = node.icon ?: "",
                        commandType = node.commandType,
                        cwCmdId = node.cwCmdId,
                        ccwCmdId = node.ccwCmdId,
                        cwCommand = cwCommand,
                        ccwCommand = ccwCommand,
                        playPauseCmdId = node.playPauseCmdId,
                        prevCmdId = node.prevCmdId,
                        nextCmdId = node.nextCmdId,
                        playPauseCommand = playPauseCommand,
                        prevCommand = prevCommand,
                        nextCommand = nextCommand,
                    )
                }
            }
            .onFailure { t ->
                reduce { state.copy(isLoading = false) }
                postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to load"))
            }
    }


    private fun onBackClick() = intent {
        postSideEffect(CommandFormSideEffect.NavigateBack)
    }

    private fun onNameChanged(value: String) = intent {
        reduce { state.copy(name = value, hasNameError = false) }
    }

    private fun onCommandChanged(value: String) = intent {
        reduce { state.copy(command = value, hasCommandError = false) }
    }

    private fun onIconChanged(value: String) = intent {
        reduce { state.copy(icon = value) }
    }

    private fun onCommandTypeModelChanged(value: CommandTypeModel) = intent {
        reduce { state.copy(commandType = value) }
    }

    private fun onTestRunClick() = intent {
        if (state.command.isBlank()) return@intent
        val node = CommandNodeModel(
            id = 0,
            parentId = state.parentId,
            label = state.name.ifBlank { "TEST" },
            isFolder = false,
            sortOrder = 0,
            command = state.command.trim(),
            icon = null,
            commandType = state.commandType,
        )
        executeCommand(node)
    }

    private fun onSaveClick() = intent {
        val hasNameError = state.name.isBlank()
        val hasCommandError = !state.isFolder && !state.isRotary && !state.isMediaScreen && !state.isAgentScreen && state.command.isBlank()
        val hasCwCommandError = state.isRotary && !state.isFolder && state.cwCommand.isBlank()
        val hasCcwCommandError = state.isRotary && !state.isFolder && state.ccwCommand.isBlank()

        if (hasNameError || hasCommandError || hasCwCommandError || hasCcwCommandError) {
            reduce {
                state.copy(
                    hasNameError = hasNameError,
                    hasCommandError = hasCommandError,
                    hasCwCommandError = hasCwCommandError,
                    hasCcwCommandError = hasCcwCommandError,
                )
            }
            return@intent
        }

        when {
            state.isAgentScreen && !state.isFolder -> saveAgentScreenLeaf()
            state.isMediaScreen && !state.isFolder -> saveMediaScreenLeaf()
            state.isRotary && !state.isFolder -> saveRotaryLeaf()
            else -> {
                val node = CommandNodeModel(
                    id = if (commandId != -1) commandId else 0,
                    parentId = state.parentId,
                    label = state.name.trim(),
                    isFolder = state.isFolder,
                    isRotary = false,
                    sortOrder = state.sortOrder,
                    command = state.command.trim(),
                    icon = state.icon.trim().ifBlank { null },
                    commandType = state.commandType,
                )
                saveCommand(node)
                    .onSuccess {
                        postSideEffect(CommandFormSideEffect.NavigateBack)
                        syncCommands().onFailure { t -> log.w { "SYNC failed after save — ${t.message}" } }
                    }
                    .onFailure { t ->
                        postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save"))
                    }
            }
        }
    }

    /**
     * Saves a rotary leaf with its two virtual CW/CCW children.
     *
     * New rotary leaf (commandId == -1):
     *   1. Save main (id=0) → mainId
     *   2. Save CW virtual (parentId=mainId, sortOrder=0) → cwId
     *   3. Save CCW virtual (parentId=mainId, sortOrder=1) → ccwId
     *   4. Update main (id=mainId, cwCmdId=cwId, ccwCmdId=ccwId)
     *
     * Editing existing rotary leaf (commandId != -1):
     *   1. Update CW virtual leaf
     *   2. Update CCW virtual leaf
     *   3. Update main
     */
    private fun saveRotaryLeaf() = intent {
        val s = state
        val isNew = commandId == -1

        val mainNode = CommandNodeModel(
            id = if (isNew) 0 else commandId,
            parentId = s.parentId,
            label = s.name.trim(),
            isFolder = false,
            isRotary = true,
            sortOrder = s.sortOrder,
            command = "",
            icon = s.icon.trim().ifBlank { null },
            commandType = s.commandType,
            cwCmdId = if (isNew) 0 else s.cwCmdId,
            ccwCmdId = if (isNew) 0 else s.ccwCmdId,
        )

        if (isNew) {
            // Step 1: save main to get its generated id
            val mainId = saveCommand(mainNode).getOrElse { t ->
                postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save"))
                return@intent
            }
            // Step 2: save CW virtual child
            val cwId = saveCommand(
                CommandNodeModel(
                    id = 0,
                    parentId = mainId,
                    label = "${s.name.trim()} CW",
                    isFolder = false,
                    isRotary = false,
                    sortOrder = 0,
                    command = s.cwCommand.trim(),
                    commandType = s.commandType,
                )
            ).getOrElse { t ->
                postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save CW"))
                return@intent
            }
            // Step 3: save CCW virtual child
            val ccwId = saveCommand(
                CommandNodeModel(
                    id = 0,
                    parentId = mainId,
                    label = "${s.name.trim()} CCW",
                    isFolder = false,
                    isRotary = false,
                    sortOrder = 1,
                    command = s.ccwCommand.trim(),
                    commandType = s.commandType,
                )
            ).getOrElse { t ->
                postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save CCW"))
                return@intent
            }
            // Step 4: update main with cwCmdId/ccwCmdId
            saveCommand(mainNode.copy(id = mainId, cwCmdId = cwId, ccwCmdId = ccwId))
                .onFailure { t ->
                    postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to link rotary"))
                    return@intent
                }
        } else {
            // Update CW virtual leaf
            if (s.cwCmdId != 0) {
                saveCommand(
                    CommandNodeModel(
                        id = s.cwCmdId,
                        parentId = commandId,
                        label = "${s.name.trim()} CW",
                        isFolder = false,
                        isRotary = false,
                        sortOrder = 0,
                        command = s.cwCommand.trim(),
                        commandType = s.commandType,
                    )
                ).onFailure { t ->
                    postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to update CW"))
                    return@intent
                }
            }
            // Update CCW virtual leaf
            if (s.ccwCmdId != 0) {
                saveCommand(
                    CommandNodeModel(
                        id = s.ccwCmdId,
                        parentId = commandId,
                        label = "${s.name.trim()} CCW",
                        isFolder = false,
                        isRotary = false,
                        sortOrder = 1,
                        command = s.ccwCommand.trim(),
                        commandType = s.commandType,
                    )
                ).onFailure { t ->
                    postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to update CCW"))
                    return@intent
                }
            }
            // Update main
            saveCommand(mainNode)
                .onFailure { t ->
                    postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save"))
                    return@intent
                }
        }

        postSideEffect(CommandFormSideEffect.NavigateBack)
        syncCommands().onFailure { t -> log.w { "SYNC failed after save — ${t.message}" } }
    }

    /**
     * Saves a media screen leaf with its five virtual children (vol up, vol down,
     * play/pause, prev, next).
     *
     * New media screen leaf (commandId == -1):
     *   1. Save main (id=0) → mainId
     *   2. Save 5 virtual children (parentId=mainId, sortOrder=0..4) → 5 ids
     *   3. Update main with all 5 ids
     *
     * Editing existing media screen leaf (commandId != -1):
     *   1. Update 5 virtual children
     *   2. Update main
     */
    private fun saveMediaScreenLeaf() = intent {
        val s = state
        val isNew = commandId == -1

        val mainNode = CommandNodeModel(
            id = if (isNew) 0 else commandId,
            parentId = s.parentId,
            label = s.name.trim(),
            isFolder = false,
            isMediaScreen = true,
            sortOrder = s.sortOrder,
            command = "",
            icon = s.icon.trim().ifBlank { null },
            commandType = CommandTypeModel.SYSTEM,
            cwCmdId = if (isNew) 0 else s.cwCmdId,
            ccwCmdId = if (isNew) 0 else s.ccwCmdId,
            playPauseCmdId = if (isNew) 0 else s.playPauseCmdId,
            prevCmdId = if (isNew) 0 else s.prevCmdId,
            nextCmdId = if (isNew) 0 else s.nextCmdId,
        )

        if (isNew) {
            val mainId = saveCommand(mainNode).getOrElse { t ->
                postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save"))
                return@intent
            }
            val cwId = saveCommand(
                CommandNodeModel(id = 0, parentId = mainId, label = "${s.name.trim()} VOL+", isFolder = false, sortOrder = 0, command = "VOLUME_UP", commandType = CommandTypeModel.SYSTEM)
            ).getOrElse { t ->
                postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save VOL+"))
                return@intent
            }
            val ccwId = saveCommand(
                CommandNodeModel(id = 0, parentId = mainId, label = "${s.name.trim()} VOL-", isFolder = false, sortOrder = 1, command = "VOLUME_DOWN", commandType = CommandTypeModel.SYSTEM)
            ).getOrElse { t ->
                postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save VOL-"))
                return@intent
            }
            val playPauseId = saveCommand(
                CommandNodeModel(id = 0, parentId = mainId, label = "${s.name.trim()} PLAY", isFolder = false, sortOrder = 2, command = "PLAY_PAUSE", commandType = CommandTypeModel.SYSTEM)
            ).getOrElse { t ->
                postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save PLAY"))
                return@intent
            }
            val prevId = saveCommand(
                CommandNodeModel(id = 0, parentId = mainId, label = "${s.name.trim()} PREV", isFolder = false, sortOrder = 3, command = "PREV_TRACK", commandType = CommandTypeModel.SYSTEM)
            ).getOrElse { t ->
                postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save PREV"))
                return@intent
            }
            val nextId = saveCommand(
                CommandNodeModel(id = 0, parentId = mainId, label = "${s.name.trim()} NEXT", isFolder = false, sortOrder = 4, command = "NEXT_TRACK", commandType = CommandTypeModel.SYSTEM)
            ).getOrElse { t ->
                postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save NEXT"))
                return@intent
            }
            saveCommand(mainNode.copy(id = mainId, cwCmdId = cwId, ccwCmdId = ccwId, playPauseCmdId = playPauseId, prevCmdId = prevId, nextCmdId = nextId))
                .onFailure { t ->
                    postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to link media screen"))
                    return@intent
                }
        } else {
            if (s.cwCmdId != 0) {
                saveCommand(CommandNodeModel(id = s.cwCmdId, parentId = commandId, label = "${s.name.trim()} VOL+", isFolder = false, sortOrder = 0, command = "VOLUME_UP", commandType = CommandTypeModel.SYSTEM))
                    .onFailure { t ->
                        postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to update VOL+"))
                        return@intent
                    }
            }
            if (s.ccwCmdId != 0) {
                saveCommand(CommandNodeModel(id = s.ccwCmdId, parentId = commandId, label = "${s.name.trim()} VOL-", isFolder = false, sortOrder = 1, command = "VOLUME_DOWN", commandType = CommandTypeModel.SYSTEM))
                    .onFailure { t ->
                        postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to update VOL-"))
                        return@intent
                    }
            }
            if (s.playPauseCmdId != 0) {
                saveCommand(CommandNodeModel(id = s.playPauseCmdId, parentId = commandId, label = "${s.name.trim()} PLAY", isFolder = false, sortOrder = 2, command = "PLAY_PAUSE", commandType = CommandTypeModel.SYSTEM))
                    .onFailure { t ->
                        postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to update PLAY"))
                        return@intent
                    }
            }
            if (s.prevCmdId != 0) {
                saveCommand(CommandNodeModel(id = s.prevCmdId, parentId = commandId, label = "${s.name.trim()} PREV", isFolder = false, sortOrder = 3, command = "PREV_TRACK", commandType = CommandTypeModel.SYSTEM))
                    .onFailure { t ->
                        postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to update PREV"))
                        return@intent
                    }
            }
            if (s.nextCmdId != 0) {
                saveCommand(CommandNodeModel(id = s.nextCmdId, parentId = commandId, label = "${s.name.trim()} NEXT", isFolder = false, sortOrder = 4, command = "NEXT_TRACK", commandType = CommandTypeModel.SYSTEM))
                    .onFailure { t ->
                        postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to update NEXT"))
                        return@intent
                    }
            }
            saveCommand(mainNode)
                .onFailure { t ->
                    postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save"))
                    return@intent
                }
        }

        postSideEffect(CommandFormSideEffect.NavigateBack)
        syncCommands().onFailure { t -> log.w { "SYNC failed after save — ${t.message}" } }
    }

    private fun onDeleteClick() = intent {
        if (commandId == -1) return@intent
        reduce { state.copy(showDeleteConfirm = true) }
    }

    private fun onDeleteConfirm() = intent {
        reduce { state.copy(showDeleteConfirm = false) }
        // For rotary leaves, also delete virtual CW/CCW children before deleting the main node.
        if (state.isRotary && !state.isFolder) {
            if (state.cwCmdId != 0) deleteCommand(state.cwCmdId, false)
            if (state.ccwCmdId != 0) deleteCommand(state.ccwCmdId, false)
        }
        // For media screen leaves, delete all 5 virtual children before deleting the main node.
        if (state.isMediaScreen && !state.isFolder) {
            if (state.cwCmdId != 0) deleteCommand(state.cwCmdId, false)
            if (state.ccwCmdId != 0) deleteCommand(state.ccwCmdId, false)
            if (state.playPauseCmdId != 0) deleteCommand(state.playPauseCmdId, false)
            if (state.prevCmdId != 0) deleteCommand(state.prevCmdId, false)
            if (state.nextCmdId != 0) deleteCommand(state.nextCmdId, false)
        }
        deleteCommand(commandId, state.isFolder)
            .onSuccess {
                postSideEffect(CommandFormSideEffect.NavigateBack)
                syncCommands().onFailure { t ->
                    log.w { "SYNC failed after delete — ${t.message}" }
                }
            }
            .onFailure { t ->
                postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to delete"))
            }
    }

    private fun onDeleteDismiss() = intent {
        reduce { state.copy(showDeleteConfirm = false) }
    }

    private fun onIsRotaryChanged(value: Boolean) = intent {
        reduce {
            state.copy(
                isRotary = value,
                isMediaScreen = if (value) false else state.isMediaScreen,
                isAgentScreen = if (value) false else state.isAgentScreen,
            )
        }
    }

    private fun onIsMediaScreenChanged(value: Boolean) = intent {
        reduce {
            state.copy(
                isMediaScreen = value,
                isRotary = if (value) false else state.isRotary,
                isAgentScreen = if (value) false else state.isAgentScreen,
            )
        }
    }

    private fun onIsAgentScreenChanged(value: Boolean) = intent {
        reduce {
            state.copy(
                isAgentScreen = value,
                isRotary = if (value) false else state.isRotary,
                isMediaScreen = if (value) false else state.isMediaScreen,
            )
        }
    }

    private fun saveAgentScreenLeaf() = intent {
        val s = state
        val node = CommandNodeModel(
            id = if (commandId != -1) commandId else 0,
            parentId = s.parentId,
            label = s.name.trim(),
            isFolder = false,
            isAgentScreen = true,
            sortOrder = s.sortOrder,
            command = "",
            icon = s.icon.trim().ifBlank { null },
            commandType = s.commandType,
        )
        saveCommand(node)
            .onSuccess {
                postSideEffect(CommandFormSideEffect.NavigateBack)
                syncCommands().onFailure { t -> log.w { "SYNC failed after save — ${t.message}" } }
            }
            .onFailure { t ->
                postSideEffect(CommandFormSideEffect.ShowError(t.message ?: "Failed to save"))
            }
    }

    private fun onBrowseSystemCommandClick(target: SystemCommandTarget) = intent {
        reduce { state.copy(showSystemCommandPicker = true, systemCommandTarget = target) }
    }

    private fun onSystemCommandSelected(command: String) = intent {
        reduce {
            when (state.systemCommandTarget) {
                SystemCommandTarget.CW -> state.copy(cwCommand = command, showSystemCommandPicker = false, hasCwCommandError = false)
                SystemCommandTarget.CCW -> state.copy(ccwCommand = command, showSystemCommandPicker = false, hasCcwCommandError = false)
                SystemCommandTarget.MAIN -> {
                    if (command == "MEDIA_SCREEN") {
                        // Activating media screen mode via the system command picker.
                        state.copy(
                            isMediaScreen = true,
                            isRotary = false,
                            command = "",
                            commandType = CommandTypeModel.SYSTEM,
                            showSystemCommandPicker = false,
                            hasCommandError = false,
                        )
                    } else {
                        state.copy(command = command, showSystemCommandPicker = false, hasCommandError = false)
                    }
                }
                SystemCommandTarget.PLAY_PAUSE -> state.copy(playPauseCommand = command, showSystemCommandPicker = false)
                SystemCommandTarget.PREV -> state.copy(prevCommand = command, showSystemCommandPicker = false)
                SystemCommandTarget.NEXT -> state.copy(nextCommand = command, showSystemCommandPicker = false)
            }
        }
    }

    private fun onSystemCommandPickerDismiss() = intent {
        reduce { state.copy(showSystemCommandPicker = false) }
    }

    private fun onCwCommandChanged(value: String) = intent {
        reduce { state.copy(cwCommand = value, hasCwCommandError = false) }
    }

    private fun onCcwCommandChanged(value: String) = intent {
        reduce { state.copy(ccwCommand = value, hasCcwCommandError = false) }
    }

    private fun onPlayPauseCommandChanged(value: String) = intent {
        reduce { state.copy(playPauseCommand = value) }
    }

    private fun onPrevCommandChanged(value: String) = intent {
        reduce { state.copy(prevCommand = value) }
    }

    private fun onNextCommandChanged(value: String) = intent {
        reduce { state.copy(nextCommand = value) }
    }
}
