package net.onelitefeather.attollo;

import net.onelitefeather.attollo.listener.ElevatorListener;
import net.onelitefeather.attollo.service.ElevatorService;
import net.onelitefeather.attollo.service.ElevatorServiceImpl;
import org.bukkit.plugin.java.JavaPlugin;

public final class Attollo extends JavaPlugin {

    private final ElevatorService elevatorService = new ElevatorServiceImpl(this);

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        this.elevatorService.loadBlockFromConfig();
        getServer().getPluginManager().registerEvents(new ElevatorListener(this.elevatorService), this);
    }
}
