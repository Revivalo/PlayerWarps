package cz.revivalo.playerwarps.configuration.enums;

import com.google.common.base.Splitter;
import cz.revivalo.playerwarps.PlayerWarps;
import cz.revivalo.playerwarps.configuration.YamlFile;
import cz.revivalo.playerwarps.utils.TextUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@RequiredArgsConstructor
public enum Config {
    LANGUAGE("language"),
    WARP_NAME_FORMAT("warp-name-format"),
    BANNED_ITEMS("banned-items"),
    BANNED_WORLDS("disabled-worlds"),
    UPDATE_CHECKER("update-checker"),
    AUTO_SAVE_ENABLED("autosave-enabled"),
    AUTO_SAVE_ANNOUNCE("autosave-announce"),
    AUTO_SAVE_INTERVAL("autosave-interval"),
    TELEPORTATION_DELAY("teleport-delay"),
    WARP_PRICE("warp-price"),
    DATE_FORMAT("date-format"),
    DELETE_WARP_REFUND("delete-warp-refund"),
    WARP_NAME_MAX_LENGTH("warp-name-max-lenght"),
    MAX_WARP_ADMISSION("max-warp-admission"),
    DEFAULT_WARP_ITEM("default-warp-item"),
    DEFAULT_LIMIT_SIZE("default-limit-size"),
    ALLOW_ACCEPT_TELEPORT_MENU("allow-teleport-accept-menu"),
    FAVORITE_WARPS_ITEM("favorite-warps-item"),
    ENABLE_CATEGORIES_BACKGROUND("categories-background-item"),
    WARP_LIST_ITEM("warp-list-item"),
    MY_WARPS_ITEM("my-warps-item"),
    HELP_ITEM("help-item"),
    PAGEITEM("page-item"),
    STAR_REVIEW_ITEM("star-review-item"),
    ENABLE_CATEGORIES("enable-categories"),
    CURRENCY_SYMBOL("currency-symbol"),
    BACK_ITEM("back-item");

    private static final Map<String, String> messages = new HashMap<>();
    private static final Map<String, String> listsStoredAsStrings = new HashMap<>();
    private static final Map<String, ItemStack> items = new HashMap<>();

    private final String text;

    public static void reload() {
        final YamlConfiguration configuration = new YamlFile("config.yml",
                PlayerWarps.getPlugin().getDataFolder())
                .getConfiguration();

        final ConfigurationSection configurationSection = configuration.getConfigurationSection("config");
        Objects.requireNonNull(configurationSection)
                .getKeys(true)
                .forEach(key -> {
                    if (key.endsWith("items") || key.endsWith("worlds") || key.endsWith("notifications") || key.endsWith("help")) {
                        listsStoredAsStrings.put(key, String.join("⎶", configurationSection.getStringList(key)));
                    } else if (key.endsWith("item")) {
                        try {
                            items.put(key, new ItemStack(Material.valueOf(configurationSection.getString(key).toUpperCase(Locale.ENGLISH))));
                        } catch (IllegalArgumentException | NullPointerException ex) {
                            items.put(key, null);
                        }
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
        return TextUtils.replaceString(messages.get(text), definitions);
    }
    public ItemStack asAnItem(){
        return items.get(this.text);
    }
    public String asUppercase() {
        return this.asString().toUpperCase();
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(messages.get(text));
    }

    public long asLong() {
        return Long.parseLong(messages.get(text)) * 3600000;
    }

    public int asInt() {
        return Integer.parseInt(messages.get(text));
    }
}
