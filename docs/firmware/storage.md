# On-Device Storage

The PromptKnob device keeps a small amount of persistent state in flash so it
can behave correctly between power cycles. This page describes that storage
conservatively — the authoritative implementation lives in the
[firmware repository](https://github.com/andrew-malitchuk/prompt-knob-firmware).

## Flash and the NVS partition

The ESP32 has **4 MB of flash**. A portion of it is set aside as an **NVS
(Non-Volatile Storage) partition**, which the firmware uses to persist the
**command tree** — the set of commands bound to the knob's gestures.

| Aspect | Detail |
|--------|--------|
| Total flash | 4 MB |
| Persistent store | NVS partition |
| What's stored | The command tree synced from the companion app |

## The command tree is synced from the app

You do **not** author the command tree on the device. It is created and managed
in the companion app — by importing a preset or editing commands — and then
**synced down to the device over BLE** using the YAMK protocol. The device
stores the received tree in NVS so it survives a reboot.

```
Companion app                         PromptKnob (ESP32)
  edit / import preset                       │
  command tree            ──BLE / YAMK──▶     write to NVS partition
                                             │
  (next power-up)                            read tree from NVS,
                                             render the command wheel
```

Practical consequences:

- **Presets are applied from the app.** Open **Presets** in the app, import a
  pack, and the command tree syncs to the device immediately. See the
  [Android](../user-guide/getting-started-android.md) and
  [macOS](../user-guide/getting-started-macos.md) guides.
- **The device remembers the last synced tree.** After a reboot it renders the
  command wheel from what is stored in NVS, without needing the app to be
  connected.
- **Re-syncing replaces the stored tree.** Importing a different preset or
  editing commands and syncing again overwrites what the device holds.

## Transient screens are not persisted

Interactive screens driven by Claude Code — approval prompts, choice lists,
notifications and the working-state overlay — are **live** requests sent over
BLE while the app is connected. They are not stored on the device and disappear
when dismissed, timed out, or when the session ends. Only the command tree is
persisted in NVS.

> The partition layout and exact key/value schema are defined by the firmware
> and may change. Treat the app as the source of truth for command content; the
> device is a cache of the last synced tree.
