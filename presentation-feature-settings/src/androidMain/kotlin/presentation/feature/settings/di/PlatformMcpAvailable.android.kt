package presentation.feature.settings.di

import org.koin.core.module.Module

internal actual fun Module.providePlatformMcpAvailableFlag() {
    single(mcpAvailableQualifier) { false }
}

internal actual fun Module.providePlatformClaudeAvailableFlag() {
    single(claudeAvailableQualifier) { false }
}
