package data.database.impl.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import data.database.impl.core.entity.CommandHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CommandHistoryRoomDao {

    @Query("SELECT * FROM command_history ORDER BY executedAt DESC")
    fun observeAll(): Flow<List<CommandHistoryEntity>>

    @Insert
    suspend fun insert(entity: CommandHistoryEntity): Long

    @Query("DELETE FROM command_history")
    suspend fun deleteAll()
}
