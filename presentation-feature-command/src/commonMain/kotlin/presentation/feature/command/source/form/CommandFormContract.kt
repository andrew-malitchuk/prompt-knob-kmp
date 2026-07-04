package presentation.feature.command.source.form

import domain.core.source.model.CommandTypeModel

/**
 * UI state for [CommandFormScreen].
 *
 * @property isLoading True while loading existing command data (edit mode only).
 * @property isEditMode True when editing an existing item; false when creating new.
 * @property isFolder True when the form targets a folder; false for a leaf command.
 * @property name Current value of the name field.
 * @property command Current value of the command string field.
 * @property icon Current value of the optional icon field.
 * @property parentId Parent folder id for new items.
 * @property commandType How the command string should be executed on the host platform.
 * @property availableCommandTypeModels Platform-specific list of selectable command types.
 * @property hasNameError True when name validation fails.
 * @property hasCommandError True when command string validation fails.
 */
public data class CommandFormState(
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val isFolder: Boolean = false,
    val isRotary: Boolean = false,
    val isMediaScreen: Boolean = false,
    val isAgentScreen: Boolean = false,
    val sortOrder: Int = 0,
    val isParentRotary: Boolean = false,
    val name: String = "",
    val command: String = "",
    val icon: String = "",
    val parentId: Int = 0,
    val commandType: CommandTypeModel = CommandTypeModel.SYSTEM,
    val availableCommandTypeModels: List<CommandTypeModel> = emptyList(),
    val hasNameError: Boolean = false,
    val hasCommandError: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val showSystemCommandPicker: Boolean = false,
    /** Which field the system-command picker was opened for. */
    val systemCommandTarget: SystemCommandTarget = SystemCommandTarget.MAIN,
    /** CW virtual child command string (rotary leaf only). */
    val cwCommand: String = "",
    /** CCW virtual child command string (rotary leaf only). */
    val ccwCommand: String = "",
    /** Persisted id of the CW virtual child (0 = not yet saved). */
    val cwCmdId: Int = 0,
    /** Persisted id of the CCW virtual child (0 = not yet saved). */
    val ccwCmdId: Int = 0,
    val hasCwCommandError: Boolean = false,
    val hasCcwCommandError: Boolean = false,
    /** Play/Pause virtual child command string (media screen only). */
    val playPauseCommand: String = "",
    /** Previous track virtual child command string (media screen only). */
    val prevCommand: String = "",
    /** Next track virtual child command string (media screen only). */
    val nextCommand: String = "",
    /** Persisted id of the play/pause virtual child (0 = not yet saved). */
    val playPauseCmdId: Int = 0,
    /** Persisted id of the prev virtual child (0 = not yet saved). */
    val prevCmdId: Int = 0,
    /** Persisted id of the next virtual child (0 = not yet saved). */
    val nextCmdId: Int = 0,
)

/** Which command field the system-command picker should write to. */
public enum class SystemCommandTarget { MAIN, CW, CCW, PLAY_PAUSE, PREV, NEXT }

/** One-shot side effects emitted by [CommandFormViewModel]. */
public sealed class CommandFormSideEffect {
    public data object NavigateBack : CommandFormSideEffect()
    public data class ShowError(val message: String) : CommandFormSideEffect()
}

/** User actions dispatched from the UI to [CommandFormViewModel]. */
public sealed class CommandFormIntent {
    public data object OnBackClick : CommandFormIntent()
    public data class OnNameChanged(val value: String) : CommandFormIntent()
    public data class OnCommandChanged(val value: String) : CommandFormIntent()
    public data class OnIconChanged(val value: String) : CommandFormIntent()
    public data class OnCommandTypeModelChanged(val value: CommandTypeModel) : CommandFormIntent()
    public data object OnTestRunClick : CommandFormIntent()
    public data object OnSaveClick : CommandFormIntent()
    public data object OnDeleteClick : CommandFormIntent()
    public data object OnDeleteConfirm : CommandFormIntent()
    public data object OnDeleteDismiss : CommandFormIntent()
    public data class OnIsRotaryChanged(val value: Boolean) : CommandFormIntent()
    public data class OnIsMediaScreenChanged(val value: Boolean) : CommandFormIntent()
    public data class OnIsAgentScreenChanged(val value: Boolean) : CommandFormIntent()
    public data class OnBrowseSystemCommandClick(val target: SystemCommandTarget = SystemCommandTarget.MAIN) : CommandFormIntent()
    public data class OnSystemCommandSelected(val command: String) : CommandFormIntent()
    public data object OnSystemCommandPickerDismiss : CommandFormIntent()
    public data class OnCwCommandChanged(val value: String) : CommandFormIntent()
    public data class OnCcwCommandChanged(val value: String) : CommandFormIntent()
    public data class OnPlayPauseCommandChanged(val value: String) : CommandFormIntent()
    public data class OnPrevCommandChanged(val value: String) : CommandFormIntent()
    public data class OnNextCommandChanged(val value: String) : CommandFormIntent()
}
