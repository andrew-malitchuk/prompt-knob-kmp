package data.database.impl.di

import data.database.api.source.datasource.CommandHistoryDataSource
import data.database.api.source.datasource.CommandNodeDataSource
import data.database.api.source.datasource.PresetDataSource
import data.database.impl.core.builder.provideDatabaseBuilder
import data.database.impl.core.database.AppDatabase
import data.database.impl.source.datasource.CommandHistoryDataSourceImpl
import data.database.impl.source.datasource.CommandNodeDataSourceImpl
import data.database.impl.source.datasource.PresetDataSourceImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for the database implementation layer.
 *
 * Registers the platform-specific [AppDatabase] builder, all Room DAOs, and binds
 * [CommandNodeDataSourceImpl], [CommandHistoryDataSourceImpl], and [PresetDataSourceImpl]
 * to their respective [data.database.api.source.datasource] interfaces.
 */
public val dataDatabaseImplModule: Module = module {
    provideDatabaseBuilder()
    single { get<AppDatabase>().commandNodeDao() }
    single { get<AppDatabase>().commandHistoryDao() }
    single { get<AppDatabase>().presetDao() }
    singleOf(::CommandNodeDataSourceImpl) bind CommandNodeDataSource::class
    singleOf(::CommandHistoryDataSourceImpl) bind CommandHistoryDataSource::class
    singleOf(::PresetDataSourceImpl) bind PresetDataSource::class
}
