package domain.usecase.api.source.usecase.mcp

import domain.usecase.api.source.monad.Optional

/** Gracefully stops the running MCP SSE server. */
public interface StopMcpServerUseCase {
    public suspend operator fun invoke(): Optional
}
