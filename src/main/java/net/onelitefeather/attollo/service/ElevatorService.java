package net.onelitefeather.attollo.service;

import org.bukkit.entity.Player;

/**
 * Service for handling elevator functionality.
 *
 * @author TheMeinerLP
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ElevatorService {
    /**
     * Loads the elevator block material from the configuration.
     */
    void loadBlockFromConfig();

    /**
     * Handles the elevator action for the given player.
     *
     * @param player The player using the elevator.
     * @param up     True if the player is going up, false if going down.
     */
    void handleElevator(Player player, boolean up);
}
