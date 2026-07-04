package data.preference.impl.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.Settings
import data.preference.impl.source.DataStoreSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module

/** AndroidX Preferences DataStore delegate, stored under the file name `"promptknob_prefs"`. */
private val Context.dataStore by preferencesDataStore(name = "promptknob_prefs")

/**
 * Android platform implementation of [provideSettings].
 *
 * Registers a [Settings] singleton backed by [DataStoreSettings], which delegates to
 * the AndroidX Preferences DataStore instance obtained from the application [Context].
 *
 * @see DataStoreSettings
 * @see data.preference.impl.di.provideSettings
 */
internal actual fun Module.provideSettings() {
    single<Settings> {
        DataStoreSettings(androidContext().dataStore)
    }
}
