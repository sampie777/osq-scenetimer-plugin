package nl.sajansen.scenetimer.config

import java.awt.BorderLayout
import java.util.logging.Logger
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JPanel

class ConfigWindow(private val parentFrame: JFrame?) : JDialog(parentFrame) {
    private val logger = Logger.getLogger(ConfigWindow::class.java.name)

    private val configEditPanel: ConfigEditPanel = ConfigEditPanel()

    init {
        createGui()
    }

    private fun createGui() {
        val mainPanel = JPanel(BorderLayout(10, 10))
        add(mainPanel)

        mainPanel.add(configEditPanel, BorderLayout.CENTER)
        mainPanel.add(ConfigActionPanel(this), BorderLayout.PAGE_END)

        title = "Scene Timer Plugin Settings"
        setSize(400, 220)
        setLocationRelativeTo(parentFrame)
        modalityType = ModalityType.APPLICATION_MODAL
        isVisible = true
    }

    fun saveAll(): Boolean {
        return configEditPanel.saveAll()
    }
}