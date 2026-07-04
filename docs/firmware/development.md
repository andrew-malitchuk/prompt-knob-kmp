# Firmware Development

This page describes the firmware development environment and general workflow.

> **The firmware source is external.** It lives in a separate repository —
> [github.com/andrew-malitchuk/prompt-knob-firmware](https://github.com/andrew-malitchuk/prompt-knob-firmware) —
> not in this Kotlin Multiplatform repo. This page documents the toolchain and
> workflow that are supportable from here; anything specific to the firmware
> source belongs in that repository. See [Firmware Overview](overview.md).

## Toolchain

The firmware is built with [PlatformIO](https://platformio.org/) targeting the
**ESP32** (Xtensa LX6). PlatformIO manages the platform, the compiler toolchain
and any library dependencies declared in the project's `platformio.ini`, so you
do not install the ESP32 toolchain by hand.

| Tool | Role |
|------|------|
| PlatformIO Core (`pio`) | Build, upload and monitor the firmware |
| ESP32 platform | Installed automatically by PlatformIO on first build |
| `platformio.ini` | Declares board, framework and library dependencies |

Install PlatformIO once with `pip install platformio`. See
[Getting Started](getting-started.md) for the full first-time setup.

## Everyday workflow

A typical edit / build / flash / observe loop:

```bash
# Compile only — fast feedback while editing
pio run

# Compile and flash to the connected device
pio run -t upload

# Watch boot logs and runtime output
pio device monitor

# Remove build artifacts if you hit a stale-build issue
pio run -t clean
```

## The app side of the contract

The device firmware and this companion app share the **YAMK** BLE protocol. When
you change on-device behaviour that touches the wire format, the corresponding
encoder/decoder on the app side lives in the `data-ble-api` / `data-ble-impl`
modules. Keep both sides in sync:

- **App → device:** command-tree sync, approval / choice / notify opcodes, and
  the Claude working-state overlay.
- **Device → app:** rotation, tap and swipe events, and command-selection
  responses.

For the protocol as implemented in this repo, see
[BLE / YAMK protocol](../development/ble-protocol.md).

## Testing changes end to end

1. Flash the firmware — `pio run -t upload`.
2. Run the companion app on macOS
   (`./gradlew :compose-application:runReleaseMacosArm64`) or Android.
3. Pair the device and exercise the feature you changed.
4. Use `pio device monitor` alongside the app to correlate on-device logs with
   app behaviour.
