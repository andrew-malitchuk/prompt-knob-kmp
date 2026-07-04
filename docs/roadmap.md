# Roadmap

The product roadmap for **PromptKnob** — positioning the BLE knob as a programmable control
surface across macOS and Android, with deep Claude Code integration on macOS.

> Status legend: ✅ done · 🚧 in progress · ⏳ planned

## Phase 1 — Core companion (foundation)

- ✅ BLE scan, pair, connect, and reconnect (YAMK protocol)
- ✅ Command tree with local persistence (Room)
- ✅ Per-platform execution: macOS (system + shell), Android (system + Google Assistant prompts)
- ✅ Preset gallery with per-platform packs and JSON import/export
- ✅ Adaptive Compose UI, theming, localisation (en / uk / es / de)
- ✅ Claude Code integration on macOS — MCP server + hook activity overlay

## Phase 2 — Integrations (next)

- ⏳ **OBS Studio** — toggle sources, switch scenes, start/stop recording & streaming
- ⏳ **Spotify / Apple Music** — play/pause, next/prev, volume via API
- ⏳ **Zoom / Teams** — mute, screen share, leave meeting
- ⏳ **Webhooks** — generic HTTP action for any service

## Phase 3 — Sync & multi-device

- ⏳ Cloud sync of command trees and presets
- ⏳ Preset sharing / community gallery
- ⏳ Multiple knob profiles and per-app context switching

## Phase 4 — Power features

- ⏳ Macros / multi-step command sequences
- ⏳ Conditional, context-aware bindings
- ⏳ On-device firmware OTA updates from the app
- ⏳ Usage insights for tuning bindings

See also the repository-level [`ROADMAP.md`](https://github.com/andrew-malitchuk/prompt-knob-kmp/blob/main/ROADMAP.md).
