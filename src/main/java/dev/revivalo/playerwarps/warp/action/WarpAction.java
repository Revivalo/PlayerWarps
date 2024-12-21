package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.menu.*;
import dev.revivalo.playerwarps.hook.HookManager;
import dev.revivalo.playerwarps.util.NumberUtil;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.Warp;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface WarpAction<T> {
    default void proceed(Player player, Warp warp) {
        proceed(player, warp, null);
    }

    default void proceed(Player player, Warp warp, T data) {
        proceed(player, warp, data, null);
    }

    default void proceed(Player player, Warp warp, T data, @Nullable Menu menu) {
        proceed(player, warp, data, menu, 1);
    }

    default void proceed(Player player, Warp warp, T data, @Nullable Menu menuToOpen, int page) {
        proceed(player, warp, data, menuToOpen, page, false);
    }

    default void proceed(Player player, Warp warp, T data, @Nullable Menu menuToOpen, int page, boolean isConfirmed) {
        if (!isConfirmed) {
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

                PlayerWarpsPlugin.get().runSync(() -> {
                    new ConfirmationMenu(warp, data)
                            .setMenuToOpen(menuToOpen)
                            .open(player, this);
                });

                return;
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
    }

    boolean execute(Player player, Warp warp, T data);

    PermissionUtil.Permission getPermission();

    default Lang getMessage() {
        return null;
    }

    default int getFee() {
        return 0;
    }

    default boolean hasFee() {
        return getFee() != 0;
    }

    default Lang getInputText() {
        return null;
    }

    default boolean isPublicAction() {
        return false;
    }

}
