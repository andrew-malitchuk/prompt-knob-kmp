package data.ble.api.core.resource

import data.core.source.resource.Resource

/** Outcome of a SYNC_ACK notification received from the device after a sync flow. */
public sealed class SyncAckResult : Resource {
    public data object Ok : SyncAckResult()
    public data class Error(val statusCode: UByte) : SyncAckResult()
}
