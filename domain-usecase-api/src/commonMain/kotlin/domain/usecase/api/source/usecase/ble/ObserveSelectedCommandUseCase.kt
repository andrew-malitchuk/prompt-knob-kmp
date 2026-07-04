package domain.usecase.api.source.usecase.ble

import domain.core.source.model.CommandNodeModel
import kotlinx.coroutines.flow.Flow

/** Emits a [CommandNodeModel] each time the user selects a command on the knob device. */
public interface ObserveSelectedCommandUseCase {
    public operator fun invoke(): Flow<CommandNodeModel>
}
