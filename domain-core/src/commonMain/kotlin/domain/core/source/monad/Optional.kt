package domain.core.source.monad

/**
 * Represents the result of an operation that returns no specific value ([Unit]),
 * but can fail with a [Failure].
 *
 * This is a typealias for [Result]<[Unit]>.
 */
public typealias Optional = Result<Unit>
