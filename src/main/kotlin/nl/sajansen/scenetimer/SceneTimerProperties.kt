package nl.sajansen.scenetimer

import config.Config
import getCurrentJarDirectory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.logging.Logger

object SceneTimerProperties {
    private val logger = Logger.getLogger(SceneTimerProperties.toString())

    // En-/disables the creation of a properties file and writing to a properties file.
    // Leave disabled when running tests.
    var writeToFile: Boolean = false

    private val propertiesFilePath = getCurrentJarDirectory(this).absolutePath + File.separatorChar + "osq-scenetimer.properties"
    private val properties = Properties()

    var timerServerAddress: String = Config.obsAddress.replace(":4444", ":4050")
    var timerCountUpFontSize: Int = 40
    var timerCountDownFontSize: Int = 56

    fun load() {
        logger.info("Loading scene timer plugin properties from: $propertiesFilePath")

        if (File(propertiesFilePath).exists()) {
            FileInputStream(propertiesFilePath).use { properties.load(it) }
        } else {
            logger.info("No scene timer plugin properties file found, using defaults")
        }

        timerServerAddress = properties.getProperty("timerServerAddress", timerServerAddress)
        timerCountUpFontSize = properties.getProperty("timerCountUpFontSize", timerCountUpFontSize.toString()).toInt()
        timerCountDownFontSize = properties.getProperty("timerCountDownFontSize", timerCountDownFontSize.toString()).toInt()

        if (!File(propertiesFilePath).exists()) {
            save()
        }
    }

    fun save() {
        logger.info("Saving scene timer plugin properties")
        properties.setProperty("timerServerAddress", timerServerAddress.toString())
        properties.setProperty("timerCountUpFontSize", timerCountUpFontSize.toString())
        properties.setProperty("timerCountDownFontSize", timerCountDownFontSize.toString())

        if (!writeToFile) {
            return
        }

        logger.info("Saving to scene timer plugin properties file: $propertiesFilePath")

        FileOutputStream(propertiesFilePath).use { fileOutputStream ->
            properties.store(
                fileOutputStream,
                "User properties for Scene Timer plugin"
            )
        }
    }
}