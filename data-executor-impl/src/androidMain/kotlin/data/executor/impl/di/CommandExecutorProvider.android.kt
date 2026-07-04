package data.executor.impl.di

import data.executor.api.source.executor.CommandExecutor
import data.executor.api.source.system.SystemSettingsOpener
import data.executor.impl.core.assistant.AssistantPromptExecutor
import data.executor.impl.core.system.AndroidSystemCommandExecutor
import data.executor.impl.source.executor.AndroidCommandRouter
import data.executor.impl.source.system.AndroidSystemSettingsOpener
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind

internal actual fun Module.provideCommandExecutor() {
    single { AssistantPromptExecutor(androidContext()) }
    single { AndroidSystemCommandExecutor(androidContext()) }
    single<SystemSettingsOpener> { AndroidSystemSettingsOpener(androidContext()) }
    single {
        AndroidCommandRouter(
            assistantExecutor = get(),
            systemExecutor = get(),
        )
    } bind CommandExecutor::class
}
