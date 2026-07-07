# [0.0.1] - 2026-07-07

First public release of PromptKnob — a Kotlin Multiplatform companion app for the
PromptKnob BLE rotary knob (ESP32). Targets Android and macOS, with the Compose module
also building iOS and Desktop/JVM.

## Added
- BLE scan, connect, and YAMK wire-protocol codec (SEQ / ACK / checksum) via Kable
- Android foreground service for command logging and connection keep-alive
- Command execution engine with per-platform executors (media, system, shell, Google Assistant prompts)
- Room database for the command tree and execution history
- Runtime, preference, and repository layers (in-memory session / command-cache / move-detector state)
- Feature screens: devices, device, command, preset, and settings
- Domain layer: core models, `Failure` type, repository contracts, and use cases
- macOS ↔ Claude Code integration: MCP server (port 7474) and Claude Code hook server (port 7777)
- UI kit, icon set, navigation (Navigation 3 + custom NavigationHost), styling, and localisation
- Kinetic Mono design system with core UI kit and styleguide
- KMP app entry points for Android, iOS, macOS, and Desktop
- Gradle 9.1 build system with four convention plugins (application, feature, library, di)

## Changed
- Restructured convention plugins into a `source/convention` layout
- Various cold-start, recomposition, and allocation performance improvements

## Fixed
- Crash on start, navigation, null-pointer, state-handling, layout, and memory-leak fixes

## Docs
- MkDocs site, per-module README/CLAUDE.md, project README gallery, and ROADMAP
