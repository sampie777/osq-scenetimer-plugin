package nl.sajansen.scenetimer

import gui.MainFrame
import java.util.*

object PluginInfo {
    private val properties = Properties()
    val version: String
    val author: String

    init {
        properties.load(SceneTimerPlugin::class.java.getResourceAsStream("scenetimer.properties"))
        version = properties.getProperty("version", "unknown")
        author = properties.getProperty("author", "Samuel-Anton Jansen")
    }
}