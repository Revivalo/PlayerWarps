package dev.revivalo.playerwarps.listeners;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.guimanager.menu.MenuType;
import dev.revivalo.playerwarps.user.DataSelectorType;
import dev.revivalo.playerwarps.user.User;
import dev.revivalo.playerwarps.user.UserHandler;
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
        final User user = UserHandler.getUser(id);

        WarpAction currentAction = (WarpAction) user.getData(DataSelectorType.CURRENT_WARP_ACTION);
        if (currentAction == null){
            return;
        }

        user.addData(DataSelectorType.CURRENT_WARP_ACTION, null);
        event.setCancelled(true);

        final Optional<Warp> warpOptional = Optional.ofNullable((Warp) user.getData(DataSelectorType.SELECTED_WARP)); //PlayerWarpsPlugin.getWarpHandler().getWarpByID((UUID) temp[0]);
        if (!warpOptional.isPresent()) {
            return;
        }

        final Warp warp = warpOptional.get();
        final String message = event.getMessage();

        PlayerWarpsPlugin.get().runSync(() -> {
            switch (currentAction){
                case CHANGE_DISPLAY_NAME:
                    new ChangeDisplayNameAction().preExecute(editor, warp, message, MenuType.MANAGE_MENU);
                    break;
                case CHANGE_OWNERSHIP:
                    new TransferOwnershipAction().preExecute(editor, warp, Bukkit.getPlayer(message), MenuType.MANAGE_MENU);
                    break;
                case RENAME:
                    new RenameAction().preExecute(editor, warp, message, MenuType.MANAGE_MENU);
                    break;
                case SET_ADMISSION:
                    new SetAdmissionAction().preExecute(editor, warp, message, MenuType.MANAGE_MENU);
                    break;
                case SET_GUI_ITEM:
                    new SetPreviewItemAction().preExecute(editor, warp, message, MenuType.MANAGE_MENU);
                    break;
                case SET_DESCRIPTION:
                    new SetDescriptionAction().preExecute(editor, warp, message, MenuType.MANAGE_MENU);
                    break;
            }
        });
    }
}