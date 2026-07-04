package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.source.monad.Optional

/**
 * Use case for updating the MCP server enabled state.
 */
public interface SetMcpEnabledUseCase {
    /**
     * @param value Whether the MCP server should be enabled.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(value: Boolean): Optional
}
