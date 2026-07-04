package domain.core.source.model

import domain.core.source.model.base.Model

/** Domain-level connection state for the knob device. */
public enum class ConnectionStateModel : Model {
    Disconnected,
    Connecting,
    Connected,
    Disconnecting,
}
