package presentation.feature.devices.core.utils

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

/**
 * Android actual: launches [ActivityResultContracts.RequestMultiplePermissions] to request
 * Bluetooth permissions. On API 31+ requests BLUETOOTH_SCAN and BLUETOOTH_CONNECT;
 * on earlier APIs requests ACCESS_FINE_LOCATION instead.
 */
@Composable
internal actual fun rememberBlePermissionRequester(
    onResult: (Boolean) -> Unit,
): () -> Unit {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
        )
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        onResult(result.values.all { it })
    }
    return { launcher.launch(permissions) }
}
