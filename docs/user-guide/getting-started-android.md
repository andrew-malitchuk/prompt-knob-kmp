# Getting Started on Android

This guide walks you through installing the PromptKnob Android app, granting the
permissions it needs, pairing a knob, and importing a preset so your first
commands work right away.

You will need a PromptKnob device running current firmware. If the device is not
yet flashed, see [Firmware: Getting Started](../firmware/getting-started.md).

## 1. Install the app

Build and install the debug APK from the project root:

```bash
./gradlew :aos-application:assembleDebug
```

Install the resulting APK on your device (via Android Studio, or
`adb install`), then launch **PromptKnob**. On first launch you'll go through a
short onboarding flow.

## 2. Grant Bluetooth permissions

PromptKnob connects to the knob over Bluetooth Low Energy, so Android will ask
for Bluetooth (and, on some versions, nearby-devices / location) access the
first time you scan.

- When prompted, **allow** the Bluetooth / nearby-devices permission.
- Make sure Bluetooth is turned on.
- If you dismissed a prompt, you can re-grant it from **Settings → Apps →
  PromptKnob → Permissions**.

## 3. Enable the accessibility service (for Assistant presets)

The Android presets issue **Google Assistant** voice commands through the
`AssistantPromptExecutor`. This relies on an accessibility service —
**AssistantRobotService** — that drives the Assistant UI on your behalf. It must
be enabled once:

1. Go to **Settings → Accessibility → Installed apps → PromptKnob**.
2. Turn the service **on** and confirm the system dialog.

Without this service enabled, prompt-type (Google Assistant) commands will not
run. Non-Assistant actions do not require it.

## 4. Scan and pair a knob

1. Open the app and go to **Devices**.
2. Tap **Scan** — nearby knobs appear in the list.
3. Tap your device to connect.
4. Wait for the status to change to **Connected**.

Once connected, the app can sync commands to the device and receive its
rotation, tap and swipe events.

## 5. Import an Android preset

The app ships with ready-made Android preset packs (all use Google Assistant
prompts):

| Preset | Commands | Type |
|--------|----------|------|
| **Commute** | Navigate home · Show traffic to work | PROMPT (Google Assistant) |
| **Music** | Play my liked songs on YouTube Music · Play lo-fi playlist | PROMPT |
| **Daily** | What's on my calendar today? · Turn off the lights | PROMPT |

To import one:

1. Open **Presets**.
2. Tap a gallery card to import it.
3. The command tree syncs to the device immediately.

## 6. Bind commands

Imported preset commands are bound to the knob's gestures automatically. To
customise:

1. Open **Commands** to see the current command tree.
2. Edit or add a command and choose what it does.
3. The updated tree syncs to the connected device over BLE — turn, tap or swipe
   the knob to trigger the bound actions.

> Prompt-type commands require **AssistantRobotService** to remain enabled
> (step 3). If a command silently does nothing, check that the service is still
> on.

## Next steps

- Create your own presets and export/import them as JSON.
- On macOS, PromptKnob doubles as a control surface for Claude Code — see
  [MCP Setup](mcp-setup.md).
