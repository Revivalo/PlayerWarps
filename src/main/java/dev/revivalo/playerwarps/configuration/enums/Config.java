package dev.revivalo.playerwarps.configuration.enums;

import com.google.common.base.Splitter;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.YamlFile;
import dev.revivalo.playerwarps.utils.TextUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private static final Map<String, String> messages = new HashMap<>();
    private static final Map<String, String> listsStoredAsStrings = new HashMap<>();
    private static final Map<String, ItemStack> items = new HashMap<>();

    private final String text;

    Config(String text) {
        this.text = text;
    }

    public static void reload() {
        final YamlConfiguration configuration = new YamlFile("config.yml",
                PlayerWarpsPlugin.get().getDataFolder(),
                YamlFile.UpdateMethod.EVERYTIME)
                .getConfiguration();

        final ConfigurationSection configurationSection = configuration.getConfigurationSection("config");
        Objects.requireNonNull(configurationSection)
                .getKeys(true)
                .forEach(key -> {
                    if (key.endsWith("items") || key.endsWith("worlds") || key.endsWith("notifications") || key.endsWith("help")) {
                        listsStoredAsStrings.put(key, String.join("⎶", configurationSection.getStringList(key)));
                    } else messages.put(key, configurationSection.getString(key));
                });
        Lang.reload();
    }

    public List<String> asReplacedList(final Map<String, String> definitions) {
        return Splitter.on("⎶").splitToList(TextUtils.replaceString(listsStoredAsStrings.get(this.text), definitions));
    }

    public String asString() {
        return messages.get(text);
    }
    public String asReplacedString(Map<String, String> definitions) {
        return TextUtils.replaceString(asString(), definitions);
    }
    public String asUppercase() {
        return this.asString().toUpperCase();
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(asString());
    }

    public long asLong() {
        return Long.parseLong(asString());
    }

    public int asInt() {
        return Integer.parseInt(asString());
    }
}
