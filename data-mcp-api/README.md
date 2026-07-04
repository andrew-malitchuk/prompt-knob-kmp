# data-mcp-api

> Contracts and configuration for the two local HTTP servers that bridge Claude Code and the PromptKnob device: the MCP SSE server and the Claude Mode hook server.

## Responsibility

Declares the server interfaces and their config models. Both servers run on loopback only and are implemented in `data-mcp-impl`. This module holds no logic — just the API surface consumers depend on.

## Dependencies

None. Leaf API module (no internal project dependencies).

## Public API

### Server Interfaces

| Interface | Description |
|---|---|
| `McpServer` | MCP (Model Context Protocol) SSE server exposing device interaction as tools for Claude Code and other MCP clients. `suspend start()` / `suspend stop()`. |
| `ClaudeHookServer` | HTTP server that receives Claude Code hook events and translates them into BLE state writes. `suspend start()` / `suspend stop()`. |

### Config Models

| Class | Fields (defaults) | Description |
|---|---|---|
| `McpServerConfig` | `host = "127.0.0.1"`, `port = 7474`, `approvalTimeoutMs = 30_000L` | Bind address, TCP port (must match the Claude Code MCP config), and how long to wait for a knob response before auto-rejecting. |
| `ClaudeHookServerConfig` | `host = "127.0.0.1"`, `port = 7777` | Bind address and port; the port must match the `curl` command in `~/.claude/settings.json` hooks. |

## Server Endpoints (as implemented in `data-mcp-impl`)

- **MCP** (`McpServer`, port `7474`): `GET /sse` (opens an SSE session, emits an `endpoint` event) and `POST /message?sessionId=…` (JSON-RPC 2.0). Exposes tools `request_approval`, `request_choice`, `notify`.
- **Claude hook** (`ClaudeHookServer`, port `7777`): `POST /state` — body is one of `working` / `waiting` / `done` / `error`, anything else maps to `idle`.

## Notes

- `start()` blocks until the server is stopped — call it from a background coroutine.
- Servers bind to loopback (`127.0.0.1`); they are not reachable from the network.

## Testing

```bash
./gradlew :data-mcp-api:test
```
