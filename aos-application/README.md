# aos-application

Android application entry point for PromptKnob. Hosts the single Activity, bootstraps Koin with the Android `Context`, and provides Android-only integration surfaces (Quick Settings tile, plus accessibility/device-admin XML configs).

## Public API

| Class | Package | Description |
|-------|---------|-------------|
| `PromptKnobApplication` | `dev.prompt.knob.io.core` | `Application` subclass; calls `initKoin { androidLogger(); androidContext(this) }` on startup |
| `MainActivity` | `dev.prompt.knob.io.source.activity` | Single-activity host; enables edge-to-edge then `setContent { App() }` |
| `QuickCommandTileService` | `dev.prompt.knob.io.source.tile` | Quick Settings `TileService` that toggles the BLE connection to the last known device, reflecting `ConnectionStateModel` as active/inactive |

## Android integration assets

- `res/xml/assistant_accessibility_config.xml` — accessibility service config (scoped to Google Quick Search Box).
- `res/xml/device_admin_receiver.xml` — device-admin policy (`force-lock`).
- `res/drawable/ic_tile_knob.xml` — tile icon; `tile_*` strings in `strings.xml`.

Manifest (`dev.prompt.knob.io.core.PromptKnobApplication` + `dev.prompt.knob.io.source.activity.MainActivity` as the launcher activity) is the current wiring; the tile/accessibility/device-admin assets are present in source for their respective services.

## Dependencies

`compose-application` (shared `App()` + `initKoin`), `data-ble-api`, `domain-core`, `domain-usecase-api`, `androidx.activity.compose`, `koin.android`.

## Platform notes

Android only. Applies the `androidApplication`, `composeMultiplatform`, and `composeCompiler` Gradle plugins directly (not a `convention` plugin). Signing config is loaded from `configure/secrets/signing.properties` when present.
