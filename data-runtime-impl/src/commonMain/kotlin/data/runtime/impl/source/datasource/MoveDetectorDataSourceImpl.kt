package data.runtime.impl.source.datasource

import data.runtime.api.source.datasource.MoveDetectorDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class MoveDetectorDataSourceImpl : MoveDetectorDataSource {

    private val _motion = MutableSharedFlow<Unit>()

    override fun observeMotion(): Flow<Unit> = _motion.asSharedFlow()
    override suspend fun emitMotion() { _motion.emit(Unit) }
}
