package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;

public class RenameAction implements WarpAction<String> {
    @Override
    public boolean execute(Player player, Warp warp, String newName) {
        int limit = Config.WARP_NAME_MAX_LENGTH.asInteger();
        if (newName.length() > limit) {
            player.sendMessage(Lang.WARP_NAME_IS_ABOVE_LETTER_LIMIT.asColoredString().replace("%limit%", String.valueOf(limit)));
            return false;
        }

        if (newName.contains(" ")) {
            player.sendMessage(Lang.NAME_CANT_CONTAINS_SPACE.asColoredString());
            return false;
        }

        warp.setName(newName);
        player.sendMessage(Lang.WARP_RENAMED.asColoredString().replace("%oldName%", warp.getName()).replace("%newName%", newName));

        return true;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.RENAME_WARP;
    }

    @Override
    public int getFee() {
        return Config.RENAME_WARP_FEE.asInteger();
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
