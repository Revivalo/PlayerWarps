package cz.revivalo.playerwarps.lang;

import cz.revivalo.playerwarps.PlayerWarps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public enum Lang {
    UPDATE_CHECKER("update-checker"),
    AUTO_SAVE_ENABLED("autosave-enabled"),
    AUTO_SAVE_ANNOUNCE("autosave-announce"),
    AUTO_SAVE_INTERVAL("autosave-interval"),
    TELEPORTATION_DELAY("teleport-delay"),
    WARP_NAME_FORMAT("warp-name-format"),
    WARP_PRICE("warp-price"),
    DATE_FORMAT("date-format"),
    DELETE_WARP_REFUND("delete-warp-refund"),
    WARP_NAME_MAX_LENGTH("warp-name-max-lenght"),
    MAX_WARP_ADMISSION("max-warp-admission"),
    DEFAULT_WARP_ITEM("default-warp-item"),
    DEFAULT_LIMIT_SIZE("default-limit-size"),
    ALLOW_ACCEPT_TELEPORT_MENU("allow-teleport-accept-menu"),
    FAVORITE_WARPS_ITEM("favorite-warps-item"),
    WARP_LIST_ITEM("warp-list-item"),
    MY_WARPS_ITEM("my-warps-item"),
    ENTERED_HIGHER_PRICE_THAN_ALLOWED("entered-higher-price-than-allowed"),
    ACCEPT_TELEPORT_WITH_ADMISSION("accept-teleport-with-admission"),
    TELEPORT_TO_WARP_WITH_ADMISSION("teleport-to-warp-with-admission"),
    HELP("help-message-lore"),
    OWN_WARP_ITEM_NAME("own-warp-item-name"),
    RELOAD("reload-message"),
    LIMIT_REACHED("limit-reached"),
    NO_WARPS("no-warps"),
    NON_EXISTING_WARP("non-existing-warp"),
    WARP_ALREADY_CREATED("warp-already-created"),
    WARP_CREATED("warp-created"),
    WARP_CREATED_WITH_PRICE("warp-created-with-price"),
    CURRENCY("currency"),
    INSUFFICIENT_BALANCE("insufficient-balance"),
    INSUFFICIENT_BALANCE_TO_TELEPORT("insufficient-balance-to-teleport"),
    WARP_REMOVED("warp-removed"),
    WARP_REMOVED_WITH_REFUND("warp-removed-with-refund"),
    TELEPORTATION("teleportation"),
    TELEPORTATION_CANCELLED("teleportation-cancelled"),
    TELEPORT_TO_WARP("teleport-to-warp"),
    NAME_CANT_CONTAINS_DOT("name-cant-contains-dot"),
    INSUFFICIENT_PERMS("insufficient-perms"),
    ITEM_CHANGED("item-changed"),
    TITLE_WRITE_MSG("title-write-msg"),
    RENAME_MSG("rename-write-msg"),
    ITEMWRITEMSG("item-write-msg"),
    TEXTWRITECANCELED("text-write-canceled"),
    TEXTSIZEERROR("text-size-error"),
    TEXTCHANGED("text-changed"),
    NOTOWNING("not-owning"),
    INVALIDITEM("invalid-item"),
    ALREADYREVIEWED("already-reviewed"),
    INVALIDREVIEW("invalid-review"),
    SELFREVIEW("selfreview"),
    WARPREVIEWED("warp-reviewed"),
    TRIEDTOSETBANNEDITEM("tried-to-set-banned-item"),
    NOTANUMBER("not-a-number"),
    PRICEWRITEMSG("price-write-msg"),
    PRICECHANGED("price-changed"),
    INVALIDENTEREDPRICE("invalid-entered-price"),
    WARPSITEMNAME("warps-item-name"),
    MYWARPSITEMNAME("my-warps-item-name"),
    HELPITEMNAME("help-item-name"),
    FAVORITEWARPSITEMNAME("favorite-warps-item-name"),
    MYWARPSTITLE("my-warp-title"),
    WARPSTITLE("warps-title"),
    HELPTITLE("help-title"),
    FAVORITESTITLE("favorites-title"),
    ACCEPTMENUTITLE("accept-menu-title"),
    ACCEPT("accept"),
    DENY("deny"),
    PAGEITEM("page-item"),
    NEXTPAGE("next-page"),
    PREVIOUS_PAGE("previous-page"),
    NO_DESCRIPTION("no-description"),
    STAR_REVIEW_ITEM("star-review-item"),
    BACK_ITEM("back-item"),
    BACK_NAME("back-name"),
    ONE_STAR("1star"),
    TWO_STARS("2star"),
    THREE_STARS("3star"),
    FOUR_STARS("4star"),
    FIVE_STARS("5star"),
    WARP_ENABLED("warp-enabled"),
    WARP_DISABLED("warp-disabled"),
    WARP_IS_DISABLED("warp-is-disabled"),
    ENTERED_INVALID_TYPE("entered-invalid-type-of-warp"),
    WARP_TYPE_CHANGED("warp-type-changed"),
    PRIVACY_CHANGED("privacy-changed"),
    ENABLE_CATEGORIES("enable-categories"),
    ENABLE_CATEGORIES_BACKGROUND("categories-background-item"),
    BAD_COMMAND_SYNTAX("bad-command-syntax"),
    ADD_FAVORITE("favorite-warp-added"),
    REMOVE_FAVORITE("remove-favorite-warp"),
    FAV_NOT_CONTAINS("warp-not-contains-favlist"),
    ALREADY_FAVORITE("warp-already-contains-favlist"),
    TRANSFER_SUCCESSFUL("ownership-transfer-successful"),
    TRANSFER_INFO("ownership-transfer-info"),
    TRANSFER_ERROR("ownership-transfer-error"),
    OWNER_CHANGE_MSG("owner-change-msg"),
    WARP_RENAMED("warp-name-changed"),
    TRIED_TO_CREATE_PWARP_IN_DISABLED_WORLD("tried-to-create-warp-in-disabled-world"),
    WARP_NAME_IS_ABOVE_LETTER_LIMIT("warp-name-is-above-letters-limit"),
    CATEGORY_TITLE("category-title"),
    FREE_OF_CHARGE("free-of-charge"),
    WARP_ACTIVE("warp-active"),
    WARP_INACTIVE("warp-inactive"),
    REMOVE_WARP("remove-warp"),
    CHANGE_TYPE("change-type"),
    CHANGE_ITEM("change-item"),
    CHANGE_LABEL("change-label"),
    PWARP_ENABLE("pwarp-enable"),
    PRIVACY("privacy"),
    SET_PRICE("set-price"),
    CHANGE_OWNER("change-owner"),
    RENAME_WARP("rename-warp"),
    BANNED_ITEMS("banned-items"),
    BANNED_WORLDS("disabled-worlds"),
    WARP_LORE("warp-lore"),
    OWN_WARP_LORE("own-warp-lore"),
    HELP_LORE("help-lore"),
    REMOVE_WARP_LORE("remove-warp-lore"),
    CHANGE_TYPE_LORE("change-type-lore"),
    CHANGE_ITEM_LORE("change-item-lore"),
    CHANGE_LABEL_LORE("change-label-lore"),
    PWARP_ENABLE_LORE("pwarp-enabled-lore"),
    PWARP_DISABLE_LORE("pwarp-disabled-lore"),
    PRIVATE_ENABLE_LORE("private-enabled-lore"),
    PRIVATE_DISABLE_LORE("private-disabled-lore"),
    SET_PRICE_LORE("set-price-lore"),
    CHANGE_OWNER_LORE("change-owner-lore"),
    RENAME_WARP_LORE("rename-warp-lore");

    private final String text;
    private static final Map<String, String> messages = new HashMap<>();
    private static final Map<String, List<String>> lores = new HashMap<>();

    Lang(String text) {
        this.text = text;
    }

    public String getString() {return applyColor(messages.get(text));}

    public boolean getBoolean(){return Boolean.parseBoolean(messages.get(text));}

    public int getInt(){return Integer.parseInt(messages.get(text));}

    public long getLong(){return Long.parseLong(messages.get(text));}

    public List<String> getStringList() {
        List<String> lore = new ArrayList<>();

        for (String str : lores.get(this.text)) {
            lore.add(applyColor(str));
        }

        return lore;
    }

    private static final Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>");
    public static String applyColor(String message){
        if (PlayerWarps.isHexSupport) {
            Matcher matcher = hexPattern.matcher(message);
            while (matcher.find()) {
                final ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
                final String before = message.substring(0, matcher.start());
                final String after = message.substring(matcher.end());
                message = before + hexColor + after;
                matcher = hexPattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void reload() {
        final FileConfiguration cfg = PlayerWarps.getPlugin(PlayerWarps.class).getConfig();
        for (String key : cfg.getConfigurationSection("config").getKeys(true)) {
            if (key.endsWith("lore") || key.endsWith("items") || key.endsWith("worlds")) {
                lores.put(key, cfg.getStringList("config." + key));
            } else {
                messages.put(key, cfg.getString("config." + key));
            }
        }
    }

    static {
        reload();
    }
}
