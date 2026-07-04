package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.source.monad.Optional

/**
 * Use case for persisting the Claude Mode hook server enabled state.
 */
public interface SetClaudeHookEnabledUseCase {
    public suspend operator fun invoke(enabled: Boolean): Optional
}
