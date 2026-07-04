package domain.usecase.api.source.usecase.ble

import domain.usecase.api.source.monad.Optional

/** Sends `BLE_OP_SHOW_APPROVAL` (0x05) to the connected device. */
public interface SendShowApprovalUseCase {
    public suspend operator fun invoke(): Optional
}
