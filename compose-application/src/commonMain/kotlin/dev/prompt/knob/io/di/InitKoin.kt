package dev.prompt.knob.io.di

import data.preference.impl.di.dataPreferenceImplModule
import data.repository.impl.di.dataRepositoryImplModule
import domain.usecase.impl.di.domainUseCaseImplModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import presentation.feature.about.di.presentationFeatureAboutModule
import presentation.feature.home.di.presentationFeatureHomeModule
import presentation.feature.onboarding.di.presentationFeatureOnboardingModule
import presentation.feature.settings.di.presentationFeatureSettingsModule
import presentation.feature.splash.di.presentationFeatureSplashModule

/**
 * Initializes the Koin dependency injection graph with all application modules.
 *
 * Registers data, domain, and presentation modules in the correct order.
 * Called once during application startup from platform-specific entry points.
 *
 * @param config Optional platform-specific Koin configuration (e.g., Android context).
 *
 * @see doInitKoin
 */
public fun initKoin(config: KoinAppDeclaration = {}) {
    startKoin {
        config()
        modules(
            dataPreferenceImplModule,
            dataRepositoryImplModule,
            domainUseCaseImplModule,
            presentationFeatureAboutModule,
            presentationFeatureHomeModule,
            presentationFeatureOnboardingModule,
            presentationFeatureSettingsModule,
            presentationFeatureSplashModule,
        )
    }
}

// reorganize package
