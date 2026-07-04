package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.monad.Optional
import domain.usecase.api.source.usecase.configuration.SetClaudeHookEnabledUseCase
import domain.usecase.impl.core.resultLauncher

internal class SetClaudeHookEnabledUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetClaudeHookEnabledUseCase {
    override suspend fun invoke(enabled: Boolean): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setClaudeHookEnabled(enabled)
    }
}
