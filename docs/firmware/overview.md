# Firmware Overview

PromptKnob is a physical BLE rotary controller built around an **ESP32**
microcontroller. This documentation set covers the companion app (this repo),
but the app is only half of the system — the other half is the firmware that
runs on the device itself.

## The firmware lives in a separate repository

The firmware is **not** part of this Kotlin Multiplatform repo. It is
maintained on its own, built with [PlatformIO](https://platformio.org/) for the
ESP32:

- **Repository:** [github.com/andrew-malitchuk/prompt-knob-firmware](https://github.com/andrew-malitchuk/prompt-knob-firmware)
- **Toolchain:** PlatformIO (ESP32 target)

Keeping the firmware separate lets the hardware and the companion app version
independently. You only need to touch the firmware repo when you are flashing a
device or hacking on the on-device behaviour — day-to-day app development does
not require it.

## How the app and firmware talk

The companion app communicates with the device over **Bluetooth Low Energy
(BLE 4.2)** using the **YAMK** wire protocol. YAMK is the shared contract
between the two codebases:

```
Companion app (this repo)  ──BLE / YAMK──▶  PromptKnob firmware (separate repo)
        │                                            │
   encodes commands,                          decodes opcodes,
   preset command trees,                      drives display / LEDs /
   Claude state overlays                      haptics, reports rotation,
        ▲                                      taps and swipes
        └──────────────BLE / YAMK──────────────┘
```

At a high level:

- The app **encodes** actions (show an approval screen, show a choice list,
  push a notification, update the Claude working-state overlay) and **syncs the
  command tree** down to the device.
- The firmware **decodes** those opcodes, drives the display, RGB LEDs and
  haptic motor, and reports physical input (rotary CW/CCW, tap, swipe) back to
  the app.

The YAMK codec on the app side lives in the `data-ble-api` / `data-ble-impl`
modules. For the wire-format details as implemented in this repo, see
[BLE / YAMK protocol](../development/ble-protocol.md).

## Where to go next

| Guide | Purpose |
|-------|---------|
| [Hardware specification](hardware.md) | The device's MCU, radio, storage and I/O |
| [Getting started](getting-started.md) | Clone the firmware repo and do a first build |
| [Development environment](development.md) | PlatformIO toolchain and workflow |
| [Flashing](flashing.md) | Upload firmware, use the serial monitor, troubleshoot |
| [On-device storage](storage.md) | The NVS partition and the synced command tree |
