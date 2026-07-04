package domain.usecase.api.source.usecase.history

import domain.usecase.api.source.monad.Optional

/** Deletes all command execution history entries. */
public interface ClearCommandHistoryUseCase {
    public suspend operator fun invoke(): Optional
}
