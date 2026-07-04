package domain.usecase.impl.source.usecase.history

import domain.repository.api.source.repository.CommandHistoryRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.history.ClearCommandHistoryUseCase

internal class ClearCommandHistoryUseCaseImpl(
    private val repository: CommandHistoryRepository,
) : ClearCommandHistoryUseCase {
    override suspend fun invoke(): Optional = repository.clearAll()
}
