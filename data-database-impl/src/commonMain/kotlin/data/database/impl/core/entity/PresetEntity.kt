package data.database.impl.core.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preset")
internal data class PresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
    val configJson: String,
)
