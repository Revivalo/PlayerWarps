package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.hook.HookManager;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.util.PlayerUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveWarpAction implements WarpAction<Void> {
    @Override
    public boolean execute(Player player, Warp warp, Void data) {
        PlayerWarpsPlugin.getWarpHandler().removeWarp(warp);
        HookManager.getDynmapHook().removeMarker(warp);
        HookManager.getBlueMapHook().removeMarker(warp);
        if (HookManager.isHookEnabled(HookManager.getVaultHook())) {
            PlayerUtil.getOfflinePlayer(warp.getOwner()).thenAccept(
                    offlinePlayer -> {
                        HookManager.getVaultHook().getApi().depositPlayer(offlinePlayer, Config.DELETE_WARP_REFUND.asInteger());
                        player.sendMessage(Lang.WARP_REMOVED_WITH_REFUND.asColoredString().replace("%warp%", warp.getName()).replace("%refund%", Config.DELETE_WARP_REFUND.asString()));
                    }
            );
        } else player.sendMessage(Lang.WARP_REMOVED.asColoredString().replace("%warp%", warp.getName()));

        return true;
    }

    public boolean execute(CommandSender sender, Warp warp) {
        PlayerWarpsPlugin.getWarpHandler().removeWarp(warp);
        HookManager.getDynmapHook().removeMarker(warp);
        sender.sendMessage(Lang.WARP_REMOVED.asColoredString().replace("%warp%", warp.getName()));

        return true;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.REMOVE_WARP;
    }
}
