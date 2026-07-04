# Claude Code Integration (MCP Setup)

When the **macOS** companion app is running and a PromptKnob device is
connected, it exposes two local servers that together turn the knob into a
**human-in-the-loop control surface** for Claude Code.

```
Claude Code
    ├── MCP tools (JSON-RPC / SSE) ──▶ macOS app :7474 ──┐
    └── Hooks   (HTTP POST)         ──▶ macOS app :7777 ──┤
                                                          │ BLE (YAMK)
                                                          ▼
                                                 PromptKnob (ESP32)
```

Both servers bind to loopback (`127.0.0.1`) only — they are not reachable from
the network.

## Two channels, two roles

| Channel | Port | Direction | What it does |
|---------|------|-----------|--------------|
| **MCP server** | 7474 | Claude → knob → Claude | Interactive tools that block and wait for a physical response |
| **Hook server** | 7777 | Claude → knob | Passive overlay showing Claude's working state on the device screen |

## Prerequisites

1. Run the macOS app:
   ```bash
   ./gradlew :compose-application:runReleaseMacosArm64
   ```
2. Connect the device: **Devices** → tap the knob in the scan list → wait for
   **Connected**. See [Getting Started on macOS](getting-started-macos.md).

---

## Hook server — activity overlay

Claude Code hooks POST a plain state string to `:7777/state` on every tool use.
The macOS app forwards the state over BLE and the device screen reacts
automatically:

| Hook event | State sent | Device screen |
|------------|-----------|---------------|
| `PreToolUse` | `working` | Orange ring `WORKING...` — Claude is busy |
| `PostToolUse` | `done` | Green ring `DONE ✓` — auto-dismisses after 1.5 s |
| `Notification` | `waiting` | Red ring `WAITING` — Claude needs your attention |
| `Stop` | `idle` | Overlay dismisses — session ended |

The server recognises the states `working`, `waiting`, `done`, `error` and
`idle` (any unrecognised body is treated as `idle`).

Add the following to `~/.claude/settings.json`:

```json
{
  "hooks": {
    "PreToolUse":  [{ "hooks": [{ "type": "command", "command": "curl -s -X POST http://127.0.0.1:7777/state -d 'working' 2>/dev/null || true" }] }],
    "PostToolUse": [{ "hooks": [{ "type": "command", "command": "curl -s -X POST http://127.0.0.1:7777/state -d 'done'    2>/dev/null || true" }] }],
    "Stop":        [{ "hooks": [{ "type": "command", "command": "curl -s -X POST http://127.0.0.1:7777/state -d 'idle'    2>/dev/null || true" }] }],
    "Notification":[{ "hooks": [{ "type": "command", "command": "curl -s -X POST http://127.0.0.1:7777/state -d 'waiting' 2>/dev/null || true" }] }]
  }
}
```

The `|| true` keeps hooks harmless when the app is not running. The overlay
auto-shows and auto-hides — no manual navigation on the device required.

---

## MCP server — interactive control

The `.mcp.json` file in the project root registers the MCP server with Claude
Code. When present, Claude Code connects automatically over SSE and gains three
tools.

The MCP server speaks JSON-RPC over Server-Sent Events on port **7474**:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/sse` | GET (SSE) | Opens the event stream; returns the per-session `/message` endpoint |
| `/message?sessionId=…` | POST | Delivers a JSON-RPC request for that session |

### The three tools

| Tool | Blocks | Required args | Purpose |
|------|--------|---------------|---------|
| `request_approval` | Yes (30 s) | `reason` | Show an approve / reject screen. User taps to approve or swipes to reject. |
| `request_choice` | Yes (30 s) | `options` (2+ strings), optional `prompt` | Show a scrollable list. User rotates the encoder to navigate, taps to confirm, swipes to cancel. |
| `notify` | No | `message`, optional `level` | Fire-and-forget notification with haptic / LED feedback. |

Notes:

- **Blocking tools** wait up to **30 seconds** for a physical response. On
  timeout, `request_approval` returns `{"approved":false,"reason":"timeout"}`
  and `request_choice` returns `{"selected":null,"reason":"timeout"}`.
- `request_choice` requires **at least two** options and returns the selected
  option string plus its index, or `null` if cancelled.
- `notify`'s `level` is one of `info` · `success` · `warning` · `error` and
  drives the LED / haptic hint. It returns immediately with `{"sent":true}`.

---

## User flows

### 1. Normal task — hooks only

```
You: "Refactor the BleRepository"

Claude starts reading files
  → PreToolUse  → device: orange WORKING...
  → PostToolUse → device: green DONE ✓ (disappears after 1.5 s)
  → PreToolUse  → device: orange WORKING... (next file)
  → PostToolUse → device: green DONE ✓
  ...
Claude finishes, writes response
  → Stop        → overlay gone, device back to the wheel
```

The knob shows what Claude is doing without you touching the keyboard.

### 2. Risky operation — `request_approval`

```
You: "Reset the branch to origin/main"

Claude recognises `git reset --hard` → calls request_approval
  → Device: approval screen "Reset to origin/main?"
  → You: tap centre circle   → approved → Claude executes
     or swipe                → rejected → Claude stops
     or wait 30 s            → timeout  → Claude stops
```

The knob physically gates destructive operations — Claude will not proceed
without a response.

### 3. Ambiguous request — `request_choice`

```
You: "Which approach should I use for the new screen?"

Claude has 2–4 valid options → calls request_choice
  → Device: scrollable list ["ViewModel + State", "Plain composable", "Cancel"]
  → You: rotate encoder to highlight, tap to confirm
  → Claude continues with the selected option
```

### 4. Task complete — `notify`

```
Claude finishes a long build or test run
  → calls notify("Build OK", level="success")
  → Device: brief green notification overlay + haptic pulse
  → Claude continues
```

Useful when you step away and want a physical signal that the task is done.

---

## Quick start

1. Build and run the macOS app:
   `./gradlew :compose-application:runReleaseMacosArm64`
2. Connect your device: **Devices** → tap the knob → wait for **Connected**.
3. Add the hooks block above to `~/.claude/settings.json`.
4. Run Claude Code from this directory — the project's `.mcp.json` registers the
   MCP server automatically.

## Troubleshooting

- **Nothing happens on the device.** Confirm the macOS app is running and the
  knob shows **Connected**. Both servers are hosted by the app.
- **Hooks do nothing.** Check the ports match (`7777` for hooks, `7474` for
  MCP) and that `~/.claude/settings.json` is valid JSON.
- **MCP tools not offered.** Ensure `.mcp.json` exists in the directory you run
  Claude Code from, and that the app is running so port `7474` is listening.
- **Approvals time out immediately.** The blocking tools wait 30 seconds for a
  tap or swipe; make sure you are responding on the connected device.
