package cz.revivalo.playerwarps.categories;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class Category {

    private final String type;
    private final boolean isDefault;
    private final String name;
    private final ItemStack item;
    private final int position;
    private final List<String> lore;

    public Category(String type, boolean isDefault, String name, ItemStack item, int position, List<String> lore) {
        this.type = type;
        this.isDefault = isDefault;
        this.name = name;
        this.item = item;
        this.position = position;
        this.lore = lore;
    }
    @Override
    public String toString(){return getType();}
}
