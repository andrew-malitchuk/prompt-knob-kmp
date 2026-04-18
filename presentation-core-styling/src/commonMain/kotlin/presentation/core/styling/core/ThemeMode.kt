package presentation.core.styling.core

/**
 * Available theme appearance modes for the Kinetic Mono design system.
 *
 * The **Dark** mode is the primary experience (industrial monolith). Light mode
 * provides an inverted variant while maintaining the warm-neutral palette.
 *
 * @see presentation.core.styling.source.theme.AppTheme
 */
public enum class ThemeMode {
    /** Forces the light color palette regardless of the system setting. */
    Light,

    /** Forces the dark color palette — the primary Kinetic Mono experience. */
    Dark,

    /** Delegates to the platform's current dark-mode preference at runtime. */
    System,
}
