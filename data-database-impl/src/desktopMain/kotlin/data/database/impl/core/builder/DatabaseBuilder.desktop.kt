package data.database.impl.core.builder

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import data.database.impl.core.database.AppDatabase
import data.database.impl.core.migration.MIGRATION_6_7
import data.database.impl.core.migration.MIGRATION_7_8
import org.koin.core.module.Module
import java.io.File

internal actual fun Module.provideDatabaseBuilder() {
    single<AppDatabase> {
        val dbDir = File(System.getProperty("user.home"), ".promptknob").also { it.mkdirs() }
        val dbPath = File(dbDir, "prompt_knob.db").absolutePath
        Room.databaseBuilder<AppDatabase>(name = dbPath)
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}
