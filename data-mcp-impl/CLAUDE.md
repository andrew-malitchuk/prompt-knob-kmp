# CLAUDE.md — data-mcp-impl

Guidance for an AI agent working in this module.

## Responsibility

macOS implementations of the two servers declared in `data-mcp-api`:
- `McpServerImpl` — MCP SSE server (port `7474`): `GET /sse` + `POST /message`, JSON-RPC 2.0, tools `request_approval` / `request_choice` / `notify`.
- `ClaudeHookServerImpl` — Claude Mode hook server (port `7777`): `POST /state`.

Both are built on Ktor CIO and delegate to domain use cases to reach the device over BLE.

## Convention plugins

Applies `dev.prompt.knob.io.convention.library` and `dev.prompt.knob.io.convention.di`. Explicit API mode is enforced.

## API/Impl split rules

- Impl classes are `internal` and bound to their `data-mcp-api` interfaces in `dataMcpImplModule`. Never make impls public — consumers depend on the API interfaces via Koin.
- New DI registrations go in `dataMcpImplModule` (`data.mcp.impl.di`).
- All server logic lives in the **macOS source set** (`macosMain`) — Ktor server is only wired there. Don't add server code to `commonMain`.

## Gotchas

- `start()` runs the embedded server with `wait = true` (blocking) — always launch on a background coroutine and pair with `stop()`.
- For approval/choice, subscribe to the device response *before* sending the opcode (see the `coroutineScope { async { awaitCommandSelected(...) }; send...() }` pattern) or you'll miss the event.
- Timeout comes from `McpServerConfig.approvalTimeoutMs` (default 30 s); a null response = timeout.
- Hook state strings map to `domain.core.source.model.ClaudeState`; unknown strings fall back to `IDLE`.
- Ports `7474` / `7777` must match the user's Claude Code MCP config and `~/.claude/settings.json` hooks.
