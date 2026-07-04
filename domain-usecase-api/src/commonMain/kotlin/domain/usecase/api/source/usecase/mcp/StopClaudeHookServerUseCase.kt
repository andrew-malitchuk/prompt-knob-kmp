package domain.usecase.api.source.usecase.mcp

import domain.usecase.api.source.monad.Optional

/**
 * Stops the Claude Mode hook HTTP server gracefully.
 *
 * macOS only.
 */
public interface StopClaudeHookServerUseCase {
    public suspend operator fun invoke(): Optional
}
