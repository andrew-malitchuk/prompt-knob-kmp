package data.executor.api.source.system

/**
 * Platform-specific contract for opening system settings pages and querying
 * their current state. Non-Android platforms return no-op / true defaults.
 */
public interface SystemSettingsOpener {

    /**
     * Returns true if the app's AccessibilityService (AssistantRobotService)
     * is currently enabled in system accessibility settings.
     * Always returns true on non-Android platforms.
     */
    public fun isAccessibilityServiceEnabled(): Boolean

    /**
     * Opens the system Accessibility Settings screen so the user can enable
     * the app's accessibility service.
     * No-op on non-Android platforms.
     */
    public fun openAccessibilitySettings()

    /**
     * Returns true if the POST_NOTIFICATIONS runtime permission is granted
     * (Android 13+ / API 33+). Always returns true on non-Android platforms.
     */
    public fun isNotificationPermissionGranted(): Boolean

    /**
     * Opens the system notification settings page for this app so the user
     * can grant or revoke notification permission.
     * No-op on non-Android platforms.
     */
    public fun openNotificationSettings()
}
