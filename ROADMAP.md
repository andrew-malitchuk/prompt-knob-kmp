# PromptKnob Roadmap

This is the product roadmap for **PromptKnob** — the KMP companion app for the PromptKnob
BLE rotary knob. It positions the knob as a programmable control surface (macro-pad-style)
that spans macOS and Android, with deep Claude Code integration on macOS.

> Status legend: ✅ done · 🚧 in progress · ⏳ planned

## Phase 1 — Core companion (foundation)

The end-to-end path from hardware to bound actions.

- ✅ BLE scan, pair, connect, and reconnect (YAMK protocol)
- ✅ Command tree (create / edit / organise commands) with local persistence (Room)
- ✅ Per-platform command execution: macOS (system + shell), Android (system + Google
  Assistant prompts)
- ✅ Preset gallery with per-platform packs and JSON import/export
- ✅ Adaptive Compose UI, theming, and localisation (en / uk / es / de)
- ✅ Claude Code integration on macOS — MCP server (`request_approval`, `request_choice`,
  `notify`) + hook activity overlay

## Phase 2 — Integrations (next)

Turn the knob into a controller for the tools people already use. Prioritised:

- ⏳ **OBS Studio** — toggle sources, switch scenes, start/stop recording & streaming
- ⏳ **Spotify / Apple Music** — play/pause, next/prev, volume via API
- ⏳ **Zoom / Teams** — mute, screen share, leave meeting
- ⏳ **Webhooks** — generic HTTP action so any service can be bound

## Phase 3 — Sync & multi-device

- ⏳ Cloud sync of command trees and presets across devices
- ⏳ Preset sharing / community gallery
- ⏳ Multiple knob profiles and per-app context switching

## Phase 4 — Power features

- ⏳ Macros / multi-step command sequences
- ⏳ Conditional bindings (context-aware actions)
- ⏳ On-device firmware OTA updates from the app
- ⏳ Analytics / usage insights for tuning bindings

---

For architecture and module details see [`docs/`](docs/index.md).
