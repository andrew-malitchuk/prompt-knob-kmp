package data.database.impl.core.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "command_history")
internal data class CommandHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val commandId: Int,
    val commandLabel: String,
    val commandType: String,
    val executedAt: Long,
    val status: String,
)
