package data.preference.impl.di

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import java.util.prefs.Preferences

/**
 * Desktop (JVM) platform implementation of [provideSettings].
 *
 * Registers a [Settings] singleton backed by [PreferencesSettings], which delegates to
 * `java.util.prefs.Preferences` under the user-root `"promptknob"` node.
 *
 * @see PreferencesSettings
 * @see data.preference.impl.di.provideSettings
 */
internal actual fun Module.provideSettings() {
    single<Settings> {
        PreferencesSettings(Preferences.userRoot().node("promptknob"))
    }
}
