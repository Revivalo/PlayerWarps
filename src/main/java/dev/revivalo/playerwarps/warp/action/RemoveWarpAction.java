package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.hook.Hook;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.util.PlayerUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;

public class RemoveWarpAction implements WarpAction<Void> {
    @Override
    public boolean execute(Player player, Warp warp, Void data) {
        PlayerWarpsPlugin.getWarpHandler().removeWarp(warp);
        if (Hook.isHookEnabled(Hook.getVaultHook())) {
            PlayerUtil.getOfflinePlayer(warp.getOwner()).thenAccept(
                    offlinePlayer -> {
                        Hook.getVaultHook().getApi().depositPlayer(offlinePlayer, Config.DELETE_WARP_REFUND.asInteger());
                        player.sendMessage(Lang.WARP_REMOVED_WITH_REFUND.asColoredString().replace("%warp%", warp.getName()).replace("%refund%", Config.DELETE_WARP_REFUND.asString()));
                    }
            );
        } else player.sendMessage(Lang.WARP_REMOVED.asColoredString().replace("%warp%", warp.getName()));

        return true;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.REMOVE_WARP;
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
