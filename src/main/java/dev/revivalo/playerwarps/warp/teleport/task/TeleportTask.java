package dev.revivalo.playerwarps.warp.teleport.task;

import dev.revivalo.playerwarps.warp.teleport.Teleport;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportTask extends BukkitRunnable {
    private final Teleport teleport;
    private final Player player;
    private final Location location;

    private final int locationXZ;

    private Teleport.Status status = Teleport.Status.PROCESSING;
    private int cycle = 0;

    public TeleportTask(Teleport teleport) {
        this.teleport = teleport;
        this.player = teleport.getPlayer();
        this.location = teleport.getTargetLocation();
        this.locationXZ = this.player.getLocation().getBlockX() + this.player.getLocation().getBlockZ();
    }

    @Override
    public void run() {
        if (teleport.shouldRunTimer()) {
            if (!player.isOnline()) {
                cancel();
                status = Teleport.Status.ERROR;
                return;
            }

            if (locationXZ != (player.getLocation().getBlockX() + player.getLocation().getBlockZ())) {
                cancel();
                status = Teleport.Status.ERROR;
                return;
            }

            if (cycle == teleport.getDelay() * 2) {
                cancel();
                player.teleport(location);
                status = Teleport.Status.SUCCESS;
                return;
            }

            ++cycle;
        } else {
            cancel();
            player.teleport(location);
            status = Teleport.Status.SUCCESS;
        }
    }

    public boolean isResulted() {
        return status != Teleport.Status.PROCESSING;
    }

    public Teleport.Status getStatus() {
        return status;
    }
}