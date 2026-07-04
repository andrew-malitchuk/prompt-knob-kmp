package dev.prompt.knob.io.source.tile

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import data.ble.api.source.datasource.BleServiceController
import dev.prompt.knob.io.R
import domain.core.source.model.ConnectionStateModel
import domain.usecase.api.source.usecase.ble.ConnectToDeviceUseCase
import domain.usecase.api.source.usecase.ble.DisconnectDeviceUseCase
import domain.usecase.api.source.usecase.ble.GetLastDeviceUseCase
import domain.usecase.api.source.usecase.ble.ObserveConnectionStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Quick Settings tile that toggles the BLE connection to the last known device.
 *
 * The tile reflects the current [ConnectionStateModel] as its active/inactive visual state:
 * - **Connected / Connecting** → tile is active; tapping disconnects.
 * - **Disconnected / Disconnecting** → tile is inactive; tapping connects to the last
 *   known device address if one is stored, otherwise does nothing.
 *
 * A fresh [CoroutineScope] is created on each [onStartListening] and cancelled on
 * [onStopListening] to avoid leaking work between tile panel open/close cycles.
 *
 * Requires API 24+ ([Build.VERSION_CODES.N]) — the [TileService] API minimum.
 *
 * @see ObserveConnectionStateUseCase
 * @see ConnectToDeviceUseCase
 * @see DisconnectDeviceUseCase
 * @see GetLastDeviceUseCase
 */
@RequiresApi(Build.VERSION_CODES.N)
class QuickCommandTileService : TileService() {

    private val observeConnectionState: ObserveConnectionStateUseCase by inject()
    private val connectToDevice: ConnectToDeviceUseCase by inject()
    private val disconnectDevice: DisconnectDeviceUseCase by inject()
    private val getLastDevice: GetLastDeviceUseCase by inject()
    private val bleServiceController: BleServiceController by inject()

    // NOTE: Initialised here only to satisfy the non-null type; the real scope is
    // always replaced at the top of onStartListening() before any launch() call.
    private var scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onStartListening() {
        super.onStartListening()
        // NOTE: Re-create the scope on every open cycle so that a cancelled scope
        // from a previous onStopListening() does not silently drop launched coroutines.
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        scope.launch {
            // firstOrNull() takes only the most recent emission — we need a snapshot,
            // not a continuous subscription, to set the initial tile appearance.
            val model = observeConnectionState().firstOrNull()
            updateTile(model?.state ?: ConnectionStateModel.Disconnected)
        }
    }

    override fun onClick() {
        super.onClick()
        scope.launch {
            val model = observeConnectionState().firstOrNull()
            when (model?.state) {
                ConnectionStateModel.Connected, ConnectionStateModel.Connecting -> {
                    disconnectDevice()
                    bleServiceController.stopConnectionService()
                    updateTile(ConnectionStateModel.Disconnected)
                }
                else -> {
                    val address = getLastDevice().getOrNull()
                    if (address != null) {
                        // Show connecting state optimistically before the async result arrives.
                        updateTile(ConnectionStateModel.Connecting)
                        val result = connectToDevice(address)
                        updateTile(
                            if (result.isSuccess) ConnectionStateModel.Connected
                            else ConnectionStateModel.Disconnected
                        )
                    }
                    // NOTE: If no last device is stored, the tap is silently ignored.
                    // The tile remains inactive — no error state is shown to the user.
                }
            }
        }
    }

    override fun onStopListening() {
        super.onStopListening()
        // Cancel all in-flight coroutines when the tile panel is dismissed to prevent
        // stale state updates from reaching a detached qsTile reference.
        scope.cancel()
    }

    /**
     * Applies [state] to the Quick Settings tile and triggers a visual refresh.
     *
     * Updates both the tile's active/inactive indicator and, on Android Q+, a subtitle
     * string that describes the connection state in plain text.
     *
     * @param state Current [ConnectionStateModel] to reflect in the tile UI.
     */
    private fun updateTile(state: ConnectionStateModel) {
        // NOTE: qsTile can be null if updateTile() is called after onStopListening()
        // (e.g. from an in-flight coroutine). Guard here to avoid an NPE.
        val tile = qsTile ?: return

        tile.state = when (state) {
            ConnectionStateModel.Connected -> Tile.STATE_ACTIVE
            ConnectionStateModel.Connecting -> Tile.STATE_ACTIVE
            ConnectionStateModel.Disconnected, ConnectionStateModel.Disconnecting -> Tile.STATE_INACTIVE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = when (state) {
                ConnectionStateModel.Connected -> getString(R.string.tile_connected)
                ConnectionStateModel.Connecting -> getString(R.string.tile_connecting)
                else -> getString(R.string.tile_disconnected)
            }
        }

        tile.updateTile()
    }
}
