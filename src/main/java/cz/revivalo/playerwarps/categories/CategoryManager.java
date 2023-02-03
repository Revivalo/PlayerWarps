package cz.revivalo.playerwarps.categories;

import cz.revivalo.playerwarps.PlayerWarps;
import cz.revivalo.playerwarps.configuration.YamlFile;
import cz.revivalo.playerwarps.configuration.enums.Lang;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CategoryManager {
    @Setter
    private static HashMap<String, Category> categoriesMap;

    public static void loadCategories(){
        final YamlConfiguration categoriesData = new YamlFile("categories.yml",
                PlayerWarps.getPlugin().getDataFolder())
                .getConfiguration();

        final HashMap<String, Category> categories = new HashMap<>();
        final ConfigurationSection categoriesSection = categoriesData.getConfigurationSection("categories");
        categoriesSection
                .getKeys(false)
                .stream().map(categoriesSection::getConfigurationSection).filter(Objects::nonNull).forEach(categorySection ->
                        categories.put(
                            categorySection.getName(),
                            new Category(
                                categorySection.getName(),
                                categorySection.getBoolean("default"),
                                Lang.applyColor(categorySection.getString("name")),
                                new ItemStack(Material.valueOf(categorySection.getString("item").toUpperCase(Locale.ENGLISH))),
                                categorySection.getInt("position"),
                                Lang.applyColor(categorySection.getStringList("lore"))
                        )
                ));

        setCategoriesMap(categories);
    }

    public static Category getCategoryFromName(String categoryName){
        return categoriesMap.get(categoryName);
    }

    public static Collection<Category> getCategories(){
        return categoriesMap.values();
    }
}
