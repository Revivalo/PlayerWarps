package dev.revivalo.playerwarps.configuration.file;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.YamlFile;
import dev.revivalo.playerwarps.util.TextUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public enum Config {
    LANGUAGE,
    WARP_NAME_FORMAT,
    BANNED_ITEMS,
    DISABLED_WORLDS,
    UPDATE_CHECKER,
    AUTOSAVE_ENABLED,
    AUTOSAVE_ANNOUNCE,
    AUTOSAVE_INTERVAL,
    WARP_CREATION_NOTIFICATION,
    WARP_VISIT_NOTIFICATION,
    TELEPORT_DELAY,
    ALLOW_COLORS_IN_WARP_DISPLAY_NAMES,
    WARP_PRICE,
    DATE_FORMAT,
    SORT_BY,
    ENABLE_HINTS,
    DELETE_WARP_REFUND,
    WARP_NAME_MAX_LENGTH,
    MAX_WARP_ADMISSION,
    RELOCATE_WARP_FEE,
    RENAME_WARP_FEE,
    TRANSFER_OWNERSHIP_FEE,
    SET_STATUS_FEE,
    DEFAULT_WARP_STATUS,
    SET_TYPE_FEE,
    SET_PREVIEW_ITEM_FEE,
    SET_DESCRIPTION_FEE,
    SET_DISPLAY_NAME_FEE,
    SET_ADMISSION_FEE,
    DEFAULT_WARP_ITEM,
    DEFAULT_LIMIT_SIZE,
    ALLOW_ACCEPT_TELEPORT_MENU,
    CONFIRM_ITEM,
    DENY_ITEM,
    NO_WARP_FOUND_ITEM,
    INSUFFICIENT_PERMISSIONS_ITEM,
    FAVORITE_WARPS_ITEM,
    CATEGORIES_BACKGROUND_ITEM,
    WARP_LIST_ITEM,
    SEARCH_WARP_ITEM,
    SORT_WARPS_ITEM,
    MY_WARPS_ITEM,
    HELP_ITEM,
    STAR_REVIEW_ITEM,
    SET_PRICE_ITEM,
    SET_CATEGORY_ITEM,
    CHANGE_DISPLAY_NAME_ITEM,
    CHANGE_PREVIEW_ITEM,
    CHANGE_DESCRIPTION_ITEM,
    CHANGE_ACCESSIBILITY_ITEM,
    RENAME_WARP_ITEM,
    REMOVE_WARP_ITEM,
    RELOCATE_WARP_ITEM,
    CHANGE_OWNER_ITEM,
    NEXT_PAGE_ITEM,
    PREVIOUS_PAGE_ITEM,
    ENABLE_CATEGORIES,
    ENABLE_WARP_SEARCH,
    ENABLE_WARP_RATING,
    CURRENCY_SYMBOL,
    SET_PRICE_POSITION,
    SET_CATEGORY_POSITION,
    CHANGE_DISPLAY_NAME_POSITION,
    CHANGE_PREVIEW_ITEM_POSITION,
    CHANGE_DESCRIPTION_POSITION,
    CHANGE_ACCESSIBILITY_POSITION,
    REMOVE_WARP_POSITION,
    RENAME_WARP_POSITION,
    RELOCATE_WARP_POSITION,
    CHANGE_OWNER_POSITION,
    BACK_ITEM;

    private static final YamlFile configYamlFile = new YamlFile(
            "config.yml",
            PlayerWarpsPlugin.get().getDataFolder(),
            YamlFile.UpdateMethod.EVERYTIME);
    private static final Map<String, String> strings = new HashMap<>();
    private static final Map<String, List<String>> lists = new HashMap<>();

    static {
        reload();
    }

    public static void reload() {
        configYamlFile.reload();
        final ConfigurationSection configuration = configYamlFile.getConfiguration().getConfigurationSection("config");

        configuration
                .getKeys(false)
                .forEach(key -> {
                    String editedKey = key.toUpperCase(Locale.ENGLISH).replace("-", "_");
                    if (configuration.isList(key)) {
                        lists.put(editedKey, configuration.getStringList(key));
                    } else
                        strings.put(editedKey, configuration.getString(key));
                });

        Lang.reload(LANGUAGE);
    }

    public YamlFile getConfiguration() {
        return null;
    }

    public int asInteger() {
        return Integer.parseInt(strings.get(this.name()));}

    public String asString() {
        return strings.get(this.name());
    }

    public String asReplacedString(Map<String, String> definitions) {
        return TextUtil.replaceString(strings.get(this.name()), definitions);
    }

//    public Map<String, String> asStringMap() {
//        Map<String, String> map = new HashMap<>();
//        ConfigurationSection section = configYamlFile.getConfiguration().getConfigurationSection(asString());
//        if (section == null) return map;
//        for (String key : section.getKeys(false)) {
//            map.put(key, section.getString(key));
//        }
//        return map;
//    }

    public String asUppercase() {
        return this.asString().toUpperCase();
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(asString());
    }

    public List<String> asList() {
        return lists.get(this.name());
    }

    public long asLong() {
        return Long.parseLong(strings.get(this.name()));
    }
}