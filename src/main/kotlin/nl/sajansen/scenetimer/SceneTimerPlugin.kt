package nl.sajansen.scenetimer

import gui.utils.getMainFrameComponent
import plugins.PluginLoader
import plugins.common.DetailPanelBasePlugin
import nl.sajansen.scenetimer.config.ConfigWindow
import javax.swing.*

@Suppress("unused")
class SceneTimerPlugin : DetailPanelBasePlugin {
    override val name: String = "SceneTimerPlugin"
    override val description: String = "Plugin for displaying the current scene live time"
    override val version: String = PluginInfo.version
    override val icon: Icon? = null

    override val tabName = "Scene Timer"

    override fun enable() {
        super.enable()
        SceneTimerProperties.writeToFile = true
        SceneTimerProperties.load()
        PluginLoader.registerDetailPanelPlugin(this)
    }

    override fun disable() {
        super.disable()
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
}