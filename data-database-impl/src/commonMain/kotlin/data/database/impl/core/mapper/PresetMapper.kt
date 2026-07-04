package data.database.impl.core.mapper

import common.core.source.mapper.Mapper
import data.database.api.source.resource.PresetResource
import data.database.impl.core.entity.PresetEntity

internal object PresetToResourceMapper : Mapper<PresetEntity, PresetResource> {
    override fun map(input: PresetEntity): PresetResource = PresetResource(
        id = input.id,
        name = input.name,
        createdAt = input.createdAt,
        updatedAt = input.updatedAt,
        configJson = input.configJson,
    )
}

internal object PresetToEntityMapper : Mapper<PresetResource, PresetEntity> {
    override fun map(input: PresetResource): PresetEntity = PresetEntity(
        id = input.id,
        name = input.name,
        createdAt = input.createdAt,
        updatedAt = input.updatedAt,
        configJson = input.configJson,
    )
}
