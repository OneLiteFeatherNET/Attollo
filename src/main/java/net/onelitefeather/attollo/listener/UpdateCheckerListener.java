package net.onelitefeather.attollo.listener;

import net.onelitefeather.attollo.service.UpdateService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class UpdateCheckerListener implements Listener {

    private final UpdateService updateService;

    public UpdateCheckerListener(UpdateService updateService) {
        this.updateService = updateService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        if (player.isOp() || player.hasPermission(this.updateService.getPermission())) {
            this.updateService.notifyAudience(event.getPlayer());
        }
    }

}
