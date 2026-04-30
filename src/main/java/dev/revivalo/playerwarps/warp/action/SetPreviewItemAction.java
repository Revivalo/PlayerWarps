package dev.revivalo.playerwarps.warp.action;

import com.cryptomorin.xseries.XMaterial;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SetPreviewItemAction implements WarpAction<String> {
    private static final Set<Material> BANNED_ITEMS;
    @Override
    public boolean execute(Player player, Warp warp, String item) {
        try {
            ItemStack displayItem;
            String normalizedItem = normalizeItemName(item);
            if (normalizedItem.equalsIgnoreCase("HAND")) {
                displayItem = player.getInventory().getItemInMainHand().clone();
            } else {
                Optional<Material> material = matchMaterial(normalizedItem);
                if (material.isEmpty()) {
                    player.sendMessage(Lang.INVALID_ITEM.asColoredString());
                    return false;
                }
                displayItem = new ItemStack(material.get());
            }

            ItemMeta meta = displayItem.getItemMeta();
            if (meta != null) {
                meta.addItemFlags(ItemFlag.values());
                displayItem.setItemMeta(meta);
            }

            String itemName = displayItem.getType().name().toLowerCase(Locale.ENGLISH);

            if (BANNED_ITEMS.contains(displayItem.getType())) {
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
        } catch (IllegalArgumentException | NullPointerException exception) {
            player.sendMessage(Lang.INVALID_ITEM.asColoredString());
            return false;
        }

        return true;
    }

    private Optional<Material> matchMaterial(String itemName) {
        Material material = Material.matchMaterial(itemName);
        if (material == null) {
            material = XMaterial.matchXMaterial(itemName)
                    .map(XMaterial::parseMaterial)
                    .orElse(null);
        }
        return Optional.ofNullable(material);
    }

    private static String normalizeItemName(String item) {
        String normalized = Objects.requireNonNull(item, "item").trim();

        normalized = ChatColor.stripColor(normalized);
        if (normalized == null) {
            return "";
        }

        normalized = stripWrappingQuotes(normalized.trim());
        if (normalized.toLowerCase(Locale.ENGLISH).startsWith("minecraft:")) {
            normalized = normalized.substring("minecraft:".length());
        }

        return normalized
                .replace(' ', '_')
                .replace('-', '_');
    }

    private static String stripWrappingQuotes(String item) {
        if (item.length() < 2) {
            return item;
        }

        char first = item.charAt(0);
        char last = item.charAt(item.length() - 1);

        if ((first == '"' && last == '"') || (first == '\'' && last == '\'')
                || (first == '`' && last == '`') || (first == '\u201c' && last == '\u201d')
                || (first == '\u2018' && last == '\u2019')) {
            return item.substring(1, item.length() - 1).trim();
        }

        return item;
    }

    @Override
    public boolean hasInput() {
        return true;
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.SET_PREVIEW_ITEM;
    }

    @Override
    public Lang getMessage() {
        return Lang.ITEM_WRITE_MSG;
    }

    @Override
    public int getFee() {
        return Config.SET_PREVIEW_ITEM_FEE.asInteger();
    }

    static {
        BANNED_ITEMS = new HashSet<>();
        BANNED_ITEMS.add(Material.NETHER_PORTAL);
        BANNED_ITEMS.add(Material.END_PORTAL);
        BANNED_ITEMS.add(Material.AIR);
        BANNED_ITEMS.addAll(Config.BANNED_ITEMS.asList().stream()
                .map(SetPreviewItemAction::normalizeItemName)
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }
}
