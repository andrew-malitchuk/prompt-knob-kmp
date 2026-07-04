package data.mcp.api.source.server

/**
 * HTTP server that receives Claude Code hook events and translates them into BLE state writes.
 *
 * Listens on localhost:7777 for POST /state requests from curl commands configured in
 * ~/.claude/settings.json. Maps incoming state strings to [domain.core.source.model.ClaudeState]
 * values and sends them to the connected device via BLE.
 */
public interface ClaudeHookServer {
    /** Starts the server and blocks until stopped. Call from a background coroutine. */
    public suspend fun start()

    /** Gracefully stops the server. */
    public suspend fun stop()
}
