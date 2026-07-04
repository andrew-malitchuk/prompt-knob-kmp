package data.mcp.api.source.server

/**
 * MCP (Model Context Protocol) SSE server that exposes PromptKnob device interaction
 * as tools callable by Claude Code and other MCP clients.
 */
public interface McpServer {
    /** Starts the server and blocks until stopped. Call from a background coroutine. */
    public suspend fun start()

    /** Gracefully stops the server. */
    public suspend fun stop()
}
