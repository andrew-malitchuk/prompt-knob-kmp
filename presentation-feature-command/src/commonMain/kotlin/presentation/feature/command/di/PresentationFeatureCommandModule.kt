package presentation.feature.command.di

import domain.usecase.api.source.usecase.ble.SyncCommandsUseCase
import domain.usecase.api.source.usecase.command.DeleteCommandUseCase
import domain.usecase.api.source.usecase.command.GetCommandByIdUseCase
import domain.usecase.api.source.usecase.command.ObserveCommandsUseCase
import domain.usecase.api.source.usecase.command.SaveCommandUseCase
import domain.usecase.api.source.usecase.executor.ExecuteCommandUseCase
import org.koin.core.module.Module
import org.koin.dsl.module
import presentation.feature.command.source.form.CommandFormViewModel
import presentation.feature.command.source.list.CommandListViewModel

/**
 * Koin module for the command management feature.
 *
 * Both ViewModels accept runtime parameters injected via [org.koin.core.parameter.parametersOf].
 */
public val presentationFeatureCommandModule: Module = module {
    factory { params ->
        CommandListViewModel(
            parentId = params.get(0),
            isParentRotary = params.get(1),
            observeCommands = get<ObserveCommandsUseCase>(),
            deleteCommand = get<DeleteCommandUseCase>(),
            syncCommands = get<SyncCommandsUseCase>(),
        )
    }
    factory { params ->
        CommandFormViewModel(
            commandId = params.get(0),
            isFolder = params.get(1),
            parentId = params.get(2),
            initialSortOrder = params.get(3),
            isParentRotary = params.get(4),
            getCommandById = get<GetCommandByIdUseCase>(),
            saveCommand = get<SaveCommandUseCase>(),
            deleteCommand = get<DeleteCommandUseCase>(),
            executeCommand = get<ExecuteCommandUseCase>(),
            syncCommands = get<SyncCommandsUseCase>(),
        )
    }
}
