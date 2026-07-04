# data-mcp-impl

> macOS implementations of the MCP SSE server and the Claude Mode hook server, built on Ktor CIO. Bridges Claude Code to the connected PromptKnob device over BLE.

## Responsibility

Implements the `McpServer` and `ClaudeHookServer` interfaces from `data-mcp-api`. The MCP server exposes device interaction as MCP tools over SSE + JSON-RPC; the hook server turns Claude Code lifecycle hooks into device state updates. Both delegate to domain use cases to talk to the device.

## Dependencies

| Depends on | Purpose |
|---|---|
| `data-mcp-api` | Server interfaces and config models to implement |
| `domain-usecase-api` | BLE use cases (send approval/choice/notify, await selection, send state) |
| Ktor server (CIO, SSE) | Embedded HTTP/SSE server (macOS source set) |
| kotlinx-serialization-json | JSON-RPC request/response handling |
| Koin, Kermit | DI wiring and logging |

## Public API

| Symbol | Description |
|---|---|
| `dataMcpImplModule` | Koin module registering `McpServerConfig`, `ClaudeHookServerConfig`, and both server singletons bound to their API interfaces. |

### Internal Implementations

| Class | Implements | Port / Endpoints |
|---|---|---|
| `McpServerImpl` | `McpServer` | `7474` — `GET /sse`, `POST /message?sessionId=…` |
| `ClaudeHookServerImpl` | `ClaudeHookServer` | `7777` — `POST /state` |

## MCP Server (`McpServerImpl`)

Speaks MCP over SSE + JSON-RPC 2.0. `GET /sse` opens a session and emits an `endpoint` event pointing at `/message?sessionId=…`; the client POSTs JSON-RPC requests there and receives replies as SSE `message` events. Handles `initialize`, `tools/list`, `tools/call`, and `ping`.

Exposes three tools:

| Tool | Behaviour |
|---|---|
| `request_approval` | Shows an approval screen on the knob; blocks until the user approves/rejects or the timeout expires. Returns `{approved, …}`. |
| `request_choice` | Shows a scrollable option list (≥2 options); blocks until the user selects or cancels. Returns `{selected, index}` or a cancel/timeout reason. |
| `notify` | Fire-and-forget message + severity `level` (`info`/`success`/`warning`/`error`); returns immediately with `{"sent":true}`. |

Approval/choice subscribe to the device response (`AwaitCommandSelectedUseCase`) *before* sending the opcode, using `McpServerConfig.approvalTimeoutMs` (default 30 s).

## Claude Hook Server (`ClaudeHookServerImpl`)

`POST /state` with a plain-text body; maps `working` / `waiting` / `done` / `error` to `ClaudeState` (anything else → `IDLE`) and forwards via `SendClaudeStateUseCase`.

## Platform Notes

macOS-only (`macosMain`). Servers bind to loopback and `start()` blocks (`wait = true`) — run on a background coroutine.

## Testing

```bash
./gradlew :data-mcp-impl:test
```
