package dev.themeinerlp.attollo

import dev.themeinerlp.attollo.listener.AttolloListener
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

class Attollo : JavaPlugin() {

    lateinit var elevatorBlock: Material
    override fun onLoad() {
        saveDefaultConfig()
    }

    override fun onEnable() {
        elevatorBlock = Material.matchMaterial(config.getString("elevatorBlock") ?: "DAYLIGHT_DETECTOR")
            ?: Material.DAYLIGHT_DETECTOR
        server.pluginManager.registerEvents(AttolloListener(this), this)
    }
}