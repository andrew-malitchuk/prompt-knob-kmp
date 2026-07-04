package data.preference.impl.di

import data.preference.api.source.datasource.ClaudeHookPreferenceSource
import data.preference.api.source.datasource.LanguagePreferenceSource
import data.preference.api.source.datasource.LastDevicePreferenceSource
import data.preference.api.source.datasource.McpPreferenceSource
import data.preference.api.source.datasource.OnboardingPreferenceSource
import data.preference.api.source.datasource.ThemePreferenceSource
import data.preference.impl.source.datasource.ClaudeHookPreferenceSourceImpl
import data.preference.impl.source.datasource.LanguagePreferenceSourceImpl
import data.preference.impl.source.datasource.LastDevicePreferenceSourceImpl
import data.preference.impl.source.datasource.McpPreferenceSourceImpl
import data.preference.impl.source.datasource.OnboardingPreferenceSourceImpl
import data.preference.impl.source.datasource.ThemePreferenceSourceImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin dependency injection module for the preference data layer.
 *
 * Registers the following bindings:
 * - [com.russhwolf.settings.Settings] — **singleton** platform-specific settings store.
 * - [ThemePreferenceSource] — **singleton** for theme preferences.
 * - [OnboardingPreferenceSource] — **singleton** for onboarding state.
 * - [LanguagePreferenceSource] — **singleton** for language preferences.
 * - [LastDevicePreferenceSource] — **singleton** for last connected BLE device address.
 * - [McpPreferenceSource] — **singleton** for MCP server enabled state.
 */
public val dataPreferenceImplModule: Module = module {
    provideSettings()
    singleOf(::ThemePreferenceSourceImpl) bind ThemePreferenceSource::class
    singleOf(::OnboardingPreferenceSourceImpl) bind OnboardingPreferenceSource::class
    singleOf(::LanguagePreferenceSourceImpl) bind LanguagePreferenceSource::class
    singleOf(::LastDevicePreferenceSourceImpl) bind LastDevicePreferenceSource::class
    singleOf(::McpPreferenceSourceImpl) bind McpPreferenceSource::class
    singleOf(::ClaudeHookPreferenceSourceImpl) bind ClaudeHookPreferenceSource::class
}
