package nl.sajansen.scenetimer.client


import com.google.gson.Gson
import nl.sajansen.scenetimer.OBSSceneTimer
import nl.sajansen.scenetimer.TimerRefreshableRegister
import nl.sajansen.scenetimer.client.objects.TimerMessage
import objects.notifications.Notifications
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import java.util.concurrent.CountDownLatch
import java.util.logging.Logger

@WebSocket
class TimerClientSocket {
    private val logger = Logger.getLogger(TimerClientSocket::class.java.name)

    private var session: Session? = null
    private val latch = CountDownLatch(1)

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        logger.info("Connected to server: ${session.remoteAddress.hostString}")
        this.session = session
        latch.countDown()
    }

    @OnWebSocketMessage
    fun onTextMessage(session: Session, message: String) {
        val message = try {
            Gson().fromJson(message, TimerMessage::class.java)
        } catch (e: Exception) {
            logger.warning("Failed to convert received message to json: $message")
            e.printStackTrace()
            return
        }

        OBSSceneTimer.timerMessage = message
        TimerRefreshableRegister.refreshTimer()
    }

    @OnWebSocketClose
    fun onClose(session: Session, status: Int, reason: String?) {
        logger.info("Connection closed with: ${session.remoteAddress.hostString}")
        Notifications.add("Connection with timer server lost", "Scene Timer")
    }

    fun sendMessage(message: String) {
        logger.info("Sending message: $message")
        if (session == null) {
            logger.warning("Cannot send message: not connected")
            return
        }

        try {
            session!!.remote.sendString(message)
        } catch (e: Exception) {
            logger.severe("Failed to send message to timer server")
            e.printStackTrace()
        }
    }

    fun disconnect() {
        logger.info("Disconnecting client socket")
        session?.close()
    }
}