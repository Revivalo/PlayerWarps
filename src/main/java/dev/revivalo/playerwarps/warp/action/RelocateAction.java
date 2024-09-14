package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.hook.HookManager;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;
import world.bentobox.bentobox.database.objects.Island;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class RelocateAction implements WarpAction<Void> {
    @Override
    public boolean execute(Player player, Warp warp, Void data) {
        final String worldName = Objects.requireNonNull(player.getLocation().getWorld()).getName();
        if (PlayerWarpsPlugin.getWarpHandler().getBannedWorlds().contains(worldName)
                && !PermissionUtil.hasPermission(player, PermissionUtil.Permission.ADMIN_PERMISSION)) {
            player.sendMessage(Lang.TRIED_TO_RELOCATE_WARP_TO_DISABLED_WORLD.asColoredString().replace("%world%", worldName));
            return false;
        }

        if (Hook.isHookEnabled(Hook.getBentoBoxHook())) { //TODO: Do as a checker
            Optional<Island> islandOptional = Hook.getBentoBoxHook().getApi().getIslands().getIslandAt(player.getLocation());
            if (islandOptional.isPresent()) {
                Island island = islandOptional.get();
                if (island.isOwned()) {
                    if (!island.getOwner().equals(player.getUniqueId())) {
                        player.sendMessage(Lang.TRIED_TO_CREATE_WARP_IN_FOREIGN_ISLAND.asColoredString());
                        return false;
                    }
                }
            }
        }

        warp.setLocation(player.getLocation());
        player.sendMessage(Lang.WARP_RELOCATED.asReplacedString(player, new HashMap<String, String>() {{
            put("%warp%", warp.getName());
        }}));

        return true;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.RELOCATE_WARP;
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
