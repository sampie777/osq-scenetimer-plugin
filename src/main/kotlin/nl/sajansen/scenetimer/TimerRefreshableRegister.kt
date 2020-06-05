package nl.sajansen.scenetimer

import java.util.logging.Logger

object TimerRefreshableRegister {
    private val logger = Logger.getLogger(TimerRefreshableRegister::class.java.name)

    private val components: HashSet<TimerRefreshable> = HashSet()

    fun refreshTimer() {
        val componentsCopy = components.toTypedArray()
        for (component in componentsCopy) {
            component.refreshTimer()
        }
    }

    fun register(component: TimerRefreshable) {
        logger.info("Registering component: ${component::class.java}")
        components.add(component)
    }

    fun isRegistered(component: TimerRefreshable): Boolean {
        return components.contains(component)
    }

    fun unregister(component: TimerRefreshable) {
        logger.info("Unregistering component: ${component::class.java}")
        components.remove(component)
    }

    fun unregisterAll() {
        logger.info("Unregistering all (${components.size}) components")
        components.clear()
    }
}