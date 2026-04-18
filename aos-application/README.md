# aos-application

> Android application module — Activity and Application class.

## Responsibility

Android-specific entry point. Hosts `MainActivity` (single-activity architecture with edge-to-edge rendering) and bootstraps Koin DI with Android context via `PromptKnobApplication`.

## Dependencies

| Depends on | Purpose |
|---|---|
| `compose-application` | Shared `App()` composable and `initKoin()` |

## Public API

| Class | Description |
|---|---|
| `MainActivity` | Single-activity entry point with edge-to-edge setup |
| `PromptKnobApplication` | Application subclass — Koin bootstrap |

### MainActivity

- Enables edge-to-edge rendering
- Delegates UI to shared `App()` composable

### PromptKnobApplication

- Calls `initKoin { androidContext(this) }` on startup

## Usage

```xml
<!-- AndroidManifest.xml -->
<application android:name=".PromptKnobApplication">
    <activity android:name=".MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

## Testing

```bash
./gradlew :aos-application:test
```
