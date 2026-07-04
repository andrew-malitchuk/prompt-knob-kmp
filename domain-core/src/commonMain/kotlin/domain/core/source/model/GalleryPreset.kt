package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * A built-in preset entry from the bundled gallery.
 *
 * Gallery presets are read-only and hard-coded in the app. Importing one creates a
 * new user-owned [PresetModel] copy that can then be edited or deleted.
 *
 * @property name Display name shown in the gallery list.
 * @property description Optional short description of the preset's intended use.
 * @property configJson Serialised JSON payload of the command tree for this preset.
 */
public data class GalleryPreset(
    val name: String,
    val description: String = "",
    val configJson: String,
) : Model
