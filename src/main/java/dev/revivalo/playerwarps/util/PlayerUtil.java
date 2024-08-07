package dev.revivalo.playerwarps.util;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerUtil {
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