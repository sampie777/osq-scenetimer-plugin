package nl.sajansen.scenetimer

import javax.swing.JMenu
import javax.swing.JMenuItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SceneTimerPluginTest {

    @Test
    fun testCreateMenuItems() {
        val menu = JMenu()

        val result = SceneTimerPlugin().createMenu(menu)

        assertTrue(result)
        assertEquals(1, menu.menuComponentCount)
        assertEquals("Settings", (menu.menuComponents[0] as JMenuItem).text)
    }
}