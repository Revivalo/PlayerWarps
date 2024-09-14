package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.hook.HookManager;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import dev.revivalo.playerwarps.warp.checker.BentoBoxIslandChecker;
import dev.revivalo.playerwarps.warp.checker.Checker;
import dev.revivalo.playerwarps.warp.checker.ResidenceChecker;
import org.bukkit.entity.Player;

import java.util.*;

public class RelocateAction implements WarpAction<Void> {
    private static final List<Checker> checkers = new ArrayList<>();
    static {
        if (HookManager.isHookEnabled(HookManager.getBentoBoxHook())) checkers.add(new BentoBoxIslandChecker());
        if (HookManager.isHookEnabled(HookManager.getResidenceHook())) checkers.add(new ResidenceChecker());
    }

    @Override
    public boolean execute(Player player, Warp warp, Void data) {
        final String worldName = Objects.requireNonNull(player.getLocation().getWorld()).getName();
        if (PlayerWarpsPlugin.getWarpHandler().getBannedWorlds().contains(worldName)
                && !PermissionUtil.hasPermission(player, PermissionUtil.Permission.ADMIN_PERMISSION)) {
            player.sendMessage(Lang.TRIED_TO_RELOCATE_WARP_TO_DISABLED_WORLD.asColoredString().replace("%world%", worldName));
            return false;
        }

        for (Checker checker : checkers) {
            if (!checker.validate(player)) {
                return false;
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
