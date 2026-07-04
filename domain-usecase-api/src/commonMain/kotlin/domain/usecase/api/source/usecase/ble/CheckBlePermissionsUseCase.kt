package domain.usecase.api.source.usecase.ble

/** Returns true if all required BLE permissions are currently granted. */
public interface CheckBlePermissionsUseCase {
    public suspend operator fun invoke(): Result<Boolean>
}
