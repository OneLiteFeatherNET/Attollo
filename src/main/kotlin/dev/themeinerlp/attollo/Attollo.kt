package dev.themeinerlp.attollo

import dev.themeinerlp.attollo.listener.AttolloListener
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

open class Attollo : JavaPlugin() {


    lateinit var elevatorBlock: Material
    override fun onLoad() {
        saveDefaultConfig()
    }

    override fun onEnable() {
        val rawValue = config.getString("elevatorBlock") ?: "DAYLIGHT_DETECTOR"
        val material = Material.matchMaterial(rawValue)

        if (material == null) {
            logger.warning("Invalid elevatorBlock material in config.yml: '$rawValue'. Defaulting to DAYLIGHT_DETECTOR.")
        }

        elevatorBlock = material ?: Material.DAYLIGHT_DETECTOR
        logger.info("Using elevatorBlock: $elevatorBlock")
        server.pluginManager.registerEvents(AttolloListener(this), this)
    }

}
