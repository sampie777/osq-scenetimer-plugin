package nl.sajansen.scenetimer

import gui.Refreshable
import themes.Theme
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Font
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class SceneTimerDetailPanel: JPanel(), Refreshable, OBSSceneTimerRefreshable {

    private val countUpTimerLabel = JLabel("Loading...")

    init {
        initGui()

        GUI.register(this)
        OBSSceneTimerRefreshableRegister.register(this)

        switchedScenes()
    }

    private fun initGui() {
        layout = BorderLayout()

        countUpTimerLabel.horizontalAlignment = SwingConstants.CENTER
        countUpTimerLabel.alignmentX = Component.CENTER_ALIGNMENT
        countUpTimerLabel.font = Font(Theme.get.FONT_FAMILY, Font.PLAIN, SceneTimerProperties.fontSize)
        add(countUpTimerLabel, BorderLayout.CENTER)
    }

    override fun windowClosing(window: Component?) {
        super.windowClosing(window)
        OBSSceneTimerRefreshableRegister.unregister(this)
    }

    override fun switchedScenes() {
        OBSSceneTimer.reset()
    }

    override fun refreshTimer() {
        countUpTimerLabel.text = OBSSceneTimer.getTimerAsClock()
    }
}