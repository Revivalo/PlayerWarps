package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.Warp;
import org.bukkit.entity.Player;

public class VerifyWarpAction implements WarpAction<Boolean> {
    @Override
    public boolean execute(Player player, Warp warp, Boolean data) {

        warp.setVerificationNeeded(data);

        return true;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.VERIFY;
    }
}
