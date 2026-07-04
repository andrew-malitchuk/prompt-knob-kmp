# Hardware Specification

PromptKnob is a self-contained ESP32-based rotary controller. It has its own
display, LEDs and haptic feedback, and connects to the companion app over
Bluetooth Low Energy.

## Device specification

| Component | Details |
|-----------|---------|
| MCU | ESP32 (Xtensa LX6 dual-core, 240 MHz) |
| Wireless | Bluetooth 4.2 / BLE |
| Storage | 4 MB flash; NVS partition for command tree |
| Input | Rotary encoder — CW/CCW rotation, tap, swipe |
| Output | Display, RGB LEDs, haptic motor |
| USB | Micro-USB / USB-C via onboard USB-to-UART bridge |

## Inputs

The rotary encoder is the only physical control, and every interaction maps to
a distinct gesture:

- **CW / CCW rotation** — step through lists, adjust values (for example volume
  or brightness).
- **Tap** — confirm the current selection or trigger the bound command.
- **Swipe** — reject, cancel or dismiss.

These gestures are what the app's interactive Claude Code tools rely on: a tap
approves, a swipe rejects, and rotation navigates a choice list.

## Outputs

- **Display** — shows the active screen: the command wheel, approval prompts,
  scrollable choice lists, notifications and the Claude working-state overlay.
- **RGB LEDs** — colour-coded status and notification feedback (for example the
  orange / green / red rings used by the Claude state overlay).
- **Haptic motor** — a physical pulse on notifications and confirmations, so you
  get feedback without looking at the screen.

## Connectivity

The USB port (via the onboard USB-to-UART bridge) is used for flashing firmware
and for the serial monitor — see [Flashing](flashing.md). During normal use the
device talks to the companion app exclusively over BLE.

> The hardware description above reflects the device targeted by the firmware.
> The firmware itself is maintained in a separate repository — see
> [Firmware Overview](overview.md).
