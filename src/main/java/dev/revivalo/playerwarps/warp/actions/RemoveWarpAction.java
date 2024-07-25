package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.hooks.Hooks;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.utils.PlayerUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RemoveWarpAction implements WarpAction<Void> {
    @Override
    public boolean execute(Player player, Warp warp, Void data) {
        PlayerWarpsPlugin.getWarpHandler().removeWarp(warp);
        if (Hooks.isHookEnabled(Hooks.getVaultHook())) {
            PlayerUtils.getOfflinePlayer(warp.getOwner()).thenAccept(
                    offlinePlayer -> {
                        Hooks.getVaultHook().getApi().depositPlayer(offlinePlayer, Config.DELETE_WARP_REFUND.asInteger());
                        player.sendMessage(Lang.WARP_REMOVED_WITH_REFUND.asColoredString().replace("%warp%", warp.getName()).replace("%refund%", Config.DELETE_WARP_REFUND.asString()));
                    }
            );
        } else player.sendMessage(Lang.WARP_REMOVED.asColoredString().replace("%warp%", warp.getName()));

        return true;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.REMOVE_WARP;
    }

    @Override
    public int getFee() {
        return 0;
    }

    @Override
    public Lang getInputText() {
        return null;
    }

    @Override
    public boolean isPublicAction() {
        return false;
    }
}
