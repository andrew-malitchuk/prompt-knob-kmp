package domain.usecase.impl.source.usecase.mcp

import data.mcp.api.source.server.McpServer
import domain.core.source.monad.Failure
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.mcp.StartMcpServerUseCase
import domain.usecase.impl.core.resultLauncher

internal class StartMcpServerUseCaseImpl(
    private val mcpServer: McpServer,
) : StartMcpServerUseCase {
    override suspend fun invoke(): Optional =
        resultLauncher(errorMapper = { Failure.Technical.Platform(it) }) {
            mcpServer.start()
        }
}
