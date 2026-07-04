package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Parsed device firmware version received via BLE VERSION_INFO notify (0xF1)
 * or the Device Information Service Firmware Revision String (0x180A / 0x2A26).
 */
public data class FirmwareVersionModel(val major: Int, val minor: Int, val patch: Int) : Model {
    override fun toString(): String = "$major.$minor.$patch"
}

