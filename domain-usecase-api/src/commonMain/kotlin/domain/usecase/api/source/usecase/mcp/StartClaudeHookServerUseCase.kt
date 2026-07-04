package domain.usecase.api.source.usecase.mcp

import domain.usecase.api.source.monad.Optional

/**
 * Starts the Claude Mode hook HTTP server.
 *
 * This is a blocking call — the server runs until [StopClaudeHookServerUseCase] is called.
 * macOS only.
 */
public interface StartClaudeHookServerUseCase {
    public suspend operator fun invoke(): Optional
}
