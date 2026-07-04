package domain.usecase.api.source.usecase.preset

/**
 * Creates a new user preset from a JSON command-tree payload.
 *
 * Does not activate the preset automatically; call [LoadPresetUseCase] with the
 * returned id to make it the active layout.
 *
 * @see ExportPresetUseCase
 * @see LoadPresetUseCase
 */
public interface ImportPresetUseCase {
    /**
     * @param name Display name for the newly created preset.
     * @param json Serialised command tree in `CommandExportPayloadModel` JSON format.
     * @return [Result.success] wrapping the new preset's database id, or [Result.failure]
     *   if the JSON is malformed or the database write fails.
     */
    public suspend operator fun invoke(name: String, json: String): Result<Int>
}
