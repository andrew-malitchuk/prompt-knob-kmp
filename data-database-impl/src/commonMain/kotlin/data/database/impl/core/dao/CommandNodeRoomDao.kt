package data.database.impl.core.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import data.database.impl.core.entity.CommandNodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CommandNodeRoomDao {

    @Query("SELECT * FROM command_node ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<CommandNodeEntity>>

    @Query("SELECT * FROM command_node WHERE parentId = :parentId ORDER BY sortOrder ASC")
    fun observeByParentId(parentId: Int): Flow<List<CommandNodeEntity>>

    @Query("SELECT * FROM command_node ORDER BY sortOrder ASC")
    suspend fun getAll(): List<CommandNodeEntity>

    @Query("SELECT * FROM command_node WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): CommandNodeEntity?

    @Query("SELECT * FROM command_node WHERE parentId = :parentId ORDER BY sortOrder ASC")
    suspend fun getByParentId(parentId: Int): List<CommandNodeEntity>

    @Upsert
    suspend fun upsert(entity: CommandNodeEntity): Long

    @Query("DELETE FROM command_node WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM command_node")
    suspend fun deleteAll()

    @Transaction
    suspend fun deleteRecursive(parentId: Int) {
        val children = getByParentId(parentId)
        for (child in children) {
            if (child.isFolder) {
                deleteRecursive(child.id)
            }
            deleteById(child.id)
        }
    }
}
