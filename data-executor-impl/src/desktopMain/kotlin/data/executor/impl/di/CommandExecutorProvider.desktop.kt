package data.executor.impl.di

import data.executor.api.source.executor.CommandExecutor
import data.executor.api.source.system.SystemSettingsOpener
import data.executor.impl.source.executor.NoOpCommandExecutor
import data.executor.impl.source.system.NoOpSystemSettingsOpener
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal actual fun Module.provideCommandExecutor() {
    singleOf(::NoOpCommandExecutor) bind CommandExecutor::class
    singleOf(::NoOpSystemSettingsOpener) bind SystemSettingsOpener::class
}
