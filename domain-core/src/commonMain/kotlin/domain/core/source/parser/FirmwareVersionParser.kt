package domain.core.source.parser

import domain.core.source.model.FirmwareVersionModel

/**
 * Parses a firmware version string in `MAJOR.MINOR.PATCH` format.
 *
 * @param text Raw version string received from the device (e.g. `"1.2.3"`).
 * @return A [FirmwareVersionModel] on success, or null if [text] is not a valid
 *   three-component numeric version string.
 */
public fun parseFirmwareVersion(text: String): FirmwareVersionModel? {
    val parts = text.trim().split('.')
    if (parts.size != 3) return null
    val major = parts[0].toIntOrNull() ?: return null
    val minor = parts[1].toIntOrNull() ?: return null
    val patch = parts[2].toIntOrNull() ?: return null
    return FirmwareVersionModel(major, minor, patch)
}
