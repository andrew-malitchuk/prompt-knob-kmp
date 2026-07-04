# Firmware: Getting Started

This guide gets you from an empty machine to your first firmware build. The
firmware is maintained in a **separate repository** and built with PlatformIO
for the ESP32 — see [Firmware Overview](overview.md) for how it relates to the
companion app.

## Prerequisites

- Python 3 with `pip`
- A PromptKnob device and a USB cable (data-capable, not charge-only)
- Git

## 1. Install PlatformIO

PlatformIO Core ships as a Python package and provides the `pio` command-line
tool used for building, flashing and monitoring:

```bash
pip install platformio
```

Verify the install:

```bash
pio --version
```

## 2. Clone the firmware repository

```bash
git clone https://github.com/andrew-malitchuk/prompt-knob-firmware.git
cd prompt-knob-firmware
```

## 3. Build the firmware

From the firmware repository root, run a build. PlatformIO reads the project's
`platformio.ini`, downloads the ESP32 platform and toolchain on first run, and
compiles the sources:

```bash
pio run
```

The first build takes longer while PlatformIO fetches the platform and any
declared libraries. Subsequent builds are incremental.

## 4. Flash it to the device

With the device connected over USB, build and upload in one step:

```bash
pio run -t upload
```

PlatformIO auto-detects the serial port. If it cannot, or if the upload fails,
see [Flashing](flashing.md) for driver setup and manual port selection.

## 5. Watch the serial output

Open the serial monitor to see boot logs and confirm the firmware is running:

```bash
pio device monitor
```

Press `Ctrl+C` to exit.

## Next steps

- [Development environment](development.md) — the PlatformIO toolchain and
  general workflow.
- [Flashing](flashing.md) — full upload, monitor and troubleshooting reference.
- Once the firmware is running, pair the device with the companion app:
  [Android](../user-guide/getting-started-android.md) ·
  [macOS](../user-guide/getting-started-macos.md).
