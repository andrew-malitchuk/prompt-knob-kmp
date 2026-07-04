package domain.usecase.impl.source.usecase.mcp

import data.mcp.api.source.server.ClaudeHookServer
import domain.core.source.monad.Failure
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.mcp.StopClaudeHookServerUseCase
import domain.usecase.impl.core.resultLauncher

internal class StopClaudeHookServerUseCaseImpl(
    private val claudeHookServer: ClaudeHookServer,
) : StopClaudeHookServerUseCase {
    override suspend fun invoke(): Optional =
        resultLauncher(errorMapper = { Failure.Technical.Platform(it) }) {
            claudeHookServer.stop()
        }
}
