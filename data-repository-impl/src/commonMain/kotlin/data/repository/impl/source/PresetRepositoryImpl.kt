package data.repository.impl.source

import data.database.api.source.datasource.PresetDataSource
import data.repository.impl.core.mapper.PresetMapper
import domain.core.source.model.PresetModel
import domain.core.source.monad.Optional
import domain.repository.api.source.repository.PresetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PresetRepositoryImpl(
    private val dataSource: PresetDataSource,
) : PresetRepository {

    override fun observeAll(): Flow<List<PresetModel>> =
        dataSource.observeAll().map { list -> list.map(PresetMapper.toModel::map) }

    override suspend fun getById(id: Int): PresetModel? =
        dataSource.getById(id)?.let(PresetMapper.toModel::map)

    override suspend fun save(preset: PresetModel): Result<Int> = runCatching {
        dataSource.upsert(PresetMapper.toResource.map(preset))
    }

    override suspend fun deleteById(id: Int): Optional = runCatching {
        dataSource.deleteById(id)
    }
}
