package dev.themeinerlp.attollo.listener

import dev.themeinerlp.attollo.Attollo
import dev.themeinerlp.attollo.NOTIFY_UPDATE_PERMISSION
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class UpdateCheckerListener(private val attollo: Attollo) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (player.isOp || player.hasPermission(NOTIFY_UPDATE_PERMISSION)) {
            attollo.updateService.notifyPlayer(player)
        }
    }

}