# data-ble-impl — agent guide

Concrete BLE layer. Implements `data-ble-api` per platform and exposes one Koin module. Applies the `dev.prompt.knob.io.convention.library` convention plugin.

## Responsibility

- Shared (`commonMain`): `YamkCodecImpl`, `YamkProtocolImpl`, `PromptKnobBle` device constants, and the Koin module `dataBleImplModule`.
- Per-platform (`androidMain`/`iosMain`/`macosMain`/`desktopMain`): `BleScannerImpl`, `BleConnectionImpl`, `BleServiceControllerImpl`, `BlePermissionCheckerImpl`.
- Android also has `BleConnectionService` (foreground service) and `BlePairingReceiver`.

## Rules

- API/Impl split: this is the **impl** side. Implementation classes are `internal`; only `dataBleImplModule` and Android framework entry points (`BleConnectionService`) are `public`. Add new contracts to `data-ble-api`, then implement here.
- Platform wiring goes through `provideBleAdapter()` — an `internal expect fun Module.provideBleAdapter()` with one `actual` per platform (`BleProvider.<platform>.kt`). Register new per-platform singletons there, not in `commonMain`.
- `explicitApi` is enforced.

## Gotchas

- iOS and macOS share the same CoreBluetooth-via-Kable code path; the Gradle script adds `kable-core` to every Apple `*Main` source set — mirror that if you add another Apple target.
- Desktop backends are stubs; don't assume a real scan/connect there.
- `BleConnectionService` resolves `BleConnection` via Koin (`by inject()`), so it must be started after Koin init and declared in the Android app manifest with a foreground-service type.
- Keep `PromptKnobBle.SERVICE_UUID` / `DEVICE_NAME` in sync with the firmware; scan filtering relies on them.
