package data.ble.impl.core.permission

import data.ble.api.source.datasource.BlePermissionChecker

/** Desktop stub: BLE permissions are not applicable on the JVM Desktop target. */
internal class BlePermissionCheckerImpl : BlePermissionChecker {
    override suspend fun hasPermissions(): Boolean = false
    override suspend fun requestPermissions(): Boolean = false
}
