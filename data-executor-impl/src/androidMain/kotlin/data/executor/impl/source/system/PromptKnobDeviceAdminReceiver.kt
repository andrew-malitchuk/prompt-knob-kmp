package data.executor.impl.source.system

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

/**
 * Device Admin Receiver for PromptKnob.
 *
 * Grants the app the [android.app.admin.DevicePolicyManager.lockNow] capability
 * so the LOCK_SCREEN system command can lock the device without requiring root.
 *
 * The user must activate device admin once via:
 * Settings > Security > Device Admin apps > PromptKnob
 *
 * (or via the intent launched by [AndroidSystemCommandExecutor] when the policy
 * is not yet active).
 */
public class PromptKnobDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent): Unit = Unit
    override fun onDisabled(context: Context, intent: Intent): Unit = Unit
}
