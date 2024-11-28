package dev.themeinerlp.attollo.service

import com.github.zafarkhaja.semver.Version
import dev.themeinerlp.attollo.Attollo
import dev.themeinerlp.attollo.LATEST_RELEASE_VERSION_REQUEST
import dev.themeinerlp.attollo.NOTIFY_UPDATE_PERMISSION
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.http.HttpClient
import java.net.http.HttpResponse

class UpdateService(plugin: Attollo) : Runnable {
    private val hangarClient = HttpClient.newBuilder().build()
    private val LOGGER = LoggerFactory.getLogger(UpdateService::class.java)
    private val localVersion = Version.parse(plugin.pluginMeta.version)
    private var remoteVersion: Version? = null
    private val scheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0, 20 * 60 * 60 * 3)
    private val DOWNLOAD_URL = "https://hangar.papermc.io/OneLiteFeather/Attollo/versions/%s"


    override fun run() {
        val remoteVersion: Version? = getNewerVersion()
        if (remoteVersion != null) {
            this.remoteVersion = remoteVersion
            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.isOp || onlinePlayer.hasPermission(NOTIFY_UPDATE_PERMISSION)) {
                    notifyPlayer(localVersion, remoteVersion, onlinePlayer)
                }
            }
        }
    }

    fun notifyConsole(logger: ComponentLogger) {
        if (this.remoteVersion != null && remoteVersion?.isHigherThan(this.localVersion) == true) {
            logger.warn(MiniMessage.miniMessage().deserialize("<yellow>Your version (${localVersion}) is older than our latest published version (${remoteVersion.toString()}). Please update as soon as possible to get continued support. Or use this link ${DOWNLOAD_URL.format(remoteVersion.toString())}"))
        }
    }

    fun notifyPlayer(player: Player) {
        if (this.remoteVersion != null && remoteVersion?.isHigherThan(this.localVersion) == true) {
            notifyPlayer(this.localVersion, this.remoteVersion, player)
        }
    }

    private fun notifyPlayer(localVersion: Version, remoteVersion: Version?, player: Player) {
        player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow><click:open_url:'https://hangar.papermc.io/OneLiteFeather/Attollo'>Your version (${localVersion}) is older than our latest published version (${remoteVersion.toString()}). Please update as soon as possible to get continued support. Or click me to get on the download page!</click>"))
    }

    private fun getNewerVersion(): Version? {
        try {
            val httpResponse = hangarClient.send(
                LATEST_RELEASE_VERSION_REQUEST,
                HttpResponse.BodyHandlers.ofString()
            )
            val remoteVersion = Version.parse(httpResponse.body())
            if (remoteVersion.isHigherThan(this.localVersion)) {
                return remoteVersion
            }
        } catch (e: IOException) {
            LOGGER.error("Something went wrong to check updates", e)
        } catch (e: InterruptedException) {
            LOGGER.error("Something went wrong to check updates", e)
        }
        return null
    }

    fun shutdown() {
        hangarClient.shutdownNow()
        scheduler.cancel()
    }
}