package data.database.api.source.datasource

import data.database.api.source.resource.PresetResource

/**
 * Data source contract for the `preset` table.
 *
 * Combines observable, readable, upsertable, and deletable capabilities over [PresetResource].
 *
 * @see PresetResource
 */
public interface PresetDataSource :
    ObservableDataSource<PresetResource>,
    ReadableDataSource<PresetResource>,
    UpsertableDataSource<PresetResource>,
    DeletableDataSource
