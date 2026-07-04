package data.mcp.api.core.config

/**
 * Configuration for the MCP SSE server.
 *
 * @param host Bind address (loopback only — not intended for external access).
 * @param port TCP port the server listens on. Claude Code config must match.
 * @param approvalTimeoutMs How long to wait for a knob response before auto-rejecting.
 */
public data class McpServerConfig(
    val host: String = "127.0.0.1",
    val port: Int = 7474,
    val approvalTimeoutMs: Long = 30_000L,
)
