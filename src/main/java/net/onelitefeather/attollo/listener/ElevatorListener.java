package net.onelitefeather.attollo.listener;

import net.onelitefeather.attollo.service.ElevatorService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public final class ElevatorListener implements Listener {

    private final ElevatorService elevatorService;

    public ElevatorListener(ElevatorService elevatorService) {
        this.elevatorService = elevatorService;
    }


    @EventHandler
    public void onUp(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getTo().getY() <= event.getFrom().getY()) return;
        if (event.getTo().getY() - event.getFrom().getY() <= 0.125) return;
        this.elevatorService.handleElevator(player, true);
    }

    @EventHandler
    public void onDown(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        this.elevatorService.handleElevator(player, false);
    }

}
