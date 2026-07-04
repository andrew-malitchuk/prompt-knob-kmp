package data.database.impl.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import data.database.impl.core.dao.CommandHistoryRoomDao
import data.database.impl.core.dao.CommandNodeRoomDao
import data.database.impl.core.dao.PresetRoomDao
import data.database.impl.core.entity.CommandHistoryEntity
import data.database.impl.core.entity.CommandNodeEntity
import data.database.impl.core.entity.PresetEntity

@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

@Database(
    entities = [CommandNodeEntity::class, CommandHistoryEntity::class, PresetEntity::class],
    version = 8,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun commandNodeDao(): CommandNodeRoomDao
    abstract fun commandHistoryDao(): CommandHistoryRoomDao
    abstract fun presetDao(): PresetRoomDao
}
