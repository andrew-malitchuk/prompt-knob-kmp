package data.ble.impl.core.permission

import data.ble.api.source.datasource.BlePermissionChecker
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreBluetooth.CBCentralManager
import platform.CoreBluetooth.CBCentralManagerDelegateProtocol
import platform.CoreBluetooth.CBManagerAuthorizationAllowedAlways
import platform.CoreBluetooth.CBManagerAuthorizationNotDetermined
import platform.darwin.NSObject
import kotlin.coroutines.resume

/**
 * iOS implementation of [BlePermissionChecker].
 *
 * Instantiating [CBCentralManager] triggers the system Bluetooth permission dialog.
 * The Info.plist must include NSBluetoothAlwaysUsageDescription.
 */
internal class BlePermissionCheckerImpl : BlePermissionChecker {

    // Strong reference prevents the delegate from being GC'd before the callback fires.
    private var centralManager: CBCentralManager? = null

    override suspend fun hasPermissions(): Boolean =
        CBCentralManager.authorization == CBManagerAuthorizationAllowedAlways

    /**
     * Instantiates a [CBCentralManager] to trigger the iOS Bluetooth permission dialog,
     * then suspends until the system resolves the authorization status.
     */
    override suspend fun requestPermissions(): Boolean {
        val current = CBCentralManager.authorization
        if (current != CBManagerAuthorizationNotDetermined) {
            return current == CBManagerAuthorizationAllowedAlways
        }
        return suspendCancellableCoroutine { continuation ->
            val delegate = object : NSObject(), CBCentralManagerDelegateProtocol {
                override fun centralManagerDidUpdateState(central: CBCentralManager) {
                    if (central.authorization != CBManagerAuthorizationNotDetermined) {
                        if (continuation.isActive) {
                            continuation.resume(central.authorization == CBManagerAuthorizationAllowedAlways)
                        }
                    }
                }
            }
            centralManager = CBCentralManager(delegate, null)
        }
    }
}
