package dev.revivalo.playerwarps.categories;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Category {

    private final String type;
    private boolean defaultCategory;
    private final String name;
    @Nullable private final String permission;
    private final ItemStack item;
    private final int position;
    private final List<String> lore;

    public Category(String type, boolean defaultCategory, String name, @Nullable String permission, ItemStack item, int position, List<String> lore) {
        this.type = type;
        this.defaultCategory = defaultCategory;
        this.name = name;
        this.permission = permission;
        this.item = item;
        this.position = position;
        this.lore = lore;
    }

    @Override
    public String toString(){return getType();}

    public String getType() {
        return type;
    }

    public boolean isDefaultCategory() {
        return defaultCategory;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getPosition() {
        return position;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setDefaultCategory(boolean defaultCategory) {
        this.defaultCategory = defaultCategory;
    }

    @Nullable
    public String getPermission() {
        return permission;
    }

    public boolean hasPermission(Player player) {
        return permission == null || player.hasPermission(permission);
    }

    // TODO: equals()
}
