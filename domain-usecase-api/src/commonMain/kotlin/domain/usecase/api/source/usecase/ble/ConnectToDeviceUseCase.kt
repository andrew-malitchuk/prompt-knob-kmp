package domain.usecase.api.source.usecase.ble

import domain.usecase.api.source.monad.Optional

/** Connects to the BLE device at the given [address]. [name] is persisted for display. */
public interface ConnectToDeviceUseCase {
    public suspend operator fun invoke(address: String, name: String? = null): Optional
}
