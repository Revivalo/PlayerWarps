package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.utils.TextUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ChangeDisplayNameAction implements WarpAction<String> {
    @Override
    public boolean execute(Player player, Warp warp, String text) {
        int textLength = text.length();
        if (textLength < 3 || textLength > 32) {
            player.sendMessage(Lang.TEXT_SIZE_ERROR.asColoredString());
            return false;
        }
        warp.setDisplayName(TextUtils.getColorizedString(player, text));
        player.sendMessage(Lang.DISPLAY_NAME_CHANGED.asReplacedString(player, new HashMap<String, String>() {{
            put("%warp%", warp.getName());
            put("%displayName%", warp.getDisplayName());
        }}));

        return true;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.CHANGE_DISPLAY_NAME;
    }

    @Override
    public int getFee() {
        return Config.SET_DISPLAY_NAME_FEE.asInteger();
    }
}
