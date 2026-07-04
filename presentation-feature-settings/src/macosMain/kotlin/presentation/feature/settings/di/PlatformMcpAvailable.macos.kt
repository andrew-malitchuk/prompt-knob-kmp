package presentation.feature.settings.di

import org.koin.core.module.Module

// macOS is the only platform where the MCP server runs.
internal actual fun Module.providePlatformMcpAvailableFlag() {
    single(mcpAvailableQualifier) { true }
}

internal actual fun Module.providePlatformClaudeAvailableFlag() {
    single(claudeAvailableQualifier) { true }
}
