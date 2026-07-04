package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Progression states of an OTA firmware update session.
 *
 * The expected happy-path sequence:
 * [Waiting] → [Receiving] (multiple) → [Verifying] → [Done]
 *
 * Any state can transition to [Error].
 */
public sealed class OtaStateModel : Model {
    /** OTA_BEGIN was sent; waiting for OTA_READY notification from the device. */
    public data object Waiting : OtaStateModel()

    /**
     * Firmware chunks are being sent; the device has acknowledged readiness.
     *
     * @property bytesReceived Number of bytes sent so far.
     * @property totalBytes Total firmware image size declared in OTA_BEGIN.
     */
    public data class Receiving(
        val bytesReceived: Long,
        val totalBytes: Long,
    ) : OtaStateModel() {
        /** Upload progress as a 0–100 integer percentage. */
        val percentage: Int get() =
            if (totalBytes > 0) ((bytesReceived * 100) / totalBytes).toInt().coerceIn(0, 100) else 0
    }

    /** All chunks sent and OTA_END delivered; device is verifying CRC and flashing. */
    public data object Verifying : OtaStateModel()

    /** OTA_DONE received; device will restart momentarily. */
    public data object Done : OtaStateModel()

    /**
     * OTA failed for the given [code].
     *
     * The session is terminated; the old firmware remains active on the device.
     */
    public data class Error(val code: OtaErrorCodeModel) : OtaStateModel()
}
