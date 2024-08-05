package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;

public class RelocateAction implements WarpAction<Void> {
    @Override
    public boolean execute(Player player, Warp warp, Void data) {
        final String worldName = Objects.requireNonNull(player.getLocation().getWorld()).getName();
        if (PlayerWarpsPlugin.getWarpHandler().getBannedWorlds().contains(worldName)
                && !PermissionUtils.hasPermission(player, PermissionUtils.Permission.ADMIN_PERMISSION)) {
            player.sendMessage(Lang.TRIED_TO_RELOCATE_PWARP_TO_DISABLED_WORLD.asColoredString().replace("%world%", worldName));
            return false;
        }

        warp.setLocation(player.getLocation());
        player.sendMessage(Lang.WARP_RELOCATED.asReplacedString(player, new HashMap<String, String>() {{
            put("%warp%", warp.getName());
        }}));

        return true;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.RELOCATE_WARP;
    }

    @Override
    public int getFee() {
        return Config.RELOCATE_WARP_FEE.asInteger();
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
