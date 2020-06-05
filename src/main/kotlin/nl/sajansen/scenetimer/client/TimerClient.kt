package nl.sajansen.scenetimer.client


import objects.notifications.Notifications
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest
import org.eclipse.jetty.websocket.client.WebSocketClient
import java.net.URI
import java.util.logging.Logger

object TimerClient {
    private val logger = Logger.getLogger(TimerClient::class.java.name)

    private val websocketClient = WebSocketClient()
    private val timerClientSocket = TimerClientSocket()
    private var session: Session? = null

    fun connect(url: String) {
        logger.info("Starting timer websocket connection")
        try {
            websocketClient.start()
            val connection = websocketClient.connect(timerClientSocket, URI(url), ClientUpgradeRequest())
            session = connection.get()
        } catch (e: Exception) {
            logger.severe("Failed to start connection")
            e.printStackTrace()
            Notifications.add("Could not connect to timer server: $url", "Scene Timer")
        }
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
    }
}