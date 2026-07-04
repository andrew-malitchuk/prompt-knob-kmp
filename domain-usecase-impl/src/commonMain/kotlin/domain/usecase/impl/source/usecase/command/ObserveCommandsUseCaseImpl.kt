package domain.usecase.impl.source.usecase.command

import domain.core.source.model.CommandNodeModel
import domain.repository.api.source.repository.CommandRepository
import domain.usecase.api.source.usecase.command.ObserveCommandsUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveCommandsUseCaseImpl(
    private val repository: CommandRepository,
) : ObserveCommandsUseCase {

    override fun invoke(parentId: Int): Flow<List<CommandNodeModel>> =
        repository.observeByParentId(parentId)
}
