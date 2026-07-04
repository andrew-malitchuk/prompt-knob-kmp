package data.executor.impl.di

import data.executor.api.source.executor.CommandExecutor
import data.executor.api.source.system.SystemSettingsOpener
import data.executor.impl.core.shell.ShellCommandExecutor
import data.executor.impl.core.system.MacOsSystemCommandExecutor
import data.executor.impl.source.executor.MacOsCommandRouter
import data.executor.impl.source.system.NoOpSystemSettingsOpener
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal actual fun Module.provideCommandExecutor() {
    single { ShellCommandExecutor() }
    single { MacOsSystemCommandExecutor(get()) }
    single {
        MacOsCommandRouter(
            shellExecutor = get(),
            systemExecutor = get(),
        )
    } bind CommandExecutor::class
    singleOf(::NoOpSystemSettingsOpener) bind SystemSettingsOpener::class
}
