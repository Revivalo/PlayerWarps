package dev.revivalo.playerwarps.warp.action;

import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SetPreviewItemAction implements WarpAction<String> {
    private static final Set<Material> bannedItems;
    @Override
    public boolean execute(Player player, Warp warp, String item) {
        try {
            ItemStack displayItem;
            if (item.equalsIgnoreCase("HAND")) {
                displayItem = new ItemStack(player.getInventory().getItemInMainHand().getType());
            } else {
                displayItem = new ItemStack(Material.valueOf(item.toUpperCase()));
            }

            ItemMeta meta = displayItem.getItemMeta();
            if (meta != null) {
                meta.addItemFlags(ItemFlag.values());
                displayItem.setItemMeta(meta);
            }

            String itemName = displayItem.getType().name().toLowerCase(Locale.ENGLISH);

            if (bannedItems.contains(displayItem.getType())) {
                player.sendMessage(Lang.TRIED_TO_SET_BANNED_ITEM.asColoredString());
                return false;
            } else {
                if (!player.hasPermission("playerwarps.icon.*")) {
                    String iconPermission = "playerwarps.icon." + itemName;
                    if (!player.hasPermission(iconPermission)) {
                        player.sendMessage(Lang.INSUFFICIENT_PERMISSIONS.asColoredString().replace("%permission%", iconPermission));
                        return false;
                    }
                }

                warp.setMenuItem(displayItem);
                player.sendMessage(Lang.ITEM_CHANGED.asColoredString().replace("%item%", itemName));
            }
        } catch (IllegalArgumentException exception) {
            player.sendMessage(Lang.INVALID_ITEM.asColoredString());
            return false;
        }

        return true;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.SET_PREVIEW_ITEM;
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
        bannedItems.addAll(Config.BANNED_ITEMS.asList().stream().map(Material::valueOf).collect(Collectors.toList()));
    }
}