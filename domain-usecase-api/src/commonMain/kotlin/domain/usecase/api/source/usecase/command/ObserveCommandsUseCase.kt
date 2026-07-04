package domain.usecase.api.source.usecase.command

import domain.core.source.model.CommandNodeModel
import kotlinx.coroutines.flow.Flow

/** Observes the list of [CommandNodeModel] items whose parent matches [parentId]. */
public interface ObserveCommandsUseCase {
    public operator fun invoke(parentId: Int = 0): Flow<List<CommandNodeModel>>
}
