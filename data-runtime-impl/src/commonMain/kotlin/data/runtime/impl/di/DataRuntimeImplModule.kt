package data.runtime.impl.di

import data.runtime.api.source.datasource.BleSessionDataSource
import data.runtime.api.source.datasource.CommandCacheDataSource
import data.runtime.api.source.datasource.MoveDetectorDataSource
import data.runtime.impl.source.datasource.BleSessionDataSourceImpl
import data.runtime.impl.source.datasource.CommandCacheDataSourceImpl
import data.runtime.impl.source.datasource.MoveDetectorDataSourceImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public val dataRuntimeImplModule: Module = module {
    singleOf(::BleSessionDataSourceImpl) bind BleSessionDataSource::class
    singleOf(::CommandCacheDataSourceImpl) bind CommandCacheDataSource::class
    singleOf(::MoveDetectorDataSourceImpl) bind MoveDetectorDataSource::class
}
