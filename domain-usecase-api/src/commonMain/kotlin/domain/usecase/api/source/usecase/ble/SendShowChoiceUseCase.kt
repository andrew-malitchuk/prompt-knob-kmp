package domain.usecase.api.source.usecase.ble

import domain.usecase.api.source.monad.Optional

/**
 * Sends `BLE_OP_SHOW_CHOICE` (0x06) with the given options to the connected device.
 *
 * The device displays a scrollable list; the user rotates to navigate and taps to confirm.
 * Pair with [AwaitCommandSelectedUseCase] to receive the result: cmd_id > 0 means
 * option[cmd_id - 1] was selected, cmd_id == 0 means the user cancelled.
 */
public interface SendShowChoiceUseCase {
    public suspend operator fun invoke(options: List<String>): Optional
}
