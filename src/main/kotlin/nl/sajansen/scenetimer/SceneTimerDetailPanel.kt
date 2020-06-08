package nl.sajansen.scenetimer

import nl.sajansen.scenetimer.client.TimerClient
import nl.sajansen.scenetimer.client.objects.ConnectionState
import nl.sajansen.scenetimer.client.objects.TimerMessage
import nl.sajansen.scenetimer.client.objects.TimerState
import themes.Theme
import themes.DarkTheme
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class SceneTimerDetailPanel : JPanel(), TimerRefreshable {

    val connectionStateLabel: JLabel = JLabel()
    val sceneLabel: JLabel = JLabel("Loading...")
    val timerUpLabel: JLabel = JLabel()
    val timerDeviderLabel: JLabel = JLabel("/")
    val timerDownLabel: JLabel = JLabel()

    private var TIMER_APPROACHING_FONT_COLOR = Color(51, 51, 51)
    private var TIMER_APPROACHING_BACKGROUND_COLOR = Color.ORANGE
    private var TIMER_EXCEEDED_FONT_COLOR = Color(51, 51, 51)
    private var TIMER_EXCEEDED_BACKGROUND_COLOR = Color.RED

    init {
        initGui()

        TimerRefreshableRegister.register(this)

        if (Theme.get is DarkTheme) {
            TIMER_APPROACHING_FONT_COLOR = Color(51, 51, 51)
            TIMER_APPROACHING_BACKGROUND_COLOR = Color(201, 127, 0)
            TIMER_EXCEEDED_FONT_COLOR = Color(51, 51, 51)
            TIMER_EXCEEDED_BACKGROUND_COLOR = Color(255, 0, 0)
        }

        refreshTimer()
    }

    private fun initGui() {
        setSize(800, 200)
        minimumSize = Dimension(0, 0)

        layout = BorderLayout(10, 10)
        border = EmptyBorder(10, 10, 10, 10)

        connectionStateLabel.horizontalAlignment = SwingConstants.CENTER
        connectionStateLabel.font = Font(Theme.get.FONT_FAMILY, Font.PLAIN, 12)

        sceneLabel.horizontalAlignment = SwingConstants.CENTER
        sceneLabel.font = Font(Theme.get.FONT_FAMILY, Font.PLAIN, 18)

        val topPanel = JPanel()
        topPanel.background = null
        topPanel.layout = BorderLayout(10, 10)
        topPanel.add(connectionStateLabel, BorderLayout.PAGE_START)
        topPanel.add(sceneLabel, BorderLayout.CENTER)
        add(topPanel, BorderLayout.PAGE_START)

        timerUpLabel.toolTipText = "Time elapsed"
        timerUpLabel.horizontalAlignment = SwingConstants.CENTER
        timerUpLabel.alignmentX = Component.CENTER_ALIGNMENT
        timerUpLabel.alignmentY = Component.CENTER_ALIGNMENT
        timerUpLabel.font = Font(Theme.get.FONT_FAMILY, Font.PLAIN, SceneTimerProperties.timerCountUpFontSize)

        timerDeviderLabel.horizontalAlignment = SwingConstants.CENTER
        timerDeviderLabel.alignmentX = Component.CENTER_ALIGNMENT
        timerDeviderLabel.font = Font(
            Theme.get.FONT_FAMILY, Font.PLAIN,
            minOf(SceneTimerProperties.timerCountUpFontSize, SceneTimerProperties.timerCountDownFontSize)
        )
        timerDeviderLabel.isVisible = false

        timerDownLabel.toolTipText = "Time remaining"
        timerDownLabel.horizontalAlignment = SwingConstants.CENTER
        timerDownLabel.alignmentX = Component.CENTER_ALIGNMENT
        timerDownLabel.font = Font(Theme.get.FONT_FAMILY, Font.PLAIN, SceneTimerProperties.timerCountDownFontSize)
        timerDownLabel.isVisible = false

        val timersPanel = JPanel()
        timersPanel.background = null
        timersPanel.layout = FlowLayout(FlowLayout.CENTER, 20, 20)
        timersPanel.alignmentX = Component.CENTER_ALIGNMENT
        timersPanel.add(timerUpLabel)
        timersPanel.add(timerDeviderLabel)
        timersPanel.add(timerDownLabel)
        add(timersPanel, BorderLayout.CENTER)
    }

    override fun refreshTimer() {
        refreshConnectionState()

        if (OBSSceneTimer.timerMessage == null) {
            sceneLabel.text = "No data received"
            return
        }

        updateLabelsForTimer(OBSSceneTimer.timerMessage!!)
        updateColorsForTimer(OBSSceneTimer.timerMessage!!)
        repaint()
    }

    private fun updateLabelsForTimer(timerMessage: TimerMessage) {
        sceneLabel.text = timerMessage.sceneName
        timerUpLabel.text = timerMessage.elapsedTime

        if (timerMessage.isTimed) {
            timerDeviderLabel.isVisible = true
            timerDownLabel.text = timerMessage.remainingTime
            timerDownLabel.isVisible = true
        } else {
            timerDeviderLabel.isVisible = false
            timerDownLabel.isVisible = false
        }
    }

    private fun updateColorsForTimer(timerMessage: TimerMessage) {
        setColorsFor(timerMessage.timerState)
    }

    private fun setColorsFor(state: TimerState) {
        when (state) {
            TimerState.EXCEEDED -> {
                setLabelsColor(TIMER_EXCEEDED_FONT_COLOR)
                background = TIMER_EXCEEDED_BACKGROUND_COLOR
            }
            TimerState.APPROACHING -> {
                setLabelsColor(TIMER_APPROACHING_FONT_COLOR)
                background = TIMER_APPROACHING_BACKGROUND_COLOR
            }
            else -> {
                setLabelsColor(Theme.get.FONT_COLOR)
                background = Theme.get.BACKGROUND_COLOR
            }
        }
    }

    private fun setLabelsColor(color: Color) {
        sceneLabel.foreground = color
        timerUpLabel.foreground = color
        timerDeviderLabel.foreground = color
        timerDownLabel.foreground = color
    }

    override fun refreshConnectionState() {
        if (OBSSceneTimer.timerMessage != null && TimerClient.getConnectionState() == ConnectionState.CONNECTED) {
            connectionStateLabel.isVisible = false
            return
        }

        connectionStateLabel.isVisible = true
        connectionStateLabel.text = getConnectionStateRepresentation()
    }

    private fun getConnectionStateRepresentation(): String {
        if (TimerClient.getConnectionState() == ConnectionState.CONNECTING) {
            return "Connecting to ${SceneTimerProperties.timerServerAddress}..."
        }
        return TimerClient.getConnectionState().text
    }
}