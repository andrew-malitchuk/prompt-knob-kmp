package data.ble.impl.di

import data.ble.api.core.codec.YamkCodec
import data.ble.api.core.resource.YamkProtocol
import data.ble.impl.source.codec.YamkCodecImpl
import data.ble.impl.source.protocol.YamkProtocolImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for the BLE data layer.
 *
 * Registers:
 * - Platform-specific [BleScanner], [BleConnection], [BlePermissionChecker] via [provideBleAdapter].
 * - [YamkCodecImpl] as the singleton [YamkCodec] implementation.
 * - [YamkProtocolImpl] bound to [YamkProtocol] as a singleton protocol handler.
 */
public val dataBleImplModule: Module = module {
    provideBleAdapter()
    singleOf(::YamkCodecImpl) bind YamkCodec::class
    // YamkProtocolImpl has an Int mtu parameter with a default; use a lambda to avoid
    // Koin trying to resolve Int from the DI graph.
    single { YamkProtocolImpl(connection = get(), codec = get()) } bind YamkProtocol::class
}
