# CLAUDE.md — data-mcp-api

Guidance for an AI agent working in this module.

## Responsibility

Pure API surface for the two loopback HTTP servers that connect Claude Code to the PromptKnob device:
- `McpServer` — MCP SSE server (default port `7474`).
- `ClaudeHookServer` — Claude Mode hook server (default port `7777`).

Plus their config data classes (`McpServerConfig`, `ClaudeHookServerConfig`). No implementations live here.

## Convention plugin

Applies `dev.prompt.knob.io.convention.library`. Explicit API mode is enforced — every public declaration needs an explicit visibility modifier.

## API/Impl split rules

- This module owns **interfaces and config models only**. Implementations belong in `data-mcp-impl` (macOS source set).
- Keep it dependency-free. Do not add Ktor, coroutines, or Koin here — those are impl concerns.
- If you add a new server capability, declare the interface method / config field here first, then implement it in `data-mcp-impl`.

## Gotchas

- Ports are load-bearing: `7474` (MCP) and `7777` (hook) must match the user's Claude Code MCP config and `~/.claude/settings.json` hook `curl` respectively. Don't change defaults casually.
- `start()` is a blocking suspend call (runs the embedded server with `wait = true`) — document any new lifecycle method accordingly.
- `ClaudeHookServer` maps hook state strings to `domain.core.source.model.ClaudeState`; keep the config's contract in sync with that enum.
