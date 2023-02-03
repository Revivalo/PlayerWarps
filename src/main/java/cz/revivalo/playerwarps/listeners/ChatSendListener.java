package cz.revivalo.playerwarps.listeners;

import cz.revivalo.playerwarps.user.UserManager;
import cz.revivalo.playerwarps.user.WarpAction;
import cz.revivalo.playerwarps.warp.Warp;
import cz.revivalo.playerwarps.warp.WarpHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.UUID;

public class ChatSendListener implements Listener {
    private final WarpHandler warpHandler;
    public ChatSendListener(final WarpHandler warpHandler) {
        this.warpHandler = warpHandler;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final PlayerChatEvent event){
        final Player editor = event.getPlayer();
        final UUID id = editor.getUniqueId();
        final Object[] temp = UserManager.getUsersTemp(id);
        if (temp == null) return;
        event.setCancelled(true);
        final String message = event.getMessage();
        final Warp warp = warpHandler.getWarpByID((UUID) temp[0]);
        final WarpAction warpAction = (WarpAction) temp[1];
        final boolean shouldReOpenMenu = (boolean) temp[2];
        switch (warpAction){
            case CHANGE_OWNERSHIP:
                warpHandler.transferOwnership(editor, Bukkit.getPlayer(message), warp, shouldReOpenMenu);
                break;
            case RENAME:
                warpHandler.rename(editor, warp, message, shouldReOpenMenu);
                break;
            case SET_ADMISSION:
                warpHandler.setAdmission(editor, warp, message, shouldReOpenMenu);
                break;
            case SET_GUI_ITEM:
                warpHandler.setItem(editor, warp, message, shouldReOpenMenu);
                break;
            case SET_DESCRIPTION:
                warpHandler.setDescription(editor, warp, message, shouldReOpenMenu);
                break;
        }
    }
}