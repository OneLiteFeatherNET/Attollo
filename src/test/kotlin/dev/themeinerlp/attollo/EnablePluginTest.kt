package dev.themeinerlp.attollo

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import org.bukkit.Material
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EnablePluginTest {

    private lateinit var server: ServerMock
    private lateinit var plugin: Attollo

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(Attollo::class.java)
    }

    @Test
    fun testElevatorBlockFiled() {
        assertNotNull(plugin.elevatorBlock)
        assertEquals(plugin.elevatorBlock, Material.DAYLIGHT_DETECTOR)
    }

     @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

}