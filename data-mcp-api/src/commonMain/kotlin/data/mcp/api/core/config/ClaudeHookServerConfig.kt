package data.mcp.api.core.config

/**
 * Configuration for the Claude Mode hook HTTP server.
 *
 * @property host Loopback address — server is not accessible from the network.
 * @property port Port on which the hook server listens. Must match the curl command
 *   in the Claude Code hooks configuration (~/.claude/settings.json).
 */
public data class ClaudeHookServerConfig(
    val host: String = "127.0.0.1",
    val port: Int = 7777,
)
