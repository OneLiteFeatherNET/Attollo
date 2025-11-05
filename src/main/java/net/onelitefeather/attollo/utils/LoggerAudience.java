package net.onelitefeather.attollo.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.onelitefeather.attollo.Attollo;
import org.jetbrains.annotations.NotNull;

public final class LoggerAudience implements Audience {

    private final Attollo plugin;

    public LoggerAudience(Attollo plugin) {
        this.plugin = plugin;
    }

    @Override
    public void sendMessage(@NotNull final Component message) {
        this.plugin.getComponentLogger().info(message);
    }
}
