package domain.usecase.api.source.usecase.ble

import domain.usecase.api.source.monad.Optional

/**
 * Sends `BLE_OP_NOTIFY` (0x07) — a fire-and-forget notification to the connected device.
 *
 * The device briefly displays [message] with optional haptic/LED feedback. No response
 * is expected; the call returns as soon as the BLE write completes.
 */
public interface SendNotifyUseCase {
    public suspend operator fun invoke(message: String, level: Int = 0): Optional
}
