package data.executor.impl.source.system

import data.executor.api.source.system.SystemSettingsOpener

internal class NoOpSystemSettingsOpener : SystemSettingsOpener {
    override fun isAccessibilityServiceEnabled(): Boolean = true
    override fun openAccessibilitySettings(): Unit = Unit
    override fun isNotificationPermissionGranted(): Boolean = true
    override fun openNotificationSettings(): Unit = Unit
}
