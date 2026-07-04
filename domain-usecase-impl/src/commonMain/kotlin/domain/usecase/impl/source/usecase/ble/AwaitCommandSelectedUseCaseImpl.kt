package domain.usecase.impl.source.usecase.ble

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.usecase.ble.AwaitCommandSelectedUseCase
import domain.usecase.impl.core.resultLauncher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

internal class AwaitCommandSelectedUseCaseImpl(
    private val bleRepository: BleRepository,
) : AwaitCommandSelectedUseCase {
    override suspend fun invoke(timeoutMs: Long): Result<Int?> =
        resultLauncher(errorMapper = { Failure.Technical.Platform(it) }) {
            withTimeoutOrNull(timeoutMs) {
                bleRepository.observeCommandSelectedRaw().first()
            }
        }
}
