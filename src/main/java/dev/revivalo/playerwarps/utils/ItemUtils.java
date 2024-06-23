package dev.revivalo.playerwarps.utils;

import dev.dbassett.skullcreator.SkullCreator;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.hooks.Hooks;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

public final class ItemUtils {
    private static final String CUSTOM_MODEL_PREFIX = "CUSTOMMODEL";

    public static final ItemStack ONE_STAR = ItemBuilder.from(ItemUtils.getItem(Config.STAR_REVIEW_ITEM.asString()).getType()).amount(1).setName(Lang.ONE_STAR.asColoredString()).build();
    public static final ItemStack TWO_STARS = ItemBuilder.from(ItemUtils.getItem(Config.STAR_REVIEW_ITEM.asString()).getType()).amount(2).setName(Lang.TWO_STARS.asColoredString()).build();
    public static final ItemStack THREE_STARS = ItemBuilder.from(ItemUtils.getItem(Config.STAR_REVIEW_ITEM.asString()).getType()).amount(3).setName(Lang.THREE_STARS.asColoredString()).build();
    public static final ItemStack FOUR_STARS = ItemBuilder.from(ItemUtils.getItem(Config.STAR_REVIEW_ITEM.asString()).getType()).amount(4).setName(Lang.FOUR_STARS.asColoredString()).build();
    public static final ItemStack FIVE_STARS = ItemBuilder.from(ItemUtils.getItem(Config.STAR_REVIEW_ITEM.asString()).getType()).amount(5).setLore(Lang.FIVE_STARS.asColoredString()).build();

    public static ItemStack getItem(String name) {
        if (name == null) {
            return new ItemStack(Material.COOKED_BEEF);
        }

        if (name.toUpperCase(Locale.ENGLISH).startsWith(CUSTOM_MODEL_PREFIX)) {
            String materialStr = name.substring(name.indexOf('[') + 1, name.indexOf(']'));
            String dataStr = name.substring(name.indexOf('{') + 1, name.indexOf('}'));
            Material material = Material.valueOf(materialStr.toUpperCase(Locale.ENGLISH));
            int data = Integer.parseInt(dataStr);

            final ItemStack itemStack = new ItemStack(material);
            final ItemMeta meta = itemStack.getItemMeta();
            meta.setCustomModelData(data);
            itemStack.setItemMeta(meta);
            return itemStack;
        } else if (name.length() > 64) {
            return SkullCreator.itemFromBase64(name);
        } else if (Hooks.getItemsAdderHook().isOn() && ItemsAdder.isCustomItem(name)) {
            return CustomStack.getInstance(name).getItemStack();
        } else if (Hooks.getOraxenHook().isOn() && OraxenItems.exists(name)) {
            return OraxenItems.getItemById(name).build();
        } else {
            return new ItemStack(Material.valueOf(name.toUpperCase())); //XMaterial.matchXMaterial(name).orElse(XMaterial.STONE).parseItem();
        }
    }
}