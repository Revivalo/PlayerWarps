package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.playerconfig.PlayerConfig;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FavoriteWarpAction implements WarpAction<Void> {
    @Override
    public boolean execute(Player player, Warp warp, Void data) {
        final PlayerConfig playerData = PlayerConfig.getConfig(player);
        final List<String> favorites = playerData.getStringList("favorites");
        final String warpString = warp.toString();

        if (favorites.contains(warpString)) {
            favorites.remove(warpString);
            player.sendMessage(Lang.REMOVE_FAVORITE.asColoredString().replace("%warp%", warp.getName()));
        } else {
            favorites.add(warpString);
            player.sendMessage(Lang.ADD_FAVORITE.asColoredString().replace("%warp%", warp.getName()));
        }

        playerData.set("favorites", favorites);
        playerData.save();

        return true;
    }


    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.FAVORITE_WARP;
    }

    @Override
    public int getFee() {
        return 0;
    }
}
