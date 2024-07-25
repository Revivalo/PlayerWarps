package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SetPreviewItemAction implements WarpAction<String> {
    private static final Set<Material> bannedItems;
    @Override
    public boolean execute(Player player, Warp warp, String item) {
        try {
            ItemStack displayItem = new ItemStack(Material.valueOf(item.toUpperCase()));
            if (bannedItems.contains(displayItem.getType())) {
                player.sendMessage(Lang.TRIED_TO_SET_BANNED_ITEM.asColoredString());
                return false;
            } else {
                String iconPermission = "playerwarps.icon." + item.toLowerCase(Locale.ENGLISH);
                if (!player.hasPermission(iconPermission)) {
                    player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString().replace("%permission%", iconPermission));
                    return false;
                }

                warp.setMenuItem(displayItem);
                player.sendMessage(Lang.ITEM_CHANGED.asColoredString().replace("%item%", item));
            }
        } catch (IllegalArgumentException exception) {
            player.sendMessage(Lang.INVALID_ITEM.asColoredString());
            return false;
        }

        return true;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.SET_PREVIEW_ITEM;
    }

    @Override
    public int getFee() {
        return Config.SET_PREVIEW_ITEM_FEE.asInteger();
    }

    @Override
    public Lang getInputText() {
        return null;
    }

    @Override
    public boolean isPublicAction() {
        return false;
    }

    static {
        bannedItems = new HashSet<>();
        bannedItems.add(Material.NETHER_PORTAL);
        bannedItems.add(Material.END_PORTAL);
        bannedItems.add(Material.AIR);
        bannedItems.addAll(Config.BANNED_ITEMS.asReplacedList(Collections.emptyMap()).stream().map(Material::valueOf).collect(Collectors.toList()));
    }
}