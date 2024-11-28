package dev.themeinerlp.attollo.listener

import dev.themeinerlp.attollo.Attollo
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.net.URI

class UpdateCheckerListener(private val attollo: Attollo) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (player.hasPermission("attollo.update")) {
            attollo.server.scheduler.runTaskAsynchronously(attollo, Runnable {
                val latestVersion = getLatestVersion()
                if (attollo.description.version != latestVersion) {
                    player.sendMessage(
                        "§aAn update for Attollo is available! Version: $latestVersion",
                        "§aYou can download it at: https://hangar.papermc.io/OneLiteFeather/Attollo"
                    )
                }
            })
        }
    }

    private fun getLatestVersion(): String {
        val url = URI.create("https://hangar.papermc.io/api/v1/projects/Attollo/latestrelease").toURL()
        val reader = url.openStream().bufferedReader()
        val content = reader.use { it.readText() }
        return content
    }
}