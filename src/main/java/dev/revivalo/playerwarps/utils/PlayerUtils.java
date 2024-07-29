package dev.revivalo.playerwarps.utils;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Collections;
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

    public static void announce(String message, Player... except) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Arrays.asList(except).contains(player)) {
                player.sendMessage(message);
            }
        }
    }

    public static void announce(String message) {
        announce(message, new Player[]{});
    }

    public static boolean isPlayerOnline(final UUID uuid) {
        return Bukkit.getPlayer(uuid) != null;
    }
}
