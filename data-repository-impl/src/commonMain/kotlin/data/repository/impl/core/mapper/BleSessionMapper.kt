package data.repository.impl.core.mapper

import common.core.source.mapper.Mapper
import data.runtime.api.source.resource.BleSessionResource
import domain.core.source.model.BleDeviceModel

internal object BleSessionMapper {

    val toModel: Mapper<BleSessionResource, BleDeviceModel> = Mapper {
        BleDeviceModel(
            name = it.name,
            address = it.address,
            rssi = 0,
        )
    }

    val toResource: Mapper<BleDeviceModel, BleSessionResource> = Mapper {
        BleSessionResource(
            address = it.address,
            name = it.name ?: it.address,
        )
    }
}
