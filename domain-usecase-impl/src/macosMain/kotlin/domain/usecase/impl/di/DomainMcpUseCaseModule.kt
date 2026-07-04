package domain.usecase.impl.di

import domain.usecase.api.source.usecase.mcp.StartClaudeHookServerUseCase
import domain.usecase.api.source.usecase.mcp.StartMcpServerUseCase
import domain.usecase.api.source.usecase.mcp.StopClaudeHookServerUseCase
import domain.usecase.api.source.usecase.mcp.StopMcpServerUseCase
import domain.usecase.impl.source.usecase.mcp.StartClaudeHookServerUseCaseImpl
import domain.usecase.impl.source.usecase.mcp.StartMcpServerUseCaseImpl
import domain.usecase.impl.source.usecase.mcp.StopClaudeHookServerUseCaseImpl
import domain.usecase.impl.source.usecase.mcp.StopMcpServerUseCaseImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public val domainMcpUseCaseModule: Module = module {
    singleOf(::StartMcpServerUseCaseImpl) bind StartMcpServerUseCase::class
    singleOf(::StopMcpServerUseCaseImpl) bind StopMcpServerUseCase::class
    singleOf(::StartClaudeHookServerUseCaseImpl) bind StartClaudeHookServerUseCase::class
    singleOf(::StopClaudeHookServerUseCaseImpl) bind StopClaudeHookServerUseCase::class
}
