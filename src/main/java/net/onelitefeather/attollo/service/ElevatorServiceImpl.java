package net.onelitefeather.attollo.service;

import net.onelitefeather.attollo.Attollo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ElevatorServiceImpl implements ElevatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElevatorServiceImpl.class);
    private static final String USE_PERMISSION = "attollo.use";
    private final Attollo attollo;
    private Material elevatorBlock;

    public ElevatorServiceImpl(Attollo attollo) {
        this.attollo = attollo;
    }

    @Override
    public void loadBlockFromConfig() {
        var rawValue = this.attollo.getConfig().getString("elevatorBlock");
        if (rawValue == null) {
            LOGGER.warn("Invalid elevatorBlock material in config.yml: '{}'. Defaulting to DAYLIGHT_DETECTOR.", rawValue);
            rawValue = "DAYLIGHT_DETECTOR";
        }
        var material = Material.matchMaterial(rawValue);
        if (material == null) {
            LOGGER.warn("Invalid elevatorBlock material in config.yml: '{}'. Defaulting to DAYLIGHT_DETECTOR.", rawValue);
            material = Material.DAYLIGHT_DETECTOR;
        }
        LOGGER.info("Using elevatorBlock: {}", material);
        this.elevatorBlock = material;
    }

    @Override
    public void handleElevator(Player player, boolean up) {
        if (!player.hasPermission(USE_PERMISSION)) return;

        Location location = player.getLocation().subtract(0.0, 1.0, 0.0);
        Block block = location.getBlock();

        if (block.getType() != this.elevatorBlock) return;

        World world = block.getWorld();
        int height = world.getMaxHeight();
        int depth = world.getMinHeight();
        Location blockLocation = block.getLocation();

        Location found = null;
        if (up) {
            for (int y = blockLocation.getBlockY() + 1; y <= height; y++) {
                Block b = world.getBlockAt(blockLocation.getBlockX(), y, blockLocation.getBlockZ());
                if (b.getType() == this.elevatorBlock) {
                    found = b.getLocation();
                    break;
                }
            }
        } else {
            for (int y = blockLocation.getBlockY() - 1; y >= depth; y--) {
                Block b = world.getBlockAt(blockLocation.getBlockX(), y, blockLocation.getBlockZ());
                if (b.getType() == this.elevatorBlock) {
                    found = b.getLocation();
                    break;
                }
            }
        }
        if (found == null) return;

        found.setYaw(location.getYaw());
        found.setPitch(location.getPitch());
        Location modifiedLocation = found.clone().add(0.5, 1.0, 0.5);
        if (modifiedLocation.getBlock().getType() != Material.AIR) {
            return;
        }
        player.teleportAsync(modifiedLocation);
    }
}
