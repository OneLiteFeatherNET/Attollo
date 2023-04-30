package dev.themeinerlp.attollo.listener

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import dev.themeinerlp.attollo.Attollo
import dev.themeinerlp.attollo.USE_PERMISSION
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.CompletableFuture
import kotlin.test.Ignore


@ExtendWith(MockKExtension::class)
class AttolloListenerTest {
    private lateinit var server: ServerMock
    private lateinit var plugin: Attollo

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(Attollo::class.java)
    }

    @Test
    fun `test up without permissions`() {
        val world = server.addSimpleWorld("world")
        val player = spyk(server.addPlayer())
        val fromLocation = spyk(Location(world, 0.0,0.0 ,0.0))
        val toLocation = spyk(Location(world, 0.0,1.0 ,0.0))
        val event = PlayerMoveEvent(
            player,
            fromLocation,
            toLocation
        )
        server.pluginManager.callEvent(event)
        verify {
            player.hasPermission(USE_PERMISSION)
            fromLocation.y
            toLocation.y
        }
    }

    @Test
    fun `test up with permissions but wrong block type`() {
        val fakeWorld = spyk(server.addSimpleWorld("world"))
        val player = spyk(server.addPlayer()) {
            every { location } returns mockk {
                every { block } returns mockk {
                    every { type } returns Material.STONE
                    every { world } returns fakeWorld
                }
            }
        }
        player.addAttachment(plugin, USE_PERMISSION, true)
        val fromLocation = spyk(Location(fakeWorld, 0.0,0.0 ,0.0))
        val toLocation = spyk(Location(fakeWorld, 0.0,1.0 ,0.0))
        val event = PlayerMoveEvent(
            player,
            fromLocation,
            toLocation
        )
        server.pluginManager.callEvent(event)
        verifyOrder {
            player.addAttachment(any(), any(), true)
            toLocation.y
            fromLocation.y
            toLocation.y
            fromLocation.y
            player.hasPermission(USE_PERMISSION)
            player.location
        }
    }

    @Test
    fun `test up with permissions but right block type`() {

        val fakeWorld = spyk(server.addSimpleWorld("world")) {
            every { maxHeight } returns 100
            every { minHeight } returns -2

        }
        val dayLightBlockLocation = spyk(Location(fakeWorld, 0.0,50.0 ,0.0)) {
            every { block } returns mockk {
                every { type } returns Material.DAYLIGHT_DETECTOR
            }
        }
        every { fakeWorld.getBlockAt(any(), 50, any()) } returns mockk {
            every { type } returns Material.DAYLIGHT_DETECTOR
            every { location } returns dayLightBlockLocation
        }
        every { dayLightBlockLocation.clone() } returns spyk(Location(fakeWorld, 0.0,50.0 ,0.0)) {
            every { block } returns mockk {
                every { type } returns Material.AIR
            }
        }
        val blockLocation = spyk(Location(fakeWorld, 0.0,1.0 ,0.0, 0f, 0f))
        val player = spyk(server.addPlayer()) {
            every { location } returns mockk {
                every { yaw } returns 0f
                every { pitch } returns 0f
                every { block } returns mockk {
                    every { type } returns Material.DAYLIGHT_DETECTOR
                    every { world } returns fakeWorld
                    every { location } returns blockLocation
                }
            }
            every { teleportAsync(any()) } answers { CompletableFuture.completedFuture(true)}
        }
        player.addAttachment(plugin, USE_PERMISSION, true)
        val fromLocation = spyk(Location(fakeWorld, 0.0,0.0 ,0.0))
        val toLocation = spyk(Location(fakeWorld, 0.0,1.0 ,0.0))
        val event = PlayerMoveEvent(
            player,
            fromLocation,
            toLocation
        )
        server.pluginManager.callEvent(event)
        verifyOrder {
            player.addAttachment(any(), any(), true)
            toLocation.y
            fromLocation.y
            toLocation.y
            fromLocation.y
            player.hasPermission(USE_PERMISSION)
            player.location
            player.teleportAsync(any())
        }
    }

    @Ignore
    @Test
    fun `test down with permissions but right block type`() {

        val fakeWorld = spyk(server.addSimpleWorld("world")) {
            every { maxHeight } returns 100
            every { minHeight } returns 0

        }
        val dayLightBlockLocation = spyk(Location(fakeWorld, 0.0,20.0 ,0.0)) {
            every { block } returns mockk {
                every { type } returns Material.DAYLIGHT_DETECTOR
            }
        }
        every { fakeWorld.getBlockAt(any(), 50, any()) } returns mockk {
            every { type } returns Material.DAYLIGHT_DETECTOR
            every { location } returns dayLightBlockLocation
        }
        every { dayLightBlockLocation.clone() } returns spyk(Location(fakeWorld, 0.0,20.0 ,0.0)) {
            every { block } returns mockk {
                every { type } returns Material.AIR
            }
        }
        val blockLocation = spyk(Location(fakeWorld, 0.0,50.0 ,0.0, 0f, 0f))
        val player = spyk(server.addPlayer()) {
            every { location } returns mockk {
                every { yaw } returns 0f
                every { pitch } returns 0f
                every { block } returns mockk {
                    every { type } returns Material.DAYLIGHT_DETECTOR
                    every { world } returns fakeWorld
                    every { location } returns blockLocation
                }
            }
            every { teleportAsync(any()) } answers { CompletableFuture.completedFuture(true)}
            every { isSneaking } returns true
        }
        player.addAttachment(plugin, USE_PERMISSION, true)
        val event = PlayerToggleSneakEvent(
            player,
            true
        )
        server.pluginManager.callEvent(event)
        verifyOrder {
            player.addAttachment(any(), any(), true)
            player.isSneaking
            player.hasPermission(USE_PERMISSION)
            player.location
            player.teleportAsync(any())
        }
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }
}