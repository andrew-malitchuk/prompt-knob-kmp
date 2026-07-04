package data.repository.impl.di

import data.repository.impl.source.BleRepositoryImpl
import data.repository.impl.source.CommandHistoryRepositoryImpl
import data.repository.impl.source.CommandRepositoryImpl
import data.repository.impl.source.ConfigureRepositoryImpl
import data.repository.impl.source.PresetRepositoryImpl
import domain.repository.api.source.repository.BleRepository
import domain.repository.api.source.repository.CommandHistoryRepository
import domain.repository.api.source.repository.CommandRepository
import domain.repository.api.source.repository.ConfigureRepository
import domain.repository.api.source.repository.PresetRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for the repository implementation layer.
 *
 * Binds [ConfigureRepositoryImpl], [BleRepositoryImpl], [CommandRepositoryImpl],
 * [CommandHistoryRepositoryImpl], and [PresetRepositoryImpl] to their respective
 * domain repository interfaces as singletons.
 */
public val dataRepositoryImplModule: Module = module {
    singleOf(::ConfigureRepositoryImpl) bind ConfigureRepository::class
    singleOf(::BleRepositoryImpl) bind BleRepository::class
    singleOf(::CommandRepositoryImpl) bind CommandRepository::class
    singleOf(::CommandHistoryRepositoryImpl) bind CommandHistoryRepository::class
    singleOf(::PresetRepositoryImpl) bind PresetRepository::class
}
