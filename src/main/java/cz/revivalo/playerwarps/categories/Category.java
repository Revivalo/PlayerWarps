package cz.revivalo.playerwarps.categories;

import java.util.List;

public class Category {

    private final String type;
    private final String name;
    private final String item;
    private final int position;
    private final List<String> lore;

    public Category(String type, String name, String item, int position, List<String> lore) {
        this.type = type;
        this.name = name;
        this.item = item;
        this.position = position;
        this.lore = lore;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getItem() {
        return item;
    }

    public int getPosition() {
        return position;
    }

    public List<String> getLore() {
        return lore;
    }
}
