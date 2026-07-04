package presentation.feature.devices.core.utils

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberOpenBluetoothSettingsLauncher(): () -> Unit {
    val context = LocalContext.current
    return { context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) }
}
