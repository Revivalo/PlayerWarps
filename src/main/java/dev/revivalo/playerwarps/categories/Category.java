package dev.revivalo.playerwarps.categories;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Category {

    private final String type;
    private boolean defaultCategory;
    private final String name;
    private final ItemStack item;
    private final int position;
    private final List<String> lore;

    public Category(String type, boolean defaultCategory, String name, ItemStack item, int position, List<String> lore) {
        this.type = type;
        this.defaultCategory = defaultCategory;
        this.name = name;
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

    // TODO: equals()
}
