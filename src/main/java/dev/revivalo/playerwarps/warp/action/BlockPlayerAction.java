package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.util.PlayerUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;

import java.util.Objects;

public class BlockPlayerAction implements WarpAction<String> {
    @Override
    public boolean execute(Player player, Warp warp, String playerToBlockName) {
        PlayerUtil.getOfflinePlayer(playerToBlockName).thenAccept(offlinePlayer -> {
                    if (Objects.equals(player.getUniqueId(), offlinePlayer.getUniqueId())) {
                        player.sendMessage(Lang.CANT_BLOCK_YOURSELF.asColoredString());
                        return;
                    }

                    warp.block(offlinePlayer);
                }
        );

        return true;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.BLOCK_PLAYER;
    }

    @Override
    public Lang getMessage() {
        return Lang.BLOCKED_PLAYER_INPUT;
    }
}
