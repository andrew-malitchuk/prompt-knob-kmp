package domain.usecase.api.source.usecase.ble

import domain.usecase.api.source.monad.Optional

/** Disconnects from the currently connected BLE device. */
public interface DisconnectDeviceUseCase {
    public suspend operator fun invoke(): Optional
}
