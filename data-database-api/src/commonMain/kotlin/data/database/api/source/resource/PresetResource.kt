package data.database.api.source.resource

import data.core.source.resource.Resource

/**
 * Data-layer resource representing a row in the `preset` table.
 *
 * @property id Database primary key. 0 for a not-yet-persisted record.
 * @property name User-assigned display name.
 * @property createdAt Epoch milliseconds of initial creation.
 * @property updatedAt Epoch milliseconds of last update.
 * @property configJson Serialised JSON payload of the command tree snapshot.
 */
public data class PresetResource(
    val id: Int,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
    val configJson: String,
) : Resource
