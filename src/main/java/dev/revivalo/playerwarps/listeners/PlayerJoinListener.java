package dev.revivalo.playerwarps.listeners;

import dev.revivalo.playerwarps.user.UserHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

public class PlayerJoinListener implements Listener {
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onJoin(final PlayerJoinEvent event) {
        UserHandler.createUser(event.getPlayer(), new HashMap<>());
    }
}