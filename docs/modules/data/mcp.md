# data-mcp

The `data-mcp` modules expose PromptKnob to Claude Code (and other MCP clients) as a local server, and receive Claude Code hook events that drive the knob's status display. Both servers are desktop-only integrations.

The pair splits into:

- **data-mcp-api** — server interfaces and their configuration.
- **data-mcp-impl** — [Ktor](https://ktor.io) (CIO) backed server implementations (macOS only).

## Purpose

Two cooperating HTTP servers:

- **MCP server** — a Model Context Protocol SSE server that publishes PromptKnob tools an MCP client can call. Blocking tools relay the request to the knob and wait for the user to respond.
- **Claude hook server** — a small HTTP endpoint that Claude Code hooks POST to, translating Claude's lifecycle state into a BLE write so the knob can show what Claude is doing.

## Key types (data-mcp-api)

| Type | Kind | Responsibility |
|------|------|----------------|
| `McpServer` | interface | `start()` / `stop()` the MCP SSE server. |
| `ClaudeHookServer` | interface | `start()` / `stop()` the Claude Code hook HTTP server. |
| `McpServerConfig` | data class | `host = "127.0.0.1"`, `port = 7474`, `approvalTimeoutMs = 30_000`. |
| `ClaudeHookServerConfig` | data class | `host = "127.0.0.1"`, `port = 7777`. |

Both servers bind to loopback (`127.0.0.1`) only.

## MCP server

`McpServerImpl` serves JSON-RPC 2.0 over Server-Sent Events on **port 7474**:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/sse` | GET | Opens the SSE stream and advertises the message endpoint. |
| `/message` | POST | Receives JSON-RPC requests. |

Alongside the standard `initialize`, `tools/list`, `tools/call`, and `ping` methods, it exposes three tools:

| Tool | Behaviour |
|------|-----------|
| `request_approval` | Shows an approval prompt on the knob and blocks until the user responds or the 30s timeout elapses. |
| `request_choice` | Shows a list of options on the knob and blocks until the user picks one (requires at least 2 options). |
| `notify` | Fire-and-forget notification to the knob with a message and severity level (info/success/warning/error). |

Tool calls are relayed to the knob through BLE use cases (for example `SendNotifyUseCase`).

## Claude hook server

`ClaudeHookServerImpl` listens on **port 7777** with a single endpoint:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/state` | POST | Receives a state string (typically sent by a `curl` command configured in `~/.claude/settings.json`). |

The request body maps to a `ClaudeState`, which is then written to the knob via `SendClaudeStateUseCase`:

| Body | `ClaudeState` |
|------|---------------|
| `working` | `WORKING` |
| `waiting` | `WAITING` |
| `done` | `DONE` |
| `error` | `ERROR` |
| anything else | `IDLE` |

## Dependency injection

The Koin module `dataMcpImplModule` (`data.mcp.impl.di`, macOS source set) registers both configs and binds `McpServerImpl` → `McpServer` and `ClaudeHookServerImpl` → `ClaudeHookServer`.

## Platform notes

Implementations live in the `macosMain` source set only. The API interfaces are in `commonMain`, but the servers are wired and run on macOS (the desktop companion), where PromptKnob acts as the bridge between Claude Code and the knob.

## Dependencies

- `domain-usecase-api` (BLE use cases the tools invoke)
- Ktor (CIO engine, SSE, content negotiation)
- kotlinx-serialization
- Koin
