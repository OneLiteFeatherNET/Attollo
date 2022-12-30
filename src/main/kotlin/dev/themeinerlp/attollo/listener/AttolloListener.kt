package dev.themeinerlp.attollo.listener

import dev.themeinerlp.attollo.USE_PERMISSION
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSneakEvent

class AttolloListener : Listener {

    @EventHandler
    fun onJump(event: PlayerMoveEvent) {
        val player = event.player
        if (event.to.y <= event.from.y) return
        if (event.to.y - event.from.y <= 0.125) return
        handleElevator(player, true)
    }

    @EventHandler
    fun onDown(event: PlayerToggleSneakEvent) {
        val player = event.player
        if (!player.isSneaking) return
        handleElevator(player)
    }

    private fun handleElevator(player: Player, up: Boolean = false) {
        if (!player.hasPermission(USE_PERMISSION)) return

        val location = player.location
        val block = location.block

        if (block.type != Material.DAYLIGHT_DETECTOR) return

        val world = block.world
        val height = world.maxHeight
        val depth = world.minHeight
        val blockLocation = block.location
        val found = if (up) {
            ((blockLocation.blockY + 1)..height).map {
                world.getBlockAt(blockLocation.blockX, it, blockLocation.blockZ)
            }
                .first { it.type == Material.DAYLIGHT_DETECTOR }.location
        } else {
            ((blockLocation.blockY - 1) downTo depth).map {
                world.getBlockAt(blockLocation.blockX, it, blockLocation.blockZ)
            }.first { it.type == Material.DAYLIGHT_DETECTOR }.location
        }
        found.yaw = location.yaw
        found.pitch = location.pitch
        val modifiedLocation = found.clone().add(0.5, 1.0, 0.5)
        if (modifiedLocation.block.type != Material.AIR) {
            return
        }
       player.teleport(modifiedLocation)
    }

}