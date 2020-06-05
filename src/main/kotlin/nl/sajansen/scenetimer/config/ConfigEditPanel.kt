package nl.sajansen.scenetimer.config

import gui.config.formcomponents.FormComponent
import gui.config.formcomponents.FormInput
import gui.config.formcomponents.HeaderFormComponent
import gui.config.formcomponents.TextFormComponent
import objects.notifications.Notifications
import nl.sajansen.scenetimer.SceneTimerProperties
import nl.sajansen.scenetimer.config.formInputs.NumberFormInput
import nl.sajansen.scenetimer.config.formInputs.StringFormInput
import java.awt.BorderLayout
import java.awt.GridLayout
import java.util.logging.Logger
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.border.EmptyBorder

class ConfigEditPanel : JPanel() {
    private val logger = Logger.getLogger(ConfigEditPanel::class.java.name)

    private val formComponents: ArrayList<FormComponent> = ArrayList()

    init {
        createFormInputs()
        createGui()
    }

    private fun createFormInputs() {
        formComponents.add(HeaderFormComponent("Timer Server"))
        formComponents.add(
            StringFormInput(
                "timerServerAddress",
                SceneTimerProperties.timerServerAddress,
                saveCallback = { value ->
                    logger.info("Saving 'timerServerAddress' value: $value")
                    SceneTimerProperties.timerServerAddress = value
                },
                labelText = "OBS websocket address",
                allowEmpty = false
            )
        )

        formComponents.add(HeaderFormComponent("GUI"))
        formComponents.add(
            NumberFormInput<Int>(
                "timerCountUpFontSize",
                SceneTimerProperties.timerCountUpFontSize,
                saveCallback = { value ->
                    logger.info("Saving 'timerCountUpFontSize' value: $value")
                    SceneTimerProperties.timerCountUpFontSize = value
                },
                labelText = "Font size of the elapsed time",
                min = 1,
                max = Int.MAX_VALUE
            )
        )
        formComponents.add(
            NumberFormInput<Int>(
                "timerCountDownFontSize",
                SceneTimerProperties.timerCountDownFontSize,
                saveCallback = { value ->
                    logger.info("Saving 'timerCountDownFontSize' value: $value")
                    SceneTimerProperties.timerCountDownFontSize = value
                },
                labelText = "Font size of the remaining time",
                min = 1,
                max = Int.MAX_VALUE
            )
        )
    }

    private fun createGui() {
        layout = BorderLayout()

        val mainPanel = JPanel()
        mainPanel.layout = GridLayout(0, 1)
        mainPanel.border = EmptyBorder(10, 10, 10, 10)

        addConfigItems(mainPanel)

        val scrollPanelInnerPanel = JPanel(BorderLayout())
        scrollPanelInnerPanel.add(mainPanel, BorderLayout.PAGE_START)
        val scrollPanel = JScrollPane(scrollPanelInnerPanel)
        scrollPanel.border = null
        add(scrollPanel, BorderLayout.CENTER)
    }

    private fun addConfigItems(panel: JPanel) {
        formComponents.forEach {
            try {
                panel.add(it.component())
            } catch (e: Exception) {
                logger.severe("Failed to create Config Edit GUI component: ${it::class.java}")
                e.printStackTrace()

                if (it !is FormInput) {
                    return@forEach
                }

                logger.severe("Failed to create Config Edit GUI component: ${it.key}")
                Notifications.add(
                    "Failed to load GUI input for config key: <strong>${it.key}</strong>. Delete your <i>osq-scenetimer.properties</i> file and try again.",
                    "Configuration"
                )
                panel.add(TextFormComponent("Failed to load component. See Notifications.").component())
            }
        }
    }

    fun saveAll(): Boolean {
        val formInputComponents = formComponents.filterIsInstance<FormInput>()
        val validationErrors = ArrayList<String>()

        formInputComponents.forEach {
            val validation = it.validate()
            if (validation.isEmpty()) {
                return@forEach
            }

            logger.warning(validation.toString())
            validationErrors += validation
        }

        if (validationErrors.isNotEmpty()) {
            if (this.parent == null) {
                logger.warning("Panel is not a visible GUI component")
                return false
            }

            JOptionPane.showMessageDialog(
                this, validationErrors.joinToString(",\n"),
                "Invalid data",
                JOptionPane.ERROR_MESSAGE
            )
            return false
        }

        formInputComponents.forEach { it.save() }
        return true
    }
}
