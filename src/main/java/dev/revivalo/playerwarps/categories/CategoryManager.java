package dev.revivalo.playerwarps.categories;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.YamlFile;
import dev.revivalo.playerwarps.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CategoryManager {
    private static HashMap<String, Category> categoriesMap;

    public static void loadCategories(){
        final YamlConfiguration categoriesData = new YamlFile("categories.yml",
                PlayerWarpsPlugin.get().getDataFolder(),
                YamlFile.UpdateMethod.ON_LOAD)
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
                                    TextUtils.getColorizedString(null, categorySection.getString("name")),
                                new ItemStack(Material.valueOf(categorySection.getString("item").toUpperCase(Locale.ENGLISH))),
                                categorySection.getInt("position"),
                                TextUtils.getColorizedList(null, categorySection.getStringList("lore"))
                        )
                ));

        setCategoriesMap(categories);

        if (!getDefaultCategory().isPresent()) {
            Category category = categories.get("all");
            category.setDefaultCategory(true);
        }
    }

    public static Category getCategoryFromName(String categoryName){
        return categoriesMap.get(categoryName != null ? categoryName : "all");
    }

    public static Optional<Category> getDefaultCategory() {
        return categoriesMap.values().stream().filter(Category::isDefaultCategory).findFirst();
    }

    public static Optional<Category> getCategory(String categoryType) {
        return getCategories().stream().filter(category -> category.getType().equalsIgnoreCase(categoryType)).findFirst();
    }

    public static boolean isCategory(String categoryName) {
        return getCategories().stream().anyMatch(category -> category.getType().equalsIgnoreCase(categoryName));
    }

    public static Collection<Category> getCategories(){
        return categoriesMap.values();
    }

    public static void setCategoriesMap(HashMap<String, Category> categoriesMap) {
        CategoryManager.categoriesMap = categoriesMap;
    }
}
