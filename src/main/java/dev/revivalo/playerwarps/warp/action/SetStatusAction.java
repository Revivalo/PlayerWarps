package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.PermissionUtil;
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
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.SET_STATUS;
    }

    @Override
    public int getFee() {
        return Config.SET_STATUS_FEE.asInteger();
    }
}
