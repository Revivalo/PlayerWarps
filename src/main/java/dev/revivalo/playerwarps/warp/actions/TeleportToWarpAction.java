package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.utils.PlayerUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class TeleportToWarpAction implements WarpAction<String> {
    @Override
    public boolean execute(Player player, Warp warp, String password) {
        if (warp == null)
            return false;

        final String warpName = warp.getName();
        boolean isOwner = warp.canManage(player);
        boolean hasBypass = PermissionUtils.hasPermission(player, PermissionUtils.Permission.BYPASS_TELEPORT_DELAY);

        if (!warp.isAccessible() && !isOwner) {
            player.sendMessage(Lang.WARP_IS_DISABLED.asColoredString().replace("%warp%", warpName));
            return false;
        }

        PlayerUtils.teleportPlayer(player, warp.getLocation(), hasBypass);

        final UUID ownerID = warp.getOwner();

        if (warp.getAdmission() != 0 && !isOwner) {
            player.sendMessage(Lang.TELEPORT_TO_WARP_WITH_ADMISSION.asColoredString()
                    .replace("%price%", String.valueOf(warp.getAdmission()))
                    .replace("%warp%", warpName)
                    .replace("%player%", Objects.requireNonNull(Bukkit.getOfflinePlayer(ownerID).getName())));
        } else
            player.sendMessage(Lang.TELEPORT_TO_WARP.asColoredString()
                    .replace("%warp%", warpName)
                    .replace("%player%", Objects.requireNonNull(Bukkit.getOfflinePlayer(ownerID).getName())));
        if (!isOwner) {
            warp.setVisits(warp.getVisits() + 1);
            warp.setTodayVisits(warp.getTodayVisits() + 1);
        }

        if (Config.WARP_VISIT_NOTIFICATION.asBoolean()) PlayerUtils.announce(Lang.WARP_VISIT_NOTIFICATION.asColoredString()
                .replace("%warp%", warpName)
                .replace("%player%", player.getName())
        );

        return true;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.USE;
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
        return true;
    }
}
