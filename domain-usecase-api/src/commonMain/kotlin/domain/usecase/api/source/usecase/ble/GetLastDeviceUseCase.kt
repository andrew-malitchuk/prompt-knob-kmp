package domain.usecase.api.source.usecase.ble

/**
 * Returns the address of the last successfully connected BLE device, or null if none.
 *
 * On Android this is a MAC address string (e.g. "AA:BB:CC:DD:EE:FF").
 * On iOS/macOS this is a CoreBluetooth peripheral UUID string.
 */
public interface GetLastDeviceUseCase {
    public suspend operator fun invoke(): Result<String?>
}
