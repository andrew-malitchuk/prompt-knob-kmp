package data.database.api.source.datasource

import data.database.api.source.datasource.base.AppendableDataSource
import data.database.api.source.resource.CommandHistoryResource

/**
 * Data source contract for persisted command execution history.
 */
public interface CommandHistoryDataSource :
    ObservableDataSource<CommandHistoryResource>,
    AppendableDataSource<CommandHistoryResource>,
    ClearableDataSource
