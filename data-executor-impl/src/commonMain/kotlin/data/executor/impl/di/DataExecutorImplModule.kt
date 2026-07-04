package data.executor.impl.di

import org.koin.core.module.Module
import org.koin.dsl.module

/** Koin module that registers the platform-specific command executor. */
public val dataExecutorImplModule: Module = module {
    provideCommandExecutor()
}
