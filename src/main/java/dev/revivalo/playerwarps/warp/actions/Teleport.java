package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.actions.task.TeleportTask;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class Teleport {
    private final Player player;
    private final Location targetLocation;
    private final boolean withCooldown;

    private TeleportTask task;

    public Teleport(Player player, Location targetLocation) {
        this.player = player;
        this.targetLocation = targetLocation;

        this.withCooldown = !PermissionUtils.hasPermission(player, PermissionUtils.Permission.BYPASS_TELEPORT_DELAY);
    }

    public void proceed() {
        player.sendMessage(Lang.TELEPORTATION.asColoredString().replace("%time%", Config.TELEPORTATION_DELAY.asString()));
        this.task = new TeleportTask(this);
        this.task.runTaskTimer(PlayerWarpsPlugin.get(), 0, 10);
    }

    public Player getPlayer() {
        return player;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public TeleportTask getTask() {
        return task;
    }

    public boolean isWithCooldown() {
        return withCooldown;
    }

    public enum Status {
        SUCCESS,
        PROCESSING,
        ERROR
    }
}
