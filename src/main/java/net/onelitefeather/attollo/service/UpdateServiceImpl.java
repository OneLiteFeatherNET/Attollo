package net.onelitefeather.attollo.service;

import com.github.zafarkhaja.semver.Version;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.onelitefeather.attollo.Attollo;
import net.onelitefeather.attollo.utils.LoggerAudience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class UpdateServiceImpl implements UpdateService {

    private static final String UPDATE_PERMISSION = "attollo.update";
    private static final String NOTIFY_PLAYER_UPDATE_MESSAGE = """
    <yellow><download_url>Your version (<local_version>) is older than our latest published version (<remote_version>).
    Please update as soon as possible to get continued support. Or click me to get on the download page!""".trim();
    private static final String NOTIFY_CONSOLE_UPDATE_MESSAGE = """
    <yellow>Your version (<local_version>) is older than our latest published version (<remote_version>).
    Please update as soon as possible to get continued support. Or use this link <download_url>.
    """.trim();
    private static final String DOWNLOAD_URL = "https://hangar.papermc.io/OneLiteFeather/Attollo/versions/%s";
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateServiceImpl.class);
    private static final long UPDATE_CHECK_INTERVAL_TICKS = Long.getLong("ATTOLLO_UPDATE_SERVICE_SCHEDULE", 20 * 60 * 60 * 3L);
    private static final HttpRequest LATEST_RELEASE_VERSION_REQUEST = HttpRequest.newBuilder()
            .uri(URI.create("https://hangar.papermc.io/api/v1/projects/Attollo/latestrelease"))
            .GET()
            .build();

    private final Attollo plugin;
    private final LoggerAudience loggerAudience;
    private final Version localVersion;
    private BukkitTask scheduler;
    private Version remoteVersion;

    public UpdateServiceImpl(Attollo plugin, LoggerAudience loggerAudience) {
        this.plugin = plugin;
        localVersion = Version.parse(plugin.getPluginMeta().getVersion());
        this.loggerAudience = loggerAudience;
    }

    @Override
    public void startUpdateCheck() {
        this.scheduler = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                this.plugin,
                this,
                0L,
                UPDATE_CHECK_INTERVAL_TICKS
        );
    }

    @Override
    public void notifyAudience(Audience audience) {
        if (this.remoteVersion == null || !this.remoteVersion.isHigherThan(this.localVersion)) {
            return;
        }
        if (audience instanceof LoggerAudience) {
            audience.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            NOTIFY_CONSOLE_UPDATE_MESSAGE,
                            Placeholder.component("local_version", Component.text(localVersion.toString())),
                            Placeholder.component("remote_version", Component.text(remoteVersion.toString())),
                            Placeholder.component("download_url", Component.text(DOWNLOAD_URL.format(remoteVersion.toString())))
                    )
            );
            return;
        }
        audience.sendMessage(
                MiniMessage.miniMessage().deserialize(
                        NOTIFY_PLAYER_UPDATE_MESSAGE,
                        Placeholder.component("local_version", Component.text(localVersion.toString())),
                        Placeholder.component("remote_version", Component.text(remoteVersion.toString())),
                        Placeholder.component("download_url", Component.text(DOWNLOAD_URL.format(remoteVersion.toString())))
                ).clickEvent(
                        ClickEvent.openUrl(
                                DOWNLOAD_URL.formatted(remoteVersion.toString())
                        )
                )
        );
    }

    @Override
    public void shutdown() {
        if (this.scheduler != null) {
            this.scheduler.cancel();
        }
    }

    @Override
    public String getPermission() {
        return UPDATE_PERMISSION;
    }

    @Override
    public void run() {
        this.remoteVersion = getNewerVersion();
        if (this.remoteVersion == null) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp() || player.hasPermission(UPDATE_PERMISSION)) {
                notifyAudience(player);
            }
        }
        notifyAudience(this.loggerAudience);
    }

    private Version getNewerVersion() {
        try(HttpClient hangarClient = HttpClient.newBuilder().build()) {
            HttpResponse<String> httpResponse = hangarClient.send(
                    LATEST_RELEASE_VERSION_REQUEST,
                    HttpResponse.BodyHandlers.ofString()
            );
            if (httpResponse.statusCode() != 200) {
                return null;
            }
            Version remoteVersion = Version.parse(httpResponse.body());
            if (remoteVersion.isHigherThan(this.localVersion)) {
                return remoteVersion;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Something went wrong to check updates", e);
        }
        return null;
    }
}
