package presentation.core.navigation.api.source.destination

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Sealed hierarchy of all navigable screens in the application.
 *
 * Each subclass is [Serializable] to support Navigation 3 saved-state persistence.
 * Register new destinations in [navSavedStateConfiguration] and [NavigationHost].
 *
 * @see AppNavigator
 */
@Serializable
public sealed class Destination : NavKey {
    /** App launch splash screen with animation. */
    @Serializable
    public data object Splash : Destination()

    /** First-time onboarding flow. */
    @Serializable
    public data object Onboarding : Destination()

    /** Main home screen. */
    @Serializable
    public data object Home : Destination()

    /** Application settings (theme, language). */
    @Serializable
    public data object Settings : Destination()

    /** About screen with app information. */
    @Serializable
    public data object About : Destination()

    /** Design-system styleguide showcase (dev utility). */
    @Serializable
    public data object Styleguide : Destination()

    /** BLE device list — scan, discover, and connect to a knob device. */
    @Serializable
    public data object Devices : Destination()

    /** BLE device detail — dashboard for the currently connected knob device. */
    @Serializable
    public data object Device : Destination()

    /** Language picker — full-screen list to change the application locale. */
    @Serializable
    public data object LanguagePicker : Destination()

    /**
     * Command/folder list for a given [parentId] level.
     *
     * [parentId] = 0 means root level.
     * [isParentRotary] true when the parent folder is a rotary encoder folder — used
     *   to auto-assign sortOrder for new leaf commands (0=CW, 1=CCW).
     */
    /** Preset gallery and user preset management. */
    @Serializable
    public data object Presets : Destination()

    @Serializable
    public data class CommandList(
        val parentId: Int = 0,
        val isParentRotary: Boolean = false,
    ) : Destination()

    /**
     * Create or edit a command/folder.
     *
     * [commandId] = -1 means "create new".
     * [isFolder] indicates whether the form targets a folder or a leaf command.
     * [parentId] is the parent folder to assign when creating.
     * [sessionId] is a random token generated on each "create" navigation to ensure
     *   a fresh ViewModel is created even when [commandId] is -1.
     * [sortOrder] position within a rotary folder: 0 = CW, 1 = CCW.
     * [isParentRotary] true when the parent is a rotary folder — shows CW/CCW direction label.
     */
    @Serializable
    public data class CommandForm(
        val commandId: Int = -1,
        val isFolder: Boolean = false,
        val parentId: Int = 0,
        val sessionId: String = "",
        val sortOrder: Int = 0,
        val isParentRotary: Boolean = false,
    ) : Destination()
}
