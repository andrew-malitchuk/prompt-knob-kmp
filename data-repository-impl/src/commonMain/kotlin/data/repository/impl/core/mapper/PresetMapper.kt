package data.repository.impl.core.mapper

import common.core.source.mapper.Mapper
import data.database.api.source.resource.PresetResource
import domain.core.source.model.PresetModel

internal object PresetMapper {

    val toModel: Mapper<PresetResource, PresetModel> = Mapper {
        PresetModel(
            id = it.id,
            name = it.name,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt,
            configJson = it.configJson,
        )
    }

    val toResource: Mapper<PresetModel, PresetResource> = Mapper {
        PresetResource(
            id = it.id,
            name = it.name,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt,
            configJson = it.configJson,
        )
    }
}
