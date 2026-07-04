package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Error codes returned by the device in OTA_ERROR notifications (opcode 0x63).
 *
 * @see OtaStateModel.Error
 */
public enum class OtaErrorCodeModel : Model {
    /** CRC32 mismatch between transmitted image and what was declared in OTA_BEGIN. */
    CRC,

    /** Internal flash write failure (esp_ota_write / esp_ota_end returned non-OK). */
    FLASH,

    /** Firmware image is larger than the OTA partition allows. */
    TOO_LARGE,

    /** Device battery is below the minimum threshold required for a safe OTA. */
    BATTERY,

    /** OTA_BEGIN payload was malformed or the sequence was violated. */
    BAD_BEGIN,

    /** BLE link dropped during the OTA session; partial image was discarded. */
    DISCONNECTED,
}
