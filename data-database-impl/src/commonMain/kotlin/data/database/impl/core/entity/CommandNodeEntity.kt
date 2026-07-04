package data.database.impl.core.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "command_node")
internal data class CommandNodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val parentId: Int,
    val label: String,
    val command: String,
    val icon: String?,
    val isFolder: Boolean,
    val isRotary: Boolean = false,
    val isMediaScreen: Boolean = false,
    val isAgentScreen: Boolean = false,
    val sortOrder: Int,
    val commandType: String = "SYSTEM",
    val cwCmdId: Int = 0,
    val ccwCmdId: Int = 0,
    val playPauseCmdId: Int = 0,
    val prevCmdId: Int = 0,
    val nextCmdId: Int = 0,
)
