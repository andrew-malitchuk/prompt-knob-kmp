package domain.usecase.api.source.usecase.mcp

import domain.usecase.api.source.monad.Optional

/** Starts the MCP SSE server and suspends until it is stopped. Call from a background coroutine. */
public interface StartMcpServerUseCase {
    public suspend operator fun invoke(): Optional
}
