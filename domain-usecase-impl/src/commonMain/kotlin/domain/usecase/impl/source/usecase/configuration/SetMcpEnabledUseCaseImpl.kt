package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.configuration.SetMcpEnabledUseCase
import domain.usecase.impl.core.resultLauncher

/**
 * Default implementation of [SetMcpEnabledUseCase].
 *
 * Persists the provided MCP server enabled flag via [ConfigureRepository].
 *
 * @property configureRepository Repository used for writing MCP configuration.
 */
internal class SetMcpEnabledUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetMcpEnabledUseCase {

    /**
     * @param value Whether the MCP server should be enabled.
     * @return An [Optional] indicating success or failure.
     * @see SetMcpEnabledUseCase.invoke
     */
    override suspend fun invoke(value: Boolean): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setMcpEnabled(value)
    }
}
