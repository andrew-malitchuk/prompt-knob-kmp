package presentation.feature.settings.di

import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named

/** Named qualifier for the MCP availability flag. */
public val mcpAvailableQualifier: Qualifier = named("isMcpAvailable")

/** Named qualifier for the Claude Mode availability flag. */
public val claudeAvailableQualifier: Qualifier = named("isClaudeAvailable")

internal expect fun Module.providePlatformMcpAvailableFlag()
internal expect fun Module.providePlatformClaudeAvailableFlag()
