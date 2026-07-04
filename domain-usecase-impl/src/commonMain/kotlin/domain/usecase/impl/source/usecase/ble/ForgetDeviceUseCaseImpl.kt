package domain.usecase.impl.source.usecase.ble

import domain.repository.api.source.repository.BleRepository
import domain.repository.api.source.repository.CommandHistoryRepository
import domain.repository.api.source.repository.CommandRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.ble.ForgetDeviceUseCase

internal class ForgetDeviceUseCaseImpl(
    private val bleRepository: BleRepository,
    private val commandRepository: CommandRepository,
    private val commandHistoryRepository: CommandHistoryRepository,
) : ForgetDeviceUseCase {

    override suspend fun invoke(): Optional = runCatching {
        bleRepository.markSkipAutoReconnect()
        // 1. Clear local commands first so the in-memory set is authoritative.
        commandRepository.deleteAll().getOrThrow()
        // 2. Send CLEAR_COMMANDS (0x04) — firmware erases NVS atomically and responds
        //    with SYNC_ACK. Lenient: proceed with local wipe even if the device is gone.
        bleRepository.clearCommands()
            .onFailure { /* best-effort: device may be gone, proceed with local wipe */ }
        // 3. Clear command execution history so no trace of this device remains locally.
        commandHistoryRepository.clearAll().getOrThrow()
        // 4. Clear the persisted last-device address.
        bleRepository.clearLastConnectedDevice().getOrThrow()
        // 5. Disconnect transport.
        bleRepository.disconnect() // lenient — device may already be disconnected
    }
}
