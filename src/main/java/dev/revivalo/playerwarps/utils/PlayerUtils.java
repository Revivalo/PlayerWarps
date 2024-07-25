package dev.revivalo.playerwarps.utils;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerUtils {
    private static final HashMap<Player, Integer> tp = new HashMap<>();

    public static CompletableFuture<OfflinePlayer> getOfflinePlayer(final UUID uuid) {
        return PlayerWarpsPlugin.get().completableFuture(() -> Bukkit.getOfflinePlayer(uuid));
    }

    public static CompletableFuture<OfflinePlayer> getOfflinePlayer(final String playerName) {
        return PlayerWarpsPlugin.get().completableFuture(() -> Bukkit.getOfflinePlayer(playerName));
    }

    public static void announce(String message) {
        PlayerWarpsPlugin.get().getServer().getOnlinePlayers()
                .forEach(player -> player.sendMessage(message));
    }

    public static boolean isPlayerOnline(final UUID uuid) {
        return Bukkit.getPlayer(uuid) != null;
    }

    public static void teleportPlayer(Player player, Location loc, boolean cooldown) {
        if (!cooldown) {
            tp.put(player, player.getLocation().getBlockX() + player.getLocation().getBlockZ());
            player.sendMessage(Lang.TELEPORTATION.asColoredString().replace("%time%", Config.TELEPORTATION_DELAY.asString()));

            new BukkitRunnable() {

                int cycle = 0;

                @Override
                public void run() {
                    if (!player.isOnline()) cancel();
                    else {
                        if (tp.get(player) != (player.getLocation().getBlockX() + player.getLocation().getBlockZ())) {
                            player.sendMessage(Lang.TELEPORTATION_CANCELLED.asColoredString());
                            cancel();
                        } else {
                            if (cycle == Config.TELEPORTATION_DELAY.asInteger() * 2) {
                                cancel();
                                player.teleport(loc);
                            }
                        }
                        ++cycle;
                    }
                }
            }.runTaskTimer(PlayerWarpsPlugin.get(), 0, 10);
        } else player.teleport(loc);
    }
}
