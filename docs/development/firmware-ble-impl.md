# Firmware BLE Implementation (App Side)

This page describes how PromptKnob's **app-side** BLE layer talks to the PromptKnob firmware: scanning for the device, connecting over GATT, the characteristics it uses, and the Android foreground service that keeps the connection alive. The firmware itself is developed in a separate repository, [prompt-knob-firmware](https://github.com/andrew-malitchuk/prompt-knob-firmware).

For the framing and opcodes that flow over these characteristics, see [YAMK BLE protocol](ble-protocol.md).

All classes below live in `data-ble-impl`. The interfaces they implement live in `data-ble-api`.

## Interfaces and implementations

| Concern | Interface (`data-ble-api`) | Implementation (`data-ble-impl`) |
|---------|----------------------------|----------------------------------|
| Scanning | `BleScanner` | `BleScannerImpl` (androidMain) |
| Connection / GATT | `BleConnection` | `BleConnectionImpl` (androidMain) |
| Foreground service control | `BleServiceController` | `BleServiceControllerImpl` (androidMain) |
| Android service | — | `BleConnectionService` (androidMain) |
| Constants (UUIDs, name) | — | `PromptKnobBle` (object) |

The BLE stack on Android is built on [Kable](https://github.com/JuulLabs/kable) (`com.juul.kable`).

## Device identity

The firmware advertises with a fixed name and manufacturer data, defined in `PromptKnobBle`:

- Advertised device name: `"prompt-knob"`.
- Manufacturer ID: `0xFFFF`, magic bytes `[0xAB]`.

`BleScannerImpl` uses these to filter advertisements down to PromptKnob devices only.

## Scanning

`BleScannerImpl` wraps a Kable `Scanner`:

- Runs in `SCAN_MODE_LOW_LATENCY` (needed to reliably receive scan responses).
- Filters on the device name `prompt-knob` and the manufacturer data (`0xFFFF` / `0xAB`).
- Emits already-bonded devices first, then continues emitting newly discovered devices as they appear.

Each discovered device is surfaced as a `BleDeviceResource`:

```kotlin
BleDeviceResource(
    name: String?,   // advertised name (or a fallback)
    address: String, // MAC address on Android
    rssi: Int,       // signal strength in dBm
)
```

## Connection flow

`BleConnectionImpl.connect(address)` connects by MAC address:

1. Acquire an internal `connectMutex` so two concurrent connects can't race.
2. Move the connection state to `Connecting`.
3. Create a Kable `Peripheral` for the address.
4. Register the characteristic descriptors it will use (see below).
5. `peripheral.connect()`.
6. Negotiate MTU with `requestMtu(512)`.
7. Validate the GATT service cache. If it looks stale (the OTA service is missing, or `OTA_DATA` lacks the Write property), it refreshes the cache (`BluetoothGatt.refresh()` via reflection) and reconnects so service discovery runs fresh.
8. Observe peripheral state changes and emit `Connected`.

Connection state is exposed through the `BleConnection.state` flow and surfaced to the domain layer as `BleConnectionStateResource`.

### MTU and write sizing

MTU is negotiated during connect (BLE starts at 23 bytes and typically negotiates up to 512 on modern Android). The usable payload per write is `mtu - 3` (GATT overhead). For OTA data writes the size is computed as:

```kotlin
val mtu = (kablePeripheral as? AndroidPeripheral)?.mtu?.value ?: 23
val otaWriteSize = (mtu - 3).coerceIn(20, 512)
```

## GATT services and characteristics

UUIDs are hardcoded in `BleConnectionImpl`. There are three services:

### HID / command service — `6e400001-b5a3-f393-e0a9-e50e24dcca9e`

| Characteristic | UUID | Properties | Use |
|----------------|------|------------|-----|
| CMD | `6e400003-b5a3-f393-e0a9-e50e24dcca9e` | Notify | Device → phone frames (e.g. `CMD_SELECTED`) |
| SYNC | `6e400002-b5a3-f393-e0a9-e50e24dcca9e` | Write | Phone → device frames (sync, commands, etc.) |

Writes to the SYNC characteristic use `WriteType.WithResponse` (the sync flow relies on the device acknowledging).

### Device Information Service — `0000180a-0000-1000-8000-00805f9b34fb`

| Characteristic | UUID | Properties | Use |
|----------------|------|------------|-----|
| Firmware Revision | `00002a26-0000-1000-8000-00805f9b34fb` | Read | Firmware version (falls back to the `VERSION_INFO` notify) |

### OTA service — `6e410001-b5a3-f393-e0a9-e50e24dcca9e`

| Characteristic | UUID | Properties | Use |
|----------------|------|------------|-----|
| OTA_CTRL | `6e410002-b5a3-f393-e0a9-e50e24dcca9e` | Write-with-response | OTA control opcodes (`OTA_BEGIN`/`OTA_END`/`OTA_ABORT`) |
| OTA_DATA | `6e410003-b5a3-f393-e0a9-e50e24dcca9e` | Write-with-response | Firmware image chunks |
| OTA_STATUS | `6e410004-b5a3-f393-e0a9-e50e24dcca9e` | Notify | OTA status (`OTA_READY`/`OTA_PROGRESS`/`OTA_DONE`/`OTA_ERROR`) |

## Android foreground service

On Android the connection is owned by `BleConnectionService`, a `Service` that keeps the BLE link alive when the app is backgrounded and reconnects automatically. It is started and stopped through `BleServiceControllerImpl` (which implements the `BleServiceController` interface).

The service is controlled by intents:

- `ACTION_CONNECT` (`"data.ble.impl.action.CONNECT"`) — connect to the device in the `EXTRA_DEVICE_ADDRESS` extra.
- `ACTION_DISCONNECT` (`"data.ble.impl.action.DISCONNECT"`) — stop and disconnect.

On start it calls `startForeground(NOTIFICATION_ID = 1001, …)` with a persistent notification:

- Channel ID `"ble_connection"`, name `"PromptKnob BLE"`, importance `IMPORTANCE_LOW`.
- Icon `android.R.drawable.stat_notify_sync`, title `"PromptKnob"`.
- The content text tracks connection state — for example `"Connected to PromptKnob"`, `"Connecting to PromptKnob…"`, `"Disconnecting…"`, `"PromptKnob disconnected"`.

### Auto-reconnect

While the service owns the connection it observes `bleConnection.state` and, on disconnect, retries with exponential backoff:

- Initial delay `RECONNECT_INITIAL_DELAY_MS = 2_000` ms.
- Doubles each attempt (`delay = (delay * 2).coerceAtMost(30_000)`), capped at `RECONNECT_MAX_DELAY_MS = 30_000` ms.

## Dependency injection

The BLE Koin module is `dataBleImplModule`
(`data-ble-impl/.../di/DataBleImplModule.kt`). It registers the common codec/protocol singletons:

```kotlin
public val dataBleImplModule: Module = module {
    provideBleAdapter()                                  // platform-specific bindings
    singleOf(::YamkCodecImpl) bind YamkCodec::class
    single { YamkProtocolImpl(connection = get(), codec = get()) } bind YamkProtocol::class
}
```

`provideBleAdapter()` is an `expect`/`actual` extension on `Module`. The Android actual (`BleProvider.android.kt`) binds the platform pieces:

```kotlin
internal actual fun Module.provideBleAdapter() {
    single<BleScanner> { BleScannerImpl(androidContext()) }
    single<BleConnection> { BleConnectionImpl() }
    single<BlePermissionChecker> { BlePermissionCheckerImpl(androidContext()) }
    single<BleServiceController> { BleServiceControllerImpl(androidContext()) }
}
```

`dataBleImplModule` is wired into the app graph in `initKoin()` — see the [architecture overview](../architecture/overview.md).

## Related pages

- [YAMK BLE protocol](ble-protocol.md) — frames, opcodes, checksum, sync.
- [Architecture overview](../architecture/overview.md)
