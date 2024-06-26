package dev.revivalo.playerwarps.configuration.enums;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.YamlFile;
import dev.revivalo.playerwarps.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public enum Config {
    WARP_NAME_FORMAT("warp-name-format"),
    BANNED_ITEMS("banned-items"),
    BANNED_WORLDS("disabled-worlds"),
    UPDATE_CHECKER("update-checker"),
    AUTO_SAVE_ENABLED("autosave-enabled"),
    AUTO_SAVE_ANNOUNCE("autosave-announce"),
    AUTO_SAVE_INTERVAL("autosave-interval"),
    TELEPORTATION_DELAY("teleport-delay"),
    ALLOW_COLORS_IN_WARP_DISPLAY_NAMES("allow-colors-in-warp-display-names"),
    WARP_PRICE("warp-price"),
    DATE_FORMAT("date-format"),
    DELETE_WARP_REFUND("delete-warp-refund"),
    WARP_NAME_MAX_LENGTH("warp-name-max-length"),
    MAX_WARP_ADMISSION("max-warp-admission"),
    RELOCATE_WARP_FEE("relocate-warp-fee"),
    RENAME_WARP_FEE("rename-warp-fee"),
    TRANSFER_OWNERSHIP_FEE("transfer-ownership-fee"),
    SET_STATUS_FEE("set-status-fee"),
    SET_TYPE_FEE("set-type-fee"),
    SET_PREVIEW_ITEM_FEE("set-preview-item-fee"),
    SET_DESCRIPTION_FEE("set-description-fee"),
    SET_DISPLAY_NAME_FEE("set-display-name-fee"),
    SET_ADMISSION_FEE("set-admission-fee"),
    DEFAULT_WARP_ITEM("default-warp-item"),
    DEFAULT_LIMIT_SIZE("default-limit-size"),
    ALLOW_ACCEPT_TELEPORT_MENU("allow-teleport-accept-menu"),
    FAVORITE_WARPS_ITEM("favorite-warps-item"),
    CATEGORIES_BACKGROUND_ITEM("categories-background-item"),
    WARP_LIST_ITEM("warp-list-item"),
    SORT_WARPS_ITEM("sort-warps-item"),
    MY_WARPS_ITEM("my-warps-item"),
    PAGE_ITEM("page-item"),
    STAR_REVIEW_ITEM("star-review-item"),
    ENABLE_CATEGORIES("enable-categories"),
    CURRENCY_SYMBOL("currency-symbol"),
    SET_PRICE_POSITION("set-price-position"),
    SET_CATEGORY_POSITION("set-category-position"),
    CHANGE_DISPLAY_NAME_POSITION("change-display-name-position"),
    CHANGE_PREVIEW_ITEM_POSITION("change-preview-item-position"),
    CHANGE_DESCRIPTION_POSITION("change-description-position"),
    CHANGE_ACCESSIBILITY_POSITION("change-accessibility-position"),
    REMOVE_WARP_POSITION("remove-warp-position"),
    RENAME_WARP_POSITION("rename-warp-position"),
    RELOCATE_WARP_POSITION("relocate-warp-position"),
    CHANGE_OWNER_POSITION("change-owner-position"),
    BACK_ITEM("back-item");

    private static final YamlFile configYamlFile = new YamlFile(
            "config.yml",
            PlayerWarpsPlugin.get().getDataFolder(),
            YamlFile.UpdateMethod.EVERYTIME);
    private static final Map<String, String> strings = new HashMap<>();
    private static final Map<String, List<String>> lists = new HashMap<>();

    static {
        reload();
    }

    private final String text;

    Config(String text) {
        this.text = text;
    }

    public static void reload() {
        configYamlFile.reload();
        final ConfigurationSection configuration = configYamlFile.getConfiguration().getConfigurationSection("config");

        configuration
                .getKeys(false)
                .forEach(key -> {
                    if (configuration.isList(key)) {
                        lists.put(key, configuration.getStringList(key));
                    } else
                        strings.put(key, configuration.getString(key));
                });

        Lang.reload();
    }

    public YamlFile getConfiguration() {
        return null;
    }

    public int asInteger() {
        return Integer.parseInt(strings.get(text));}

    public String asString() {
        return strings.get(text);
    }

    public String asReplacedString(Map<String, String> definitions) {
        return TextUtils.replaceString(strings.get(text), definitions);
    }

    public Material asMaterial(){
        try {
            return Material.valueOf(asUppercase());
        } catch (IllegalArgumentException e) {
            return Material.STONE;
        }
    }

    public Map<String, String> asStringMap() {
        Map<String, String> map = new HashMap<>();
        ConfigurationSection section = configYamlFile.getConfiguration().getConfigurationSection(asString());
        if (section == null) return map;
        for (String key : section.getKeys(false)) {
            map.put(key, section.getString(key));
        }
        return map;
    }

    public String asUppercase() {
        return this.asString().toUpperCase();
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(asString());
    }

    public List<String> asReplacedList(Map<String, String> definitions) {
        return lists.get(text);
    }

    public long asLong() {
        return Long.parseLong(strings.get(text));
    }
}
