# data-ble-api — agent guide

BLE contract module. Holds **interfaces, models, and protocol constants only** — no implementation. Applies the `dev.prompt.knob.io.convention.library` convention plugin.

## Responsibility

- Data-source interfaces: `BleScanner`, `BleConnection`, `BlePermissionChecker`, `BleServiceController`.
- YAMK wire protocol: `YamkProtocol`, `YamkCodec`, `YamkFrame`, and the opcode/status objects (`PhoneToDeviceOpcode`, `DeviceToPhoneOpcode`, `OtaOpcode`, `SyncAckStatus`).
- BLE resources/models under `core/resource` and `source/resource`.

## Rules

- API/Impl split: this is the **api** side. Every interface here is implemented in `data-ble-impl`. Never add concrete classes, Koin modules, or platform (`androidMain`/`iosMain`) code here — it is `commonMain`-only.
- `explicitApi` is enforced — mark every declaration `public`/`internal`.
- All models implement `data.core.source.resource.Resource`.
- Depends on `domain-core` (`api`) and `data-core` (`implementation`); do **not** add BLE library deps (Kable etc.) — those belong in impl.

## Gotchas

- Opcodes are `UByte` constants — keep hex values byte-for-byte aligned with the ESP32 firmware; a wrong opcode silently breaks sync.
- `YamkFrame` overrides `equals`/`hashCode` because it wraps a `ByteArray` — preserve that if you edit the frame.
- `syncCommands` returns `SyncAckResult`; map non-`Ok` status codes via `SyncAckStatus`, don't swallow them.
