package data.ble.api.core.resource

import data.core.source.resource.Resource

/** Carries the cmd_id from a CMD_SELECTED notification received from the device. */
public data class CommandSelectedEvent(val cmdId: Int) : Resource
