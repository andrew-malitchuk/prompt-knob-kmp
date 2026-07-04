package data.database.impl.source.datasource

import data.database.api.source.datasource.PresetDataSource
import data.database.api.source.resource.PresetResource
import data.database.impl.core.dao.PresetRoomDao
import data.database.impl.core.mapper.PresetToEntityMapper
import data.database.impl.core.mapper.PresetToResourceMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PresetDataSourceImpl(
    private val dao: PresetRoomDao,
) : PresetDataSource {

    override fun observeAll(): Flow<List<PresetResource>> =
        dao.observeAll().map { list -> list.map { PresetToResourceMapper.map(it) } }

    override suspend fun getAll(): List<PresetResource> =
        dao.getAll().map { PresetToResourceMapper.map(it) }

    override suspend fun getById(id: Int): PresetResource? =
        dao.getById(id)?.let { PresetToResourceMapper.map(it) }

    override suspend fun upsert(resource: PresetResource): Int =
        dao.upsert(PresetToEntityMapper.map(resource)).toInt()

    override suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
