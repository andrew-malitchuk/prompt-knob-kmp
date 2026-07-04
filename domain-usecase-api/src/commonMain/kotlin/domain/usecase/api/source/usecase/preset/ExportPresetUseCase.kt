package domain.usecase.api.source.usecase.preset

/**
 * Serialises a saved preset's command tree to a shareable JSON string.
 *
 * The returned JSON matches the `CommandExportPayloadModel` schema so it can be
 * re-imported via [ImportPresetUseCase].
 *
 * @see ImportPresetUseCase
 */
public interface ExportPresetUseCase {
    /**
     * @param id Database id of the preset to export.
     * @return [Result.success] wrapping the JSON string, or [Result.failure] if the preset
     *   was not found or serialisation failed.
     */
    public suspend operator fun invoke(id: Int): Result<String>
}
