package presentation.feature.preset.source.list

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import domain.core.source.monad.Failure
import domain.core.source.model.GalleryPreset
import domain.usecase.api.source.usecase.ble.SyncCommandsUseCase
import domain.usecase.api.source.usecase.preset.DeletePresetUseCase
import domain.usecase.api.source.usecase.preset.ExportPresetUseCase
import domain.usecase.api.source.usecase.preset.GetGalleryPresetsUseCase
import domain.usecase.api.source.usecase.preset.ImportPresetUseCase
import domain.usecase.api.source.usecase.preset.LoadPresetUseCase
import domain.usecase.api.source.usecase.preset.ObservePresetsUseCase
import domain.usecase.api.source.usecase.preset.SavePresetUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container

/**
 * ViewModel for the preset list screen.
 *
 * On init, loads the bundled gallery and starts observing user-saved presets reactively.
 * After any load or gallery-import operation, syncs the new command layout to the connected
 * device via [SyncCommandsUseCase]. Sync failures are logged as warnings but do not
 * surface as errors to the user.
 *
 * @param getGalleryPresets Provides the built-in read-only gallery presets.
 * @param observePresets Observes the list of user-saved presets reactively.
 * @param savePreset Saves the current command tree as a new named preset.
 * @param loadPreset Activates a saved preset by replacing the current command tree.
 * @param deletePreset Deletes a saved preset by id.
 * @param exportPreset Serialises a preset to a shareable JSON string.
 * @param importPreset Creates a new preset from a JSON payload.
 * @param syncCommands Pushes the current command tree to the connected knob device.
 *
 * @see PresetListState
 * @see PresetListSideEffect
 * @see PresetListScreen
 */
@OrbitExperimental
public class PresetListViewModel(
    private val getGalleryPresets: GetGalleryPresetsUseCase,
    private val observePresets: ObservePresetsUseCase,
    private val savePreset: SavePresetUseCase,
    private val loadPreset: LoadPresetUseCase,
    private val deletePreset: DeletePresetUseCase,
    private val exportPreset: ExportPresetUseCase,
    private val importPreset: ImportPresetUseCase,
    private val syncCommands: SyncCommandsUseCase,
) : ContainerHost<PresetListState, PresetListSideEffect>, ViewModel() {

    private val log = Logger.withTag("PresetListViewModel")

    private fun Throwable.errorMessage(): String =
        when (this) {
            is Failure -> cause?.message ?: message ?: this::class.simpleName ?: "Unknown error"
            else -> message ?: this::class.simpleName ?: "Unknown error"
        }

    override val container: Container<PresetListState, PresetListSideEffect> =
        container(PresetListState()) {
            loadGallery()
            observeUserPresets()
        }

    public fun handleIntent(intent: PresetListIntent) {
        when (intent) {
            PresetListIntent.OnBackClick -> onBackClick()
            is PresetListIntent.OnGalleryPresetClick -> onGalleryPresetClick(intent.preset)
            PresetListIntent.OnConfirmGalleryPreset -> onConfirmGalleryPreset()
            PresetListIntent.OnDismissGalleryPreset -> onDismissGalleryPreset()
            PresetListIntent.OnSaveClick -> onSaveClick()
            is PresetListIntent.OnSaveConfirm -> onSaveConfirm(intent.name)
            PresetListIntent.OnSaveDismiss -> onSaveDismiss()
            is PresetListIntent.OnLoadPreset -> onLoadPreset(intent.id)
            is PresetListIntent.OnDeleteClick -> onDeleteClick(intent.id, intent.label)
            PresetListIntent.OnConfirmDelete -> onConfirmDelete()
            PresetListIntent.OnDismissDelete -> onDismissDelete()
            is PresetListIntent.OnExportPreset -> onExportPreset(intent.id)
        }
    }

    private fun loadGallery() = intent {
        reduce { state.copy(gallery = getGalleryPresets().getOrDefault(emptyList())) }
    }

    private fun observeUserPresets() = intent {
        observePresets()
            .onEach { presets ->
                reduce {
                    state.copy(
                        isLoading = false,
                        presets = presets.map { it.toUiModel() },
                    )
                }
            }
            .catch { t ->
                reduce { state.copy(isLoading = false) }
                postSideEffect(PresetListSideEffect.ShowError(t.errorMessage()))
            }
            .collect {}
    }

    private fun onBackClick() = intent {
        postSideEffect(PresetListSideEffect.NavigateBack)
    }

    private fun onGalleryPresetClick(preset: GalleryPreset) = intent {
        reduce { state.copy(pendingGalleryPreset = preset) }
    }

    private fun onConfirmGalleryPreset() = intent {
        val preset = state.pendingGalleryPreset ?: return@intent
        reduce { state.copy(pendingGalleryPreset = null) }
        importPreset(preset.name, preset.configJson)
            .onSuccess { id ->
                loadPreset(id)
                    .onSuccess {
                        syncCommands().onFailure { t ->
                            log.w { "SYNC failed after gallery load — ${t.message}" }
                        }
                        postSideEffect(PresetListSideEffect.NavigateBack)
                    }
                    .onFailure { t ->
                        postSideEffect(PresetListSideEffect.ShowError(t.errorMessage()))
                    }
            }
            .onFailure { t ->
                postSideEffect(PresetListSideEffect.ShowError(t.errorMessage()))
            }
    }

    private fun onDismissGalleryPreset() = intent {
        reduce { state.copy(pendingGalleryPreset = null) }
    }

    private fun onSaveClick() = intent {
        reduce { state.copy(showSaveDialog = true) }
    }

    private fun onSaveConfirm(name: String) = intent {
        reduce { state.copy(showSaveDialog = false) }
        savePreset(name)
            .onFailure { t ->
                postSideEffect(PresetListSideEffect.ShowError(t.errorMessage()))
            }
    }

    private fun onSaveDismiss() = intent {
        reduce { state.copy(showSaveDialog = false) }
    }

    private fun onLoadPreset(id: Int) = intent {
        loadPreset(id)
            .onSuccess {
                syncCommands().onFailure { t ->
                    log.w { "SYNC failed after load — ${t.message}" }
                }
            }
            .onFailure { t ->
                postSideEffect(PresetListSideEffect.ShowError(t.errorMessage()))
            }
    }

    private fun onDeleteClick(id: Int, label: String) = intent {
        reduce { state.copy(pendingDeleteId = id, pendingDeleteLabel = label) }
    }

    private fun onConfirmDelete() = intent {
        val id = state.pendingDeleteId ?: return@intent
        reduce { state.copy(pendingDeleteId = null, pendingDeleteLabel = null) }
        deletePreset(id)
            .onFailure { t ->
                postSideEffect(PresetListSideEffect.ShowError(t.errorMessage()))
            }
    }

    private fun onDismissDelete() = intent {
        reduce { state.copy(pendingDeleteId = null, pendingDeleteLabel = null) }
    }

    private fun onExportPreset(id: Int) = intent {
        exportPreset(id)
            .onSuccess { json -> postSideEffect(PresetListSideEffect.ShareExport(json)) }
            .onFailure { t ->
                postSideEffect(PresetListSideEffect.ShowError(t.errorMessage()))
            }
    }
}
