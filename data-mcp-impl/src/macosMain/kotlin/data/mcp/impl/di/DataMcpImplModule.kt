package data.mcp.impl.di

import data.mcp.api.core.config.ClaudeHookServerConfig
import data.mcp.api.core.config.McpServerConfig
import data.mcp.api.source.server.ClaudeHookServer
import data.mcp.api.source.server.McpServer
import data.mcp.impl.source.server.ClaudeHookServerImpl
import data.mcp.impl.source.server.McpServerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public val dataMcpImplModule: Module = module {
    single { McpServerConfig() }
    singleOf(::McpServerImpl) bind McpServer::class
    single { ClaudeHookServerConfig() }
    singleOf(::ClaudeHookServerImpl) bind ClaudeHookServer::class
}
