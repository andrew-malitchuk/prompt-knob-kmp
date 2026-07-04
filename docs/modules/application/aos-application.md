# aos-application

The Android application module. It is the thinnest possible Android host: it bootstraps the Koin dependency graph, hosts the shared Compose UI inside a single activity, and exposes a Quick Settings tile for toggling the BLE connection. All product logic lives in the shared `compose-application` and downstream modules — this module only supplies the Android runtime shell.

Package: `dev.prompt.knob.io`.

## Responsibilities

- Create the Koin graph once per process via the custom `Application` subclass.
- Render the shared `App()` composable edge-to-edge from a single activity.
- Provide a Quick Settings tile that connects to / disconnects from the last known knob without opening the app.

## Key types

| Type | Package | Role |
|------|---------|------|
| `PromptKnobApplication` | `dev.prompt.knob.io.core` | `Application` subclass. Calls `initKoin { androidLogger(); androidContext(this) }` in `onCreate()` before any injection occurs. |
| `MainActivity` | `dev.prompt.knob.io.source.activity` | `ComponentActivity` entry point. Calls `enableEdgeToEdge()` then `setContent { App() }`. Contains no business logic. |
| `QuickCommandTileService` | `dev.prompt.knob.io.source.tile` | `TileService` (API 24+) that toggles the BLE connection to the last known device. |

### PromptKnobApplication

Declared in the manifest as `android:name`. It is the first component created in the process, so it registers the Android `Context` with Koin before any Activity or Service requests an injection.

```kotlin
override fun onCreate() {
    super.onCreate()
    initKoin {
        androidLogger()
        androidContext(this@PromptKnobApplication)
    }
}
```

### MainActivity

The single launcher activity. It enables edge-to-edge rendering (before `super.onCreate()`) and delegates the entire UI to the shared `App()` composable from `compose-application`.

### QuickCommandTileService

A Quick Settings tile that mirrors the current `ConnectionStateModel` and toggles it on tap. It injects use cases and the BLE service controller directly from Koin:

| Dependency | Package |
|------------|---------|
| `ObserveConnectionStateUseCase` | `domain.usecase.api.source.usecase.ble` |
| `ConnectToDeviceUseCase` | `domain.usecase.api.source.usecase.ble` |
| `DisconnectDeviceUseCase` | `domain.usecase.api.source.usecase.ble` |
| `GetLastDeviceUseCase` | `domain.usecase.api.source.usecase.ble` |
| `BleServiceController` | `data.ble.api.source.datasource` |

Behavior:

- **Connected / Connecting** &rarr; tile shows `STATE_ACTIVE`; tapping calls `disconnectDevice()` and `bleServiceController.stopConnectionService()`.
- **Disconnected / Disconnecting** &rarr; tile shows `STATE_INACTIVE`; tapping reads the last device address via `getLastDevice()` and, if present, calls `connectToDevice(address)`. If no last device is stored, the tap is silently ignored.

A fresh `CoroutineScope(SupervisorJob() + Dispatchers.Main)` is created in `onStartListening()` and cancelled in `onStopListening()` so no work leaks between tile-panel open/close cycles. On Android Q+ the tile subtitle is set from the `tile_connected` / `tile_connecting` / `tile_disconnected` string resources.

## Resources

| Resource | Purpose |
|----------|---------|
| `res/values/strings.xml` | `app_name`, `tile_label`, and the three tile subtitle strings. |
| `res/drawable/ic_tile_knob.xml` | Vector icon for the Quick Settings tile. |
| `res/xml/assistant_accessibility_config.xml` | Accessibility-service config scoped to `com.google.android.googlequicksearchbox`. |
| `res/xml/device_admin_receiver.xml` | Device-admin policy declaring `force-lock`. |
| `res/mipmap-*` / `ic_launcher_*` | Adaptive launcher icon assets. |

> The `assistant_accessibility_config.xml` and `device_admin_receiver.xml` files are present in the resource set as configuration scaffolding; the current `AndroidManifest.xml` registers only the launcher activity.

## Manifest

```xml
<application android:name="dev.prompt.knob.io.core.PromptKnobApplication" ...>
    <activity
        android:exported="true"
        android:name="dev.prompt.knob.io.source.activity.MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

## Dependencies

- [`compose-application`](compose-application.md) — provides `App()` and `initKoin`.
- `domain-usecase-api` and `data-ble-api` — consumed directly by the tile service.

## See also

- [compose-application](compose-application.md) — the shared KMP entry point and Koin wiring.
