package data.database.impl.core.builder

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import data.database.impl.core.database.AppDatabase
import org.koin.core.module.Module
import platform.Foundation.NSHomeDirectory

internal actual fun Module.provideDatabaseBuilder() {
    single<AppDatabase> {
        val dbPath = NSHomeDirectory() + "/Documents/prompt_knob.db"
        Room.databaseBuilder<AppDatabase>(name = dbPath)
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}
