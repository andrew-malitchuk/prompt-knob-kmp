package data.database.impl.core.builder

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import data.database.impl.core.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module

internal actual fun Module.provideDatabaseBuilder() {
    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(
            context = androidContext(),
            name = androidContext().getDatabasePath("prompt_knob.db").absolutePath,
        )
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}
