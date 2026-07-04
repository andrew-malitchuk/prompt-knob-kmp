package domain.usecase.impl.source.usecase.history

import domain.core.source.model.CommandHistoryEntryModel
import domain.repository.api.source.repository.CommandHistoryRepository
import domain.usecase.api.source.usecase.history.ObserveCommandHistoryUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveCommandHistoryUseCaseImpl(
    private val repository: CommandHistoryRepository,
) : ObserveCommandHistoryUseCase {
    override fun invoke(): Flow<List<CommandHistoryEntryModel>> = repository.observeAll()
}
