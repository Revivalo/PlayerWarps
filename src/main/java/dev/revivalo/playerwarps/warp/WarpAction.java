package dev.revivalo.playerwarps.warp;

import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.guimanager.menu.ManageMenu;
import dev.revivalo.playerwarps.guimanager.menu.MenuType;
import dev.revivalo.playerwarps.guimanager.menu.WarpsMenu;
import dev.revivalo.playerwarps.hooks.Hooks;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.utils.SortingUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface WarpAction<T> {
    default void preExecute(Player player, Warp warp, T data, @Nullable MenuType menuToOpen, int page) {
        if (!PermissionUtils.hasPermission(player, getPermission())) {
            player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
            return;
        }

        if (warp != null)
            if (!warp.canManage(player)) {
                player.sendMessage(Lang.NOT_OWNING.asColoredString());
                return;
            }

        if (Hooks.getVaultHook().isOn()) {
            if (!Hooks.getVaultHook().getApi().withdrawPlayer(player, getFee()).transactionSuccess()) {
                player.sendMessage(Lang.INSUFFICIENT_BALANCE.asColoredString().replace("%price%", String.valueOf(getFee())));
                return;
            }
        }

        execute(player, warp, data);

        if (menuToOpen != null) {
            switch (menuToOpen) {
                case MANAGE_MENU:
                    new ManageMenu(warp).open(player);
                    break;
                case DEFAULT_LIST_MENU:
                case FAVORITE_LIST_MENU:
                case OWNED_LIST_MENU:
                    new WarpsMenu(menuToOpen)
                            .setPage(page)
                            .open(player, null, SortingUtils.SortType.LATEST);
                    break;
            }
//            if (warp == null) {
//                new WarpsMenu(MenuType.OWNED_LIST_MENU, 1).open(player, null, SortingUtils.SortType.LATEST);//PlayerWarpsPlugin.getGuiManager().openWarpsMenu(player, GUIManager.WarpMenuType.OWNED, null, 1, SortingUtils.SortType.LATEST);
//            } else {
//                new ManageMenu(warp).open(player); //PlayerWarpsPlugin.getGuiManager().openSetUpMenu(player, warp);
//            }
        }
    }

    /*
    Pořešit otevírání menu
     */

    void execute(Player player, Warp warp, T data);

    PermissionUtils.Permission getPermission();

    int getFee();
}
