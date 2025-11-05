package net.onelitefeather.attollo.service;

import net.kyori.adventure.audience.Audience;

/**
 * Service for handling update functionality.
 *
 * @author TheMeinerLP
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UpdateService extends Runnable {

    /**
     * Starts the update check process.
     */
    void startUpdateCheck();

    /**
     * Notifies the given audience about the update status.
     *
     * @param audience The audience to notify.
     */
    void notifyAudience(Audience audience);

    /**
     * Shuts down the update service, releasing any resources.
     */
    void shutdown();

    /**
     * Gets the permission string required to receive update notifications.
     *
     * @return The permission string.
     */
    String getPermission();
}
