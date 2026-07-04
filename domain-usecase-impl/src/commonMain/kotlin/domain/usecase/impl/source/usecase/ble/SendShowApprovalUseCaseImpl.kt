package domain.usecase.impl.source.usecase.ble

import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.ble.SendShowApprovalUseCase

internal class SendShowApprovalUseCaseImpl(
    private val bleRepository: BleRepository,
) : SendShowApprovalUseCase {
    override suspend fun invoke(): Optional = bleRepository.sendShowApproval()
}
