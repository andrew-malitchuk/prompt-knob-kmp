package domain.usecase.impl.source.usecase.command

import domain.core.source.model.CommandNodeModel
import domain.repository.api.source.repository.CommandRepository
import domain.usecase.api.source.usecase.command.SaveCommandUseCase

internal class SaveCommandUseCaseImpl(
    private val repository: CommandRepository,
) : SaveCommandUseCase {

    override suspend fun invoke(command: CommandNodeModel): Result<Int> =
        repository.save(command)
}
