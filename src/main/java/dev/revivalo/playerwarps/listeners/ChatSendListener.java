package dev.revivalo.playerwarps.listeners;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.guimanager.menu.MenuType;
import dev.revivalo.playerwarps.user.UserManager;
import dev.revivalo.playerwarps.user.WarpAction;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.actions.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;
import java.util.UUID;

public class ChatSendListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event){
        final Player editor = event.getPlayer();
        final UUID id = editor.getUniqueId();
        final Object[] temp = UserManager.getUsersTemp(id);
        if (temp == null) return;
        event.setCancelled(true);
        final Optional<Warp> warpOptional = PlayerWarpsPlugin.getWarpHandler().getWarpByID((UUID) temp[0]);
        if (!warpOptional.isPresent())
            return;

        final Warp warp = warpOptional.get();
        final String message = event.getMessage();
        final WarpAction warpAction = (WarpAction) temp[1];
        final boolean shouldReOpenMenu = (boolean) temp[2];

        PlayerWarpsPlugin.get().runSync(() -> {
            switch (warpAction){
                case CHANGE_DISPLAY_NAME:
                    new ChangeDisplayNameAction().preExecute(editor, warp, message, MenuType.MANAGE_MENU);
                    //PlayerWarpsPlugin.getWarpHandler().changeDisplayName(editor, warp, message);
                    break;
                case CHANGE_OWNERSHIP:
                    new TransferOwnershipAction().preExecute(editor, warp, Bukkit.getPlayer(message), MenuType.MANAGE_MENU);
                    //PlayerWarpsPlugin.getWarpHandler().transferOwnership(editor, Bukkit.getPlayer(message), warp, shouldReOpenMenu);
                    break;
                case RENAME:
                    new RenameAction().preExecute(editor, warp, message, MenuType.MANAGE_MENU);
                    //PlayerWarpsPlugin.getWarpHandler().rename(editor, warp, message, shouldReOpenMenu);
                    break;
                case SET_ADMISSION:
                    new SetAdmissionAction().preExecute(editor, warp, message, MenuType.MANAGE_MENU);
                    //PlayerWarpsPlugin.getWarpHandler().setAdmission(editor, warp, message, shouldReOpenMenu);
                    break;
                case SET_GUI_ITEM:
                    new SetPreviewItemAction().preExecute(editor, warp, message, MenuType.MANAGE_MENU);
                    //PlayerWarpsPlugin.getWarpHandler().setItem(editor, warp, message, shouldReOpenMenu);
                    break;
                case SET_DESCRIPTION:
                    new SetDescriptionAction().preExecute(editor, warp, message, MenuType.MANAGE_MENU);
                    //PlayerWarpsPlugin.getWarpHandler().setDescription(editor, warp, message, shouldReOpenMenu);
                    break;
            }
        });
    }
}