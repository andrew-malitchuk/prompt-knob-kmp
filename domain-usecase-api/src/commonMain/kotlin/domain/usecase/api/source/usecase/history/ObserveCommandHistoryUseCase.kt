package domain.usecase.api.source.usecase.history

import domain.core.source.model.CommandHistoryEntryModel
import kotlinx.coroutines.flow.Flow

/** Returns a hot [Flow] of all command history entries, newest first. */
public interface ObserveCommandHistoryUseCase {
    public operator fun invoke(): Flow<List<CommandHistoryEntryModel>>
}
