package dev.themeinerlp.attollo.service

import com.github.zafarkhaja.semver.Version
import dev.themeinerlp.attollo.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
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
            logger.warn(
                MiniMessage.miniMessage().deserialize(
                    NOTIFY_CONSOLE_UPDATE_MESSAGE,
                    Placeholder.component("local_version", Component.text(localVersion.toString())),
                    Placeholder.component("remote_version", Component.text(remoteVersion.toString())),
                    Placeholder.component("download_url", Component.text(DOWNLOAD_URL.format(remoteVersion.toString())))
                )
            )
        }
    }

    fun notifyPlayer(player: Player) {
        if (this.remoteVersion != null && remoteVersion?.isHigherThan(this.localVersion) == true) {
            notifyPlayer(this.localVersion, this.remoteVersion, player)
        }
    }

    private fun notifyPlayer(localVersion: Version, remoteVersion: Version?, player: Player) {
        player.sendMessage(
            MiniMessage.miniMessage().deserialize(
                NOTIFY_PLAYER_UPDATE_MESSAGE,
                Placeholder.component("local_version", Component.text(localVersion.toString())),
                Placeholder.component("remote_version", Component.text(remoteVersion.toString())),
                Placeholder.styling("download_url", ClickEvent.openUrl(DOWNLOAD_URL.format(remoteVersion.toString())))
            )
        )
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