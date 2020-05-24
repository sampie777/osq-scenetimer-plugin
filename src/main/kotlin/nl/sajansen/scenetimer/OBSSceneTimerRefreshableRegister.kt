package nl.sajansen.scenetimer

import gui.Refreshable
import java.util.logging.Logger
import javax.swing.JFrame

interface OBSSceneTimerRefreshable {
    fun refreshTimer() {}
}


object OBSSceneTimerRefreshableRegister {
    private val logger = Logger.getLogger(OBSSceneTimerRefreshableRegister::class.java.name)

    var currentFrame: JFrame? = null

    private val components: HashSet<Refreshable> = HashSet()

    fun refreshTimer() {
        val componentsCopy = components.toTypedArray()
        for (component in componentsCopy) {
            component.refreshTimer()
        }
    }

    fun register(component: Refreshable) {
        logger.info("Registering component: ${component::class.java}")
        components.add(component)
    }

    fun isRegistered(component: Refreshable): Boolean {
        return components.contains(component)
    }

    fun unregister(component: Refreshable) {
        logger.info("Unregistering component: ${component::class.java}")
        components.remove(component)
    }

    fun unregisterAll() {
        logger.info("Unregistering all (${components.size}) components")
        components.clear()
    }

    fun registeredComponents() = components
}