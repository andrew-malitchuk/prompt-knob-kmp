package domain.usecase.api.source.usecase.ble

import domain.core.source.model.ClaudeState
import domain.usecase.api.source.monad.Optional

/**
 * Sends `BLE_OP_CLAUDE_STATE` (0x08) — updates the Claude Code session state on the device.
 *
 * Called by the Claude Mode hook server when it receives a POST /state request from
 * a Claude Code hook. The device shows the Claude screen with the corresponding indicator.
 */
public interface SendClaudeStateUseCase {
    public suspend operator fun invoke(state: ClaudeState): Optional
}
