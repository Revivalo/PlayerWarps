package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RelocateAction implements WarpAction<Void> {
    @Override
    public void execute(Player player, Warp warp, Void data) {
        warp.setLocation(player.getLocation());
        player.sendMessage(Lang.WARP_RELOCATED.asReplacedString(new HashMap<String, String>() {{
            put("%warp%", warp.getName());
        }}));
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.RELOCATE_WARP;
    }

    @Override
    public int getFee() {
        return Config.RELOCATE_WARP_FEE.asInt();
    }
}
