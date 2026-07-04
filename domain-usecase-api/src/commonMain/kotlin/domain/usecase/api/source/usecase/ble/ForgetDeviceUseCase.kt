package domain.usecase.api.source.usecase.ble

import domain.usecase.api.source.monad.Optional

/**
 * Forgets the current device: disconnects BLE, clears the last-device preference,
 * and deletes all locally stored commands.
 */
public interface ForgetDeviceUseCase {
    public suspend operator fun invoke(): Optional
}
