package net.onelitefeather.attollo;

import net.onelitefeather.attollo.listener.ElevatorListener;
import net.onelitefeather.attollo.listener.UpdateCheckerListener;
import net.onelitefeather.attollo.service.ElevatorService;
import net.onelitefeather.attollo.service.ElevatorServiceImpl;
import net.onelitefeather.attollo.service.UpdateService;
import net.onelitefeather.attollo.service.UpdateServiceImpl;
import net.onelitefeather.attollo.utils.LoggerAudience;
import org.bukkit.plugin.java.JavaPlugin;

public final class Attollo extends JavaPlugin {

    private final ElevatorService elevatorService = new ElevatorServiceImpl(this);
    private final LoggerAudience loggerAudience = new LoggerAudience(this);
    private final UpdateService updateService = new UpdateServiceImpl(this, loggerAudience);

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        this.updateService.startUpdateCheck();
        this.elevatorService.loadBlockFromConfig();
        getServer().getPluginManager().registerEvents(new ElevatorListener(this.elevatorService), this);
        getServer().getPluginManager().registerEvents(new UpdateCheckerListener(this.updateService), this);
        this.updateService.notifyAudience(loggerAudience);
    }

    @Override
    public void onDisable() {
        this.updateService.shutdown();
    }
}
