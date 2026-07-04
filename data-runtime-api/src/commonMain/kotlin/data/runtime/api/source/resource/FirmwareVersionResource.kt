package data.runtime.api.source.resource

import data.core.source.resource.Resource

/**
 * Runtime firmware version received from the connected device.
 *
 * @property major Major version component.
 * @property minor Minor version component.
 * @property patch Patch version component.
 */
public data class FirmwareVersionResource(
    val major: Int,
    val minor: Int,
    val patch: Int,
) : Resource
