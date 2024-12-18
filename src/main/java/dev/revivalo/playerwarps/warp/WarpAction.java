package dev.revivalo.playerwarps.warp;

import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.menu.*;
import dev.revivalo.playerwarps.hook.HookManager;
import dev.revivalo.playerwarps.util.NumberUtil;
import dev.revivalo.playerwarps.util.PermissionUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface WarpAction<T> {
    default void preExecute(Player player, Warp warp) {
        preExecute(player, warp, null);
    }

    default void preExecute(Player player, Warp warp, T data) {
        preExecute(player, warp, data, null);
    }

    default void preExecute(Player player, Warp warp, T data, @Nullable Menu menu) {
        preExecute(player, warp, data, menu, 1);
    }

    default void preExecute(Player player, Warp warp, T data, @Nullable Menu menuToOpen, int page) {
        if (!PermissionUtil.hasPermission(player, getPermission())) {
            player.sendMessage(Lang.INSUFFICIENT_PERMISSIONS.asColoredString().replace("%permission%", getPermission().asString()));
            return;
        }

        if (warp != null) {
            if (!isPublicAction()) {
                if (!warp.canManage(player)) {
                    player.sendMessage(Lang.NOT_OWNING.asColoredString());
                    return;
                }
            }
        }

        if (getFee() != 0) {
            if (HookManager.isHookEnabled(HookManager.getVaultHook())) {
                if (!HookManager.getVaultHook().getApi().has(player, getFee())) {
                    player.sendMessage(Lang.INSUFFICIENT_BALANCE_FOR_ACTION.asColoredString().replace("%price%", NumberUtil.formatNumber(getFee())));
                    return;
                }
            }
        }

        boolean proceeded = execute(player, warp, data);

        if (proceeded) {
            if (HookManager.isHookEnabled(HookManager.getVaultHook())) {
                HookManager.getVaultHook().getApi().withdrawPlayer(player, getFee());
            }
        }

        if (menuToOpen != null) {
            if (menuToOpen instanceof WarpsMenu.DefaultWarpsMenu
                    || menuToOpen instanceof WarpsMenu.FavoriteWarpsMenu
                    || menuToOpen instanceof WarpsMenu.MyWarpsMenu) {
                ((WarpsMenu) menuToOpen)
                        .setPage(page);
            }
                menuToOpen
                        .open(player);
        }
//        if (menuToOpen != null) {
//            switch (menuToOpen) {
//                case MANAGE_MENU:
//                    new ManageMenu(warp).open(player);
//                    break;
//                case BLOCKED_PLAYERS_MENU:
//                    new BlockedPlayersMenu(warp).open(player);
//                    break;
//                case DEFAULT_LIST_MENU:
//                case FAVORITE_LIST_MENU:
//                case OWNED_LIST_MENU:
//                    new WarpsMenu(menuToOpen)
//                            .setPage(page)
//                            .open(player, null, PlayerWarpsPlugin.getWarpHandler().getSortingManager().getDefaultSortType());
//                    break;
//            }
//        }
    }

    /*
    Pořešit otevírání menu
     */
    boolean execute(Player player, Warp warp, T data);

    PermissionUtil.Permission getPermission();

    default Lang getMessage() {
        return null;
    }

    default int getFee() {
        return 0;
    }

    default Lang getInputText() {
        return null;
    }

    default boolean isPublicAction() {
        return false;
    }

}
