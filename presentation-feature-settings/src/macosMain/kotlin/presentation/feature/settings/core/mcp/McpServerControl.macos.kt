package presentation.feature.settings.core.mcp

import domain.usecase.api.source.usecase.mcp.StartMcpServerUseCase
import domain.usecase.api.source.usecase.mcp.StopMcpServerUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal actual suspend fun startMcpServer() {
    object : KoinComponent {}.get<StartMcpServerUseCase>()()
}

internal actual suspend fun stopMcpServer() {
    object : KoinComponent {}.get<StopMcpServerUseCase>()()
}
