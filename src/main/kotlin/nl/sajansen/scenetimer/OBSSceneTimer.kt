package nl.sajansen.scenetimer

import nl.sajansen.scenetimer.client.objects.TimerMessage
import java.util.logging.Logger

object OBSSceneTimer {
    private val logger = Logger.getLogger(OBSSceneTimer::class.java.name)
    var timerMessage: TimerMessage? = null
}