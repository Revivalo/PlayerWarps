package dev.revivalo.playerwarps.utils;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerUtils {
    public static CompletableFuture<OfflinePlayer> getOfflinePlayer(final UUID uuid) {
        return PlayerWarpsPlugin.get().completableFuture(() -> Bukkit.getOfflinePlayer(uuid));
    }

    public static CompletableFuture<OfflinePlayer> getOfflinePlayer(final String playerName) {
        return PlayerWarpsPlugin.get().completableFuture(() -> Bukkit.getOfflinePlayer(playerName));
    }

    public static boolean isPlayerOnline(final UUID uuid) {
        return Bukkit.getPlayer(uuid) != null;
    }
}
