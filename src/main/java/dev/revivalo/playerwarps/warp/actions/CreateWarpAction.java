package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.hooks.Hooks;
import dev.revivalo.playerwarps.utils.ItemUtils;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class CreateWarpAction implements WarpAction<String> {
    @Override
    public boolean execute(Player player, Warp warp, String name) {
        if (!PlayerWarpsPlugin.getWarpHandler().canHaveWarp(player)) {
            player.sendMessage(Lang.LIMIT_REACHED.asColoredString()
                    .replace(
                            "%limit%",
                            String.valueOf(PlayerWarpsPlugin.getWarpHandler().getAmount(player, Config.DEFAULT_LIMIT_SIZE.asInt()))
                    )
            );
            return false;
        }

        final String worldName = Objects.requireNonNull(player.getLocation().getWorld()).getName();
        if (PlayerWarpsPlugin.getWarpHandler().getBannedWorlds().contains(worldName)
                && PermissionUtils.hasPermission(player, PermissionUtils.Permission.ADMIN_PERMISSION)) {
            player.sendMessage(Lang.TRIED_TO_CREATE_PWARP_IN_DISABLED_WORLD.asColoredString().replace("%world%", worldName));
            return false;
        }

        if (PlayerWarpsPlugin.getWarpHandler().existsWarp(name)) {
            player.sendMessage(Lang.WARP_ALREADY_CREATED.asColoredString());
            return false;
        }

        int limit = Config.WARP_NAME_MAX_LENGTH.asInt();
        if (name.length() > limit) {
            player.sendMessage(Lang.WARP_NAME_IS_ABOVE_LETTER_LIMIT.asColoredString().replace("%limit%", String.valueOf(limit)));
            return false;
        }

        if (/*warpName.contains(".") ||*/ name.contains(" ")) {
            player.sendMessage(Lang.NAME_CANT_CONTAINS_SPACE.asColoredString());
            return false;
        }

//        if (PlayerWarpsPlugin.getECONOMY() != null) {
//            int price = Config.WARP_PRICE.asInt();
//            if (!PlayerWarpsPlugin.getECONOMY().withdrawPlayer(player, price).transactionSuccess()) {
//                player.sendMessage(Lang.INSUFFICIENT_BALANCE.asColoredString());
//                return;
//            }
//        }

        final UUID ownerID = player.getUniqueId();
        final UUID warpID = UUID.randomUUID();
        final Location loc = player.getLocation();

        PlayerWarpsPlugin.getWarpHandler().addWarp(new Warp(
                new HashMap<String, Object>() {{
                    put("uuid", warpID.toString());
                    put("name", name);
                    put("displayName", name);
                    put("owner-id", ownerID.toString());
                    put("loc", loc);
                    put("ratings", 0);
                    put("visits", 0);
                    put("category", "all");
                    put("description", null);
                    put("admission", 0);
                    put("reviewers", Collections.emptyList());
                    put("todayVisits", 0);
                    put("date-created", System.currentTimeMillis());
                    put("item", ItemUtils.getItem(Config.DEFAULT_WARP_ITEM.asString()).getType().name());
                    put("status", "OPENED");
                }}

        ));

        if (Hooks.isHookEnabled(Hooks.getVaultHook()))
            player.sendMessage(Lang.WARP_CREATED_WITH_PRICE.asColoredString()
                    .replace("%name%", name)
                    .replace("%price%", String.valueOf(getFee())));
        else player.sendMessage(Lang.WARP_CREATED.asColoredString().replace("%name%", name));

        return true;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.CREATE_WARP;
    }

    @Override
    public int getFee() {
        return Config.WARP_PRICE.asInt();
    }
}
