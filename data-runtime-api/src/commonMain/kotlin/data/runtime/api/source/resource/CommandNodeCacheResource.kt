package data.runtime.api.source.resource

import data.core.source.resource.Resource

/**
 * In-memory cache entry for a single command tree node.
 *
 * Mirrors the command node structure. [commandType] is stored as a [String]
 * to keep this module free of domain-layer dependencies; callers must
 * convert via `CommandTypeModel.valueOf()` with a safe fallback.
 */
public data class CommandNodeCacheResource(
    val id: Int,
    val parentId: Int,
    val label: String,
    val isFolder: Boolean,
    val isRotary: Boolean = false,
    val isMediaScreen: Boolean = false,
    val sortOrder: Int,
    val command: String = "",
    val icon: String? = null,
    val commandType: String = "SYSTEM",
    val cwCmdId: Int = 0,
    val ccwCmdId: Int = 0,
    val playPauseCmdId: Int = 0,
    val prevCmdId: Int = 0,
    val nextCmdId: Int = 0,
) : Resource
