package dev.revivalo.playerwarps.util;

import com.cryptomorin.xseries.XMaterial;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.hook.HookManager;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Locale;
import java.util.UUID;

public final class ItemUtil {
    private static final String CUSTOM_MODEL_PREFIX = "CUSTOMMODEL";
    private static final String CUSTOM_SKULL_MODEL_PREFIX = "CUSTOMSKULL";

    public static final ItemStack ONE_STAR = ItemBuilder.from(ItemUtil.getItem(Config.STAR_REVIEW_ITEM.asString())).amount(1).setName(Lang.ONE_STARS.asColoredString()).build();
    public static final ItemStack TWO_STARS = ItemBuilder.from(ItemUtil.getItem(Config.STAR_REVIEW_ITEM.asString())).amount(2).setName(Lang.TWO_STARS.asColoredString()).build();
    public static final ItemStack THREE_STARS = ItemBuilder.from(ItemUtil.getItem(Config.STAR_REVIEW_ITEM.asString())).amount(3).setName(Lang.THREE_STARS.asColoredString()).build();
    public static final ItemStack FOUR_STARS = ItemBuilder.from(ItemUtil.getItem(Config.STAR_REVIEW_ITEM.asString())).amount(4).setName(Lang.FOUR_STARS.asColoredString()).build();
    public static final ItemStack FIVE_STARS = ItemBuilder.from(ItemUtil.getItem(Config.STAR_REVIEW_ITEM.asString())).amount(5).setName(Lang.FIVE_STARS.asColoredString()).build();

    public static ItemStack getItem(String name) {
        return getItem(name, (Player) null);
    }

    public static ItemStack getItem(String name, Player player) {
        return getItem(name, player == null ? null : player.getUniqueId());
    }

    public static ItemStack getItem(String name, UUID playerUUID) {
        if (name == null) {
            return new ItemStack(Material.STONE);
        }

        if (name.equalsIgnoreCase("air")) {
            return new ItemStack(Material.AIR);
        }

        String itemNameInUppercase = name.toUpperCase(Locale.ENGLISH);
        if (itemNameInUppercase.startsWith(CUSTOM_MODEL_PREFIX)) {
            String materialStr = name.substring(name.indexOf('[') + 1, name.indexOf(']'));
            String dataStr = name.substring(name.indexOf('{') + 1, name.indexOf('}'));
            Material material = Material.valueOf(materialStr.toUpperCase(Locale.ENGLISH));
            int data = Integer.parseInt(dataStr);

            final ItemStack itemStack = new ItemStack(material);
            final ItemMeta meta = itemStack.getItemMeta();
            meta.setCustomModelData(data);
            itemStack.setItemMeta(meta);
            return itemStack;
        } else if (itemNameInUppercase.startsWith(CUSTOM_SKULL_MODEL_PREFIX) && playerUUID != null) {
            String materialStr = name.substring(name.indexOf('[') + 1, name.indexOf(']'));
            String dataStr = name.substring(name.indexOf('{') + 1, name.indexOf('}'));
            Material material = Material.valueOf(materialStr.toUpperCase(Locale.ENGLISH));
            int data = Integer.parseInt(dataStr);

            final ItemStack itemStack = new ItemStack(material);
            final ItemMeta meta = itemStack.getItemMeta();
            final SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setCustomModelData(data);
            skullMeta.setOwningPlayer(PlayerWarpsPlugin.get().getServer().getOfflinePlayer(playerUUID));
            itemStack.setItemMeta(skullMeta);
            return itemStack;
        } else if (name.length() > 64) {
            return ItemBuilder.skull().texture(name).build();
        } else if (name.equalsIgnoreCase("skullofplayer") && playerUUID != null) {
            return ItemBuilder.skull().owner(Bukkit.getOfflinePlayer(playerUUID)).build(); // TODO: Rework
        } else if (HookManager.getItemsAdderHook().isOn() && ItemsAdder.isCustomItem(name)) {
//            if (Hooks.isHookEnabled(Hooks.getPlaceholderApiHook())) {
//                PlaceholderAPI.setPlaceholders(null, )
//            }
            return CustomStack.getInstance(name).getItemStack();
        } else if (HookManager.getOraxenHook().isOn() && OraxenItems.exists(name)) {
            return OraxenItems.getItemById(name).build();
        } else {
            if (Material.matchMaterial(name) != null) {
                ItemStack item = new ItemStack(Material.matchMaterial(name));
                ItemMeta meta = item.getItemMeta();
                meta.addItemFlags(ItemFlag.values());
                item.setItemMeta(meta);
                return item;
            } else {
                ItemStack item = XMaterial.matchXMaterial(name).orElse(XMaterial.STONE).parseItem();
                ItemMeta meta = item.getItemMeta();
                meta.addItemFlags(ItemFlag.values());
                item.setItemMeta(meta);
                return item;
            }
        }
    }
}