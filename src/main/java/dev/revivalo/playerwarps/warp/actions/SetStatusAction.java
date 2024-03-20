package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import dev.revivalo.playerwarps.warp.WarpState;
import org.bukkit.entity.Player;

public class SetStatusAction implements WarpAction<WarpState> {
    @Override
    public boolean execute(Player player, Warp warp, WarpState warpState) {

        return true;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.SET_STATUS;
    }

    @Override
    public int getFee() {
        return Config.SET_STATUS_FEE.asInt();
    }
}
