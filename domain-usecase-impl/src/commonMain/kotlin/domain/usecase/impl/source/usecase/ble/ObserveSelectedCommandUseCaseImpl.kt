package domain.usecase.impl.source.usecase.ble

import domain.core.source.model.CommandNodeModel
import domain.repository.api.source.repository.BleRepository
import domain.usecase.api.source.usecase.ble.ObserveSelectedCommandUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveSelectedCommandUseCaseImpl(
    private val bleRepository: BleRepository,
) : ObserveSelectedCommandUseCase {
    override fun invoke(): Flow<CommandNodeModel> = bleRepository.observeSelectedCommand()
}
