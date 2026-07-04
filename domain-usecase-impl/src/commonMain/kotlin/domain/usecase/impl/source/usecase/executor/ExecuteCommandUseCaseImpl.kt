package domain.usecase.impl.source.usecase.executor

import domain.usecase.api.source.monad.Optional
import data.executor.api.source.executor.CommandExecutor
import domain.core.source.model.CommandNodeModel
import domain.usecase.api.source.usecase.executor.ExecuteCommandUseCase

internal class ExecuteCommandUseCaseImpl(
    private val executor: CommandExecutor,
) : ExecuteCommandUseCase {
    override suspend fun invoke(command: CommandNodeModel): Optional = executor.execute(command)
}
