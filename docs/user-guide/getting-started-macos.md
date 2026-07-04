# Getting Started on macOS

This guide walks you through running the PromptKnob macOS app, pairing a knob,
and importing a preset so your first commands work right away.

You will need a PromptKnob device running current firmware. If the device is not
yet flashed, see [Firmware: Getting Started](../firmware/getting-started.md).

## 1. Run the macOS app

From the project root, run the native macOS build:

```bash
./gradlew :compose-application:runReleaseMacosArm64
```

The app launches as a native window. Keep it running while you use the knob —
BLE communication and any Claude Code integration depend on the app being open.

## 2. Pair a knob

1. Open **Devices**.
2. Tap **Scan** — nearby knobs appear in the scan list.
3. Tap your device to connect.
4. Wait for the status to change to **Connected**.

If prompted by macOS for Bluetooth access, allow it. Once connected, the app can
sync commands to the device and receive its rotation, tap and swipe events.

## 3. Import a macOS preset

The app ships with ready-made macOS preset packs:

| Preset | Commands | Type |
|--------|----------|------|
| **Media** | Play/Pause · Next Track · Prev Track · Volume Up · Volume Down | SYSTEM |
| **Focus** | Mic Toggle · Mute · Lock Screen · Stay Awake | SYSTEM |
| **Display** | Brightness Up · Brightness Down · Mission Control · Show Desktop | SYSTEM |
| **Terminal** | Open Terminal · Open VS Code · Say Done · Screenshot | SHELL + SYSTEM |

To import one:

1. Open **Presets**.
2. Tap a gallery card to import it.
3. The command tree syncs to the device immediately.

You can turn, tap or swipe the knob to trigger the bound actions — for example
rotate to change volume or brightness, tap to play/pause.

## 4. Customise commands

1. Open **Commands** to see the current command tree.
2. Edit or add commands. macOS supports **SYSTEM** actions (media, volume,
   brightness, lock, screenshot, and more) and **SHELL** commands (run a shell
   command, open an app).
3. The updated tree syncs to the connected device over BLE.

## Next steps

On macOS, PromptKnob also works as a human-in-the-loop control surface for
Claude Code — approve risky operations, pick between options, and see Claude's
working state on the device. Set it up in [MCP Setup](mcp-setup.md).
