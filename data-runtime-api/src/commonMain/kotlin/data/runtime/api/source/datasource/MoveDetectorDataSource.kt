package data.runtime.api.source.datasource

import kotlinx.coroutines.flow.Flow

/**
 * In-memory data source for move-detector motion events.
 *
 * No replay buffer — missed events are intentional since motion detection
 * is a real-time signal, not persistent state.
 */
public interface MoveDetectorDataSource {

    public fun observeMotion(): Flow<Unit>
    public suspend fun emitMotion()
}
