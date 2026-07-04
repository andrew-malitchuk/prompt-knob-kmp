package presentation.feature.preset.di

import domain.usecase.api.source.usecase.ble.SyncCommandsUseCase
import domain.usecase.api.source.usecase.preset.DeletePresetUseCase
import domain.usecase.api.source.usecase.preset.ExportPresetUseCase
import domain.usecase.api.source.usecase.preset.GetGalleryPresetsUseCase
import domain.usecase.api.source.usecase.preset.ImportPresetUseCase
import domain.usecase.api.source.usecase.preset.LoadPresetUseCase
import domain.usecase.api.source.usecase.preset.ObservePresetsUseCase
import domain.usecase.api.source.usecase.preset.SavePresetUseCase
import org.koin.core.module.Module
import org.koin.dsl.module
import org.orbitmvi.orbit.annotation.OrbitExperimental
import presentation.feature.preset.source.list.PresetListViewModel

@OptIn(OrbitExperimental::class)
public val presentationFeaturePresetModule: Module = module {
    factory {
        PresetListViewModel(
            getGalleryPresets = get<GetGalleryPresetsUseCase>(),
            observePresets = get<ObservePresetsUseCase>(),
            savePreset = get<SavePresetUseCase>(),
            loadPreset = get<LoadPresetUseCase>(),
            deletePreset = get<DeletePresetUseCase>(),
            exportPreset = get<ExportPresetUseCase>(),
            importPreset = get<ImportPresetUseCase>(),
            syncCommands = get<SyncCommandsUseCase>(),
        )
    }
}
