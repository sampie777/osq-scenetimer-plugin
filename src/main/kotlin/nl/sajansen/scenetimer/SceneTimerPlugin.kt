package nl.sajansen.scenetimer

import gui.utils.getMainFrameComponent
import nl.sajansen.scenetimer.client.TimerClient
import objects.notifications.Notifications
import plugins.PluginLoader
import plugins.common.DetailPanelBasePlugin
import nl.sajansen.scenetimer.config.ConfigWindow
import java.util.logging.Logger
import javax.swing.*

@Suppress("unused")
class SceneTimerPlugin : DetailPanelBasePlugin {
    private val logger = Logger.getLogger(SceneTimerPlugin::class.java.name)

    override val name: String = "SceneTimerPlugin"
    override val description: String = "Plugin for displaying the current scene live time"
    override val version: String = PluginInfo.version
    override val icon: Icon? = createImageIcon("/nl/sajansen/scenetimer/icon-14.png")

    override val tabName = "Scene Timer"

    override fun enable() {
        super.enable()
        SceneTimerProperties.writeToFile = true
        SceneTimerProperties.load()
        PluginLoader.registerDetailPanelPlugin(this)

        try {
            Thread {
                TimerClient.connect(SceneTimerProperties.timerServerAddress)
            }.start()
        } catch (e: Exception) {
            logger.severe("Failed to start tread for connecting to scene timer server")
            e.printStackTrace()
            Notifications.add("Could not connect to scene timer server: ${e.localizedMessage}", "Scene Timer")
        }
    }

    override fun disable() {
        super.disable()
        TimerClient.disconnect()
        PluginLoader.unregisterDetailPanelPlugin(this)
    }

    override fun detailPanel(): JComponent {
        return SceneTimerDetailPanel()
    }

    override fun createMenu(menu: JMenu): Boolean {
        val settingsItem = JMenuItem("Settings")
        settingsItem.addActionListener { ConfigWindow(getMainFrameComponent(getMainMenu(menu))) }
        menu.add(settingsItem)
        return true
    }

    private fun getMainMenu(menu: JMenu) = (menu.popupMenu.invoker.parent as JPopupMenu).invoker

    private fun createImageIcon(path: String): ImageIcon? {
        val imgURL = SceneTimerPlugin::class.java.getResource(path)
        if (imgURL != null) {
            return ImageIcon(imgURL)
        }

        logger.severe("Couldn't find imageIcon: $path")
        return null
    }
}