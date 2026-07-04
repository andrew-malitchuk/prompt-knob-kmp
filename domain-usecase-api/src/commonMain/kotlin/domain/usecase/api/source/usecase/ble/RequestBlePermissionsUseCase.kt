package domain.usecase.api.source.usecase.ble

/** Requests all required BLE permissions. Returns true if granted. */
public interface RequestBlePermissionsUseCase {
    public suspend operator fun invoke(): Result<Boolean>
}
