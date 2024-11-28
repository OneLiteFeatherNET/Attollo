package dev.themeinerlp.attollo

import dev.themeinerlp.attollo.listener.AttolloListener
import dev.themeinerlp.attollo.listener.UpdateCheckerListener
import dev.themeinerlp.attollo.service.UpdateService
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

open class Attollo : JavaPlugin() {

    lateinit var elevatorBlock: Material
    lateinit var updateService: UpdateService

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
        server.pluginManager.registerEvents(UpdateCheckerListener(this), this)
        updateChecker()
    }

    override fun onDisable() {
        updateService.shutdown()
    }

    private fun updateChecker() {
        updateService = UpdateService(this)
        updateService.run()
        updateService.notifyConsole(componentLogger)
    }

}