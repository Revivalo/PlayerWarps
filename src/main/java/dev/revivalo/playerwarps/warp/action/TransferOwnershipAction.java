package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TransferOwnershipAction implements WarpAction<Player> {
    @Override
    public boolean execute(Player player, Warp warp, Player newOwner) {
        if (newOwner != null && (newOwner.isOnline() || newOwner.hasPlayedBefore())) {
            if (Objects.equals(newOwner, player)) {
                player.sendMessage(Lang.ALREADY_OWNING.asColoredString());
                return false;
            }

            if (PlayerWarpsPlugin.getWarpHandler().canHaveWarp(newOwner)) {
                player.sendMessage(Lang.LIMIT_REACHED_OTHER.asColoredString().replace("%player%", newOwner.getName()));
                return false;
            }

            warp.setOwner(newOwner.getUniqueId());
            player.sendMessage(Lang.TRANSFER_SUCCESSFUL.asColoredString()
                    .replace("%player%", newOwner.getName())
                    .replace("%warp%", warp.getName()));
            if (newOwner.isOnline()) {
                newOwner.sendMessage(Lang.TRANSFER_INFO.asColoredString()
                        .replace("%player%", player.getName())
                        .replace("%warp%", warp.getName()));
            }

        } else {
            player.sendMessage(Lang.TRANSFER_ERROR.asColoredString());
            return false;
        }

        return true;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.TRANSFER_WARP;
    }

    @Override
    public int getFee() {
        return Config.TRANSFER_OWNERSHIP_FEE.asInteger();
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
