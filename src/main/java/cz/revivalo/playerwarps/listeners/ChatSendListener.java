package cz.revivalo.playerwarps.listeners;

import cz.revivalo.playerwarps.PlayerWarps;
import cz.revivalo.playerwarps.guimanager.GUIManager;
import cz.revivalo.playerwarps.warp.WarpHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatSendListener implements Listener {
    private final PlayerWarps playerWarps;
    private final GUIManager guiManager;
    private final WarpHandler warpHandler;
    public ChatSendListener(final PlayerWarps playerWarps, final GUIManager guiManager, final WarpHandler warpHandler) {
        this.playerWarps = playerWarps;
        this.guiManager = guiManager;
        this.warpHandler = warpHandler;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(final AsyncPlayerChatEvent event){
        if (event.isCancelled()) return;
        final UUID id = event.getPlayer().getUniqueId();
        if (guiManager.getChat().containsKey(id)){
            event.setCancelled(true);
            String[] value = guiManager.getChat().get(id).split(":");
            String warp = value[0];
            String choice = value[1];
            boolean open = Boolean.parseBoolean(value[2]);
            Player p = event.getPlayer();
            String msg = event.getMessage();
            Bukkit.getScheduler().runTask(playerWarps, () -> {
                switch (choice){
                    case "lore":
                        warpHandler.setLore(p, warp, msg, open);
                        break;
                    case "item":
                        warpHandler.setItem(p, warp, msg, open);
                        break;
                    case "price":
                        warpHandler.setPrice(p, warp, msg, open);
                        break;
                    case "rename":
                        warpHandler.rename(p, warp, msg, open);
                        break;
                    case "owner":
                        warpHandler.transferOwnership(p, Bukkit.getPlayer(msg), warp, open);
                        break;
                }
            });
            guiManager.getChat().remove(id);
        }
    }
}