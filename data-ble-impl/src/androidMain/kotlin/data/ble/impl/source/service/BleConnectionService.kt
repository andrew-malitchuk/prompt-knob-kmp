package data.ble.impl.source.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import data.ble.api.source.datasource.BleConnection
import data.ble.api.source.resource.BleConnectionStateResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Foreground service that keeps the BLE connection to the knob device alive
 * while the app is backgrounded.
 *
 * Start this service when connecting to the device; stop it when disconnecting.
 * It holds a [BleConnection] reference from Koin and auto-reconnects on drops.
 *
 * Intent extras:
 * - [EXTRA_DEVICE_ADDRESS]: String — the BLE address to connect/reconnect to.
 * - Action [ACTION_CONNECT]: initiate a connection.
 * - Action [ACTION_DISCONNECT]: disconnect and stop the service.
 */
public class BleConnectionService : Service() {

    private val bleConnection: BleConnection by inject()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var reconnectJob: Job? = null
    private var stateMonitorJob: Job? = null
    private var notificationObserverJob: Job? = null
    private var deviceAddress: String? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())

        when (intent?.action) {
            ACTION_CONNECT -> {
                deviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS)
                deviceAddress?.let { address -> connectWithReconnect(address) }
            }
            ACTION_DISCONNECT -> {
                // Cancel reconnect observers BEFORE disconnecting so the Disconnected
                // state transition doesn't immediately trigger another connectWithReconnect().
                stateMonitorJob?.cancel()
                stateMonitorJob = null
                reconnectJob?.cancel()
                reconnectJob = null
                scope.launch { bleConnection.disconnect() }
                stopSelf()
            }
        }

        observeStateForNotification()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stateMonitorJob?.cancel()
        reconnectJob?.cancel()
        scope.cancel()
        super.onDestroy()
    }


    private fun connectWithReconnect(address: String) {
        stateMonitorJob?.cancel()
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            // Skip the connect loop if BleRepositoryImpl.connect() already established the
            // connection before starting this service. Calling connect() on an already-connected
            // peripheral causes the BLE stack to cycle through Disconnected→Connected, which
            // produces duplicate CMD_CHAR subscription attempts and prevents NavigateToDevice.
            if (bleConnection.state.value != BleConnectionStateResource.Connected) {
                var delayMs = RECONNECT_INITIAL_DELAY_MS
                while (true) {
                    try {
                        bleConnection.connect(address)
                        break // connected successfully
                    } catch (_: Exception) {
                        delay(delayMs)
                        delayMs = (delayMs * 2).coerceAtMost(RECONNECT_MAX_DELAY_MS)
                    }
                }
            }
            // Start state observer only after a successful connection so we don't
            // accumulate observers across reconnect cycles.
            stateMonitorJob = scope.launch {
                bleConnection.state.collect { state ->
                    if (state == BleConnectionStateResource.Disconnected) {
                        connectWithReconnect(address)
                    }
                }
            }
        }
    }


    private fun observeStateForNotification() {
        if (notificationObserverJob?.isActive == true) return
        notificationObserverJob = bleConnection.state
            .onEach { state ->
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, buildNotification(state))
            }
            .launchIn(scope)
    }

    private fun buildNotification(state: BleConnectionStateResource = BleConnectionStateResource.Connecting): Notification {
        ensureNotificationChannel()
        val statusText = when (state) {
            BleConnectionStateResource.Connected -> "Connected to PromptKnob"
            BleConnectionStateResource.Connecting -> "Connecting to PromptKnob…"
            BleConnectionStateResource.Disconnecting -> "Disconnecting…"
            BleConnectionStateResource.Disconnected -> "PromptKnob disconnected"
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("PromptKnob")
            .setContentText(statusText)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun ensureNotificationChannel() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "PromptKnob BLE",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Keeps the BLE connection to the knob device active"
        }
        manager.createNotificationChannel(channel)
    }

    public companion object {
        public const val ACTION_CONNECT: String = "data.ble.impl.action.CONNECT"
        public const val ACTION_DISCONNECT: String = "data.ble.impl.action.DISCONNECT"
        public const val EXTRA_DEVICE_ADDRESS: String = "extra_device_address"

        private const val TAG = "BleConnectionService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "ble_connection"
        private const val RECONNECT_INITIAL_DELAY_MS = 2_000L
        private const val RECONNECT_MAX_DELAY_MS = 30_000L
    }
}
