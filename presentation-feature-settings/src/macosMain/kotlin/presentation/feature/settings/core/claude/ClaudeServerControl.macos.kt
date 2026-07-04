package presentation.feature.settings.core.claude

import domain.usecase.api.source.usecase.mcp.StartClaudeHookServerUseCase
import domain.usecase.api.source.usecase.mcp.StopClaudeHookServerUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal actual suspend fun startClaudeHookServer() {
    object : KoinComponent {}.get<StartClaudeHookServerUseCase>()()
}

internal actual suspend fun stopClaudeHookServer() {
    object : KoinComponent {}.get<StopClaudeHookServerUseCase>()()
}
