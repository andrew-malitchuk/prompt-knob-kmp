package presentation.feature.preset.source.list

import domain.core.source.model.PresetModel
import domain.core.source.model.GalleryPreset

/**
 * Presentation-layer projection of a saved preset for display in the preset list.
 *
 * @property id Database primary key used for load/delete/export operations.
 * @property name User-assigned display name.
 * @property updatedAt Epoch milliseconds of last update, used for sorting.
 */
public data class PresetUiModel(
    val id: Int,
    val name: String,
    val updatedAt: Long,
)

/**
 * UI state for the preset list screen.
 *
 * @property isLoading True while the initial preset list is loading.
 * @property gallery Built-in read-only gallery presets.
 * @property presets User-saved presets, ordered by last-updated descending.
 * @property pendingDeleteId Id of the preset queued for deletion confirmation, or null.
 * @property pendingDeleteLabel Display name of the preset pending deletion, or null.
 * @property showSaveDialog True when the save-name input dialog should be shown.
 * @property pendingGalleryPreset Gallery preset selected for load confirmation, or null.
 */
public data class PresetListState(
    val isLoading: Boolean = true,
    val gallery: List<GalleryPreset> = emptyList(),
    val presets: List<PresetUiModel> = emptyList(),
    val pendingDeleteId: Int? = null,
    val pendingDeleteLabel: String? = null,
    val showSaveDialog: Boolean = false,
    val pendingGalleryPreset: GalleryPreset? = null,
)

/** One-shot side effects emitted by [PresetListViewModel]. */
public sealed class PresetListSideEffect {
    /** Signals the screen to pop back to the previous destination. */
    public data object NavigateBack : PresetListSideEffect()
    /** Signals the screen to show a snackbar with [message]. */
    public data class ShowError(val message: String) : PresetListSideEffect()
    /** Signals the screen to open the system share sheet with the exported [json]. */
    public data class ShareExport(val json: String) : PresetListSideEffect()
}

/** User intents dispatched to [PresetListViewModel]. */
public sealed class PresetListIntent {
    public data object OnBackClick : PresetListIntent()
    public data class OnGalleryPresetClick(val preset: GalleryPreset) : PresetListIntent()
    public data object OnConfirmGalleryPreset : PresetListIntent()
    public data object OnDismissGalleryPreset : PresetListIntent()
    public data object OnSaveClick : PresetListIntent()
    /** @param name The name entered by the user in the save dialog. */
    public data class OnSaveConfirm(val name: String) : PresetListIntent()
    public data object OnSaveDismiss : PresetListIntent()
    /** @param id Database id of the preset to load and activate. */
    public data class OnLoadPreset(val id: Int) : PresetListIntent()
    /** @param id Database id of the preset. @param label Name shown in the confirm dialog. */
    public data class OnDeleteClick(val id: Int, val label: String) : PresetListIntent()
    public data object OnConfirmDelete : PresetListIntent()
    public data object OnDismissDelete : PresetListIntent()
    /** @param id Database id of the preset to serialise and share. */
    public data class OnExportPreset(val id: Int) : PresetListIntent()
}

internal fun PresetModel.toUiModel() = PresetUiModel(
    id = id,
    name = name,
    updatedAt = updatedAt,
)
