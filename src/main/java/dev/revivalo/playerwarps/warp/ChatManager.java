package dev.revivalo.playerwarps.warp;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//public class ChatManager {
//    public CompletableFuture<String> waitForPlayerInput(Player player, long timeout, TimeUnit unit) {
//        CompletableFuture<String> future = new CompletableFuture<>();
//
//        PlayerWarpsPlugin.get().registerEvents(new Listener() {
//            @EventHandler
//            public void onPlayerChat(PlayerChatEvent event) {
//                if (event.getPlayer().equals(player)) {
//                    event.setCancelled(true);
//                    future.complete(event.getMessage());
//                    HandlerList.unregisterAll(this);
//                }
//            }
//        });
//
//        // Schedule a timeout
//        PlayerWarpsPlugin.get().getScheduler().runTaskLater(PlayerWarpsPlugin.get(), () -> {
//            if (!future.isDone()) {
//                future.completeExceptionally(new TimeoutException("Player did not respond in time"));
//                HandlerList.unregisterAll(this);
//            }
//        }, unit.toSeconds(timeout) * 20); // Convert seconds to ticks
//
//        return future;
//    }
//
//}
