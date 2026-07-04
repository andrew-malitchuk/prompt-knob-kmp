package data.runtime.impl.source.datasource

import data.runtime.api.source.datasource.BleSessionDataSource
import data.runtime.api.source.resource.BleSessionResource
import data.runtime.api.source.resource.FirmwareVersionResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class BleSessionDataSourceImpl : BleSessionDataSource {

    private val _device = MutableStateFlow<BleSessionResource?>(null)
    private val _firmware = MutableStateFlow<FirmwareVersionResource?>(null)
    private val _battery = MutableStateFlow<Int?>(null)

    override fun observeDevice(): Flow<BleSessionResource?> = _device.asStateFlow()
    override suspend fun getDevice(): BleSessionResource? = _device.value
    override suspend fun setDevice(device: BleSessionResource?) { _device.value = device }

    override fun observeFirmwareVersion(): Flow<FirmwareVersionResource?> = _firmware.asStateFlow()
    override suspend fun getFirmwareVersion(): FirmwareVersionResource? = _firmware.value
    override suspend fun setFirmwareVersion(version: FirmwareVersionResource?) { _firmware.value = version }

    override fun observeBatteryLevel(): Flow<Int?> = _battery.asStateFlow()
    override suspend fun setBatteryLevel(level: Int?) { _battery.value = level }
}
