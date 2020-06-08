package nl.sajansen.scenetimer.client

import nl.sajansen.scenetimer.SceneTimerProperties
import nl.sajansen.scenetimer.TimerRefreshableRegister
import nl.sajansen.scenetimer.client.objects.ConnectionState
import objects.notifications.Notifications
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest
import org.eclipse.jetty.websocket.client.WebSocketClient
import java.net.URI
import java.util.*
import java.util.logging.Logger

object TimerClient {
    private val logger = Logger.getLogger(TimerClient::class.java.name)

    private val websocketClient = WebSocketClient()
    private val timerClientSocket = TimerClientSocket({ onConnect() }, { reason -> onClose(reason) })
    private var session: Session? = null
    private var reconnecting: Boolean = false
    private var connectionState: ConnectionState = ConnectionState.NOT_CONNECTED
    fun getConnectionState() = connectionState

    fun connect(url: String) {
        logger.info("Starting timer websocket connection")
        updateConnectionState(if (!reconnecting) ConnectionState.CONNECTING else ConnectionState.RECONNECTING)

        try {
            websocketClient.start()
            val connection = websocketClient.connect(timerClientSocket, URI(url), ClientUpgradeRequest())
            session = connection.get()
        } catch (e: Exception) {
            logger.severe("Failed to start connection")
            e.printStackTrace()
            processFailedConnection(
                "Could not connect to timer server ($url): ${e.localizedMessage}",
                reconnect = true
            )
            return
        }

        reconnecting = false
    }

    fun disconnect() {
        logger.info("Disconnecting form timer websocket")
        try {
            session?.close()
            timerClientSocket.disconnect()
        } catch (e: Exception) {
            logger.warning("Failed to close connection session")
            e.printStackTrace()
        }

        try {
            websocketClient.stop()
        } catch (e: Exception) {
            logger.warning("Failed to close client")
            e.printStackTrace()
        }

        updateConnectionState(ConnectionState.NOT_CONNECTED)
    }

    private fun processFailedConnection(message: String, reconnect: Boolean = true) {
        updateConnectionState(ConnectionState.CONNECTION_FAILED)

        if (!reconnecting) {
            Notifications.add(message, "Scene Timer")
        }

        if (reconnect) {
            startReconnectingTimeout()
        }
    }

    private fun startReconnectingTimeout() {
        val connectionRetryTimer = Timer()
        connectionRetryTimer.schedule(object : TimerTask() {
            override fun run() {
                reconnecting = true
                connect(SceneTimerProperties.timerServerAddress)
            }
        }, SceneTimerProperties.reconnectionTimeout)
    }

    private fun updateConnectionState(state: ConnectionState) {
        connectionState = state
        TimerRefreshableRegister.refreshConnectionState()
    }

    private fun onConnect() {
        updateConnectionState(ConnectionState.CONNECTED)
    }

    private fun onClose(reason: String?) {
        updateConnectionState(ConnectionState.DISCONNECTED)
        startReconnectingTimeout()
    }
}