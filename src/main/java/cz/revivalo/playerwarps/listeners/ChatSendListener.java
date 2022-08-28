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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event){
        final UUID id = event.getPlayer().getUniqueId();
        if (guiManager.getChat().containsKey(id)){
            event.setCancelled(true);
            final String[] value = guiManager.getChat().get(id).split(":");
            final String warp = value[0];
            final String choice = value[1];
            final boolean open = Boolean.parseBoolean(value[2]);
            final Player editor = event.getPlayer();
            final String message = event.getMessage();
            Bukkit.getScheduler().runTask(playerWarps, () -> {
                switch (choice){
                    case "lore":
                        warpHandler.setLore(editor, warp, message, open);
                        break;
                    case "item":
                        warpHandler.setItem(editor, warp, message, open);
                        break;
                    case "price":
                        warpHandler.setPrice(editor, warp, message, open);
                        break;
                    case "rename":
                        warpHandler.rename(editor, warp, message, open);
                        break;
                    case "owner":
                        warpHandler.transferOwnership(editor, Bukkit.getPlayer(message), warp, open);
                        break;
                }
            });
            guiManager.getChat().remove(id);
        }
    }
}