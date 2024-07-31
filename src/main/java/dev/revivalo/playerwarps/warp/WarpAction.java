package dev.revivalo.playerwarps.warp;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.guimanager.menu.ManageMenu;
import dev.revivalo.playerwarps.guimanager.menu.MenuType;
import dev.revivalo.playerwarps.guimanager.menu.WarpsMenu;
import dev.revivalo.playerwarps.hooks.Hooks;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.utils.SortingUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

public interface WarpAction<T> {
    default void preExecute(Player player, Warp warp, T data, @Nullable MenuType menuType) {
        preExecute(player, warp, data, menuType, 1);
    }

    default void preExecute(Player player, Warp warp, T data, @Nullable MenuType menuToOpen, int page) {
        if (!PermissionUtils.hasPermission(player, getPermission())) {
            player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString().replace("%permission%", getPermission().get()));
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
            if (Hooks.isHookEnabled(Hooks.getVaultHook())) {
                if (!Hooks.getVaultHook().getApi().has(player, getFee())) {
                    player.sendMessage(Lang.INSUFFICIENT_BALANCE_FOR_ACTION.asColoredString().replace("%price%", String.valueOf(getFee())));
                    return;
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {

            }
        }.runTaskTimer(PlayerWarpsPlugin.get(), 2, 2);

        boolean proceeded = execute(player, warp, data);

        if (proceeded) {
            if (Hooks.isHookEnabled(Hooks.getVaultHook())) {
                Hooks.getVaultHook().getApi().withdrawPlayer(player, getFee());
            }
        }

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
        }
    }

    /*
    Pořešit otevírání menu
     */


    boolean execute(Player player, Warp warp, T data);

    PermissionUtils.Permission getPermission();

    int getFee();

    Lang getInputText();

    boolean isPublicAction();

}
