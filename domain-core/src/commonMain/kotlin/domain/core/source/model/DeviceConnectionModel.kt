package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Aggregate connection model exposed to the presentation layer.
 *
 * @property device Currently connected or last seen device, null if never connected.
 * @property state Current connection state.
 */
public data class DeviceConnectionModel(
    public val device: BleDeviceModel?,
    public val state: ConnectionStateModel,
) : Model
