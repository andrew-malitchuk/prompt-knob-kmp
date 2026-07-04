package domain.usecase.impl.source.usecase.command

import domain.core.source.monad.Failure
import domain.core.source.model.CommandNodeModel
import domain.repository.api.source.repository.CommandRepository
import domain.usecase.api.source.usecase.command.GetCommandByIdUseCase
import domain.usecase.impl.core.resultLauncher

internal class GetCommandByIdUseCaseImpl(
    private val repository: CommandRepository,
) : GetCommandByIdUseCase {

    override suspend fun invoke(id: Int): Result<CommandNodeModel> = resultLauncher(
        errorMapper = Failure.Technical::Database,
    ) {
        repository.getById(id) ?: throw Failure.Logic.NotFound
    }
}
