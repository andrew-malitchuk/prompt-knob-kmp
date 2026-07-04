package data.repository.impl.core.mapper

import common.core.source.mapper.Mapper
import data.runtime.api.source.resource.FirmwareVersionResource
import domain.core.source.model.FirmwareVersionModel

internal object FirmwareVersionMapper {

    val toModel: Mapper<FirmwareVersionResource, FirmwareVersionModel> = Mapper {
        FirmwareVersionModel(
            major = it.major,
            minor = it.minor,
            patch = it.patch,
        )
    }

    val toResource: Mapper<FirmwareVersionModel, FirmwareVersionResource> = Mapper {
        FirmwareVersionResource(
            major = it.major,
            minor = it.minor,
            patch = it.patch,
        )
    }
}
