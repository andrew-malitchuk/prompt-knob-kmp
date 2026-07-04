package data.ble.impl.source.datasource

import data.ble.api.source.datasource.BleConnection
import data.ble.api.source.resource.BleConnectionStateResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

/** Desktop stub: BLE is not available on the JVM Desktop target. */
internal class BleConnectionImpl : BleConnection {
    override val state: StateFlow<BleConnectionStateResource> =
        MutableStateFlow(BleConnectionStateResource.Disconnected)

    override fun observeNotifications(): Flow<ByteArray> = emptyFlow()

    override suspend fun connect(address: String) {
        // BLE not available on Desktop — silently ignored
    }

    override suspend fun disconnect() {
        // no-op
    }

    override suspend fun write(data: ByteArray) {
        // BLE not available on Desktop — silently ignored
    }

    override suspend fun negotiateMtu(mtu: Int): Int = 0

    override suspend fun readFirmwareRevision(): String? = null

    override suspend fun writeOtaCtrl(data: ByteArray) = Unit

    override suspend fun writeOtaData(data: ByteArray) = Unit

    override fun observeOtaStatus(): Flow<ByteArray> = emptyFlow()
}
