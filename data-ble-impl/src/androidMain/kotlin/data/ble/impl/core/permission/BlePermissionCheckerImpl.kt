package data.ble.impl.core.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import data.ble.api.source.datasource.BlePermissionChecker

/**
 * Android implementation of [BlePermissionChecker].
 *
 * Checks whether the required BLE permissions are currently granted.
 * Permission request itself must be handled by the UI layer (Activity/Fragment)
 * via [BlePermissionRequest], which this class notifies when a check triggers a request.
 *
 * Required permissions:
 * - API 31+: BLUETOOTH_SCAN, BLUETOOTH_CONNECT
 * - API < 31: BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_FINE_LOCATION
 */
internal class BlePermissionCheckerImpl(
    private val context: Context,
) : BlePermissionChecker {

    override suspend fun hasPermissions(): Boolean =
        requiredPermissions().all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

    /**
     * Permission requests on Android require an Activity context and
     * ActivityResultLauncher. This implementation returns the current
     * permission state only; the UI layer is responsible for requesting
     * permissions via a side effect from the ViewModel.
     */
    override suspend fun requestPermissions(): Boolean = hasPermissions()

    private fun requiredPermissions(): List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
        )
    } else {
        listOf(
            @Suppress("DEPRECATION")
            Manifest.permission.BLUETOOTH,
            @Suppress("DEPRECATION")
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
}
