package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model representing a saved command-layout preset.
 *
 * A preset captures a full snapshot of the command tree as a JSON blob. Loading a preset
 * replaces the active command tree and syncs it to the connected knob device.
 *
 * @property id Database primary key. 0 means not yet persisted.
 * @property name User-assigned display name.
 * @property createdAt Epoch milliseconds when the preset was first saved.
 * @property updatedAt Epoch milliseconds when the preset was last overwritten.
 * @property configJson Serialised JSON payload of the command tree.
 */
public data class PresetModel(
    public val id: Int,
    public val name: String,
    public val createdAt: Long,
    public val updatedAt: Long,
    public val configJson: String,
) : Model
