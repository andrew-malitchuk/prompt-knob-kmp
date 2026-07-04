package data.database.impl.core.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import data.database.impl.core.entity.PresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PresetRoomDao {

    @Query("SELECT * FROM preset ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<PresetEntity>>

    @Query("SELECT * FROM preset ORDER BY updatedAt DESC")
    suspend fun getAll(): List<PresetEntity>

    @Query("SELECT * FROM preset WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): PresetEntity?

    @Upsert
    suspend fun upsert(entity: PresetEntity): Long

    @Query("DELETE FROM preset WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM preset")
    suspend fun deleteAll()
}
