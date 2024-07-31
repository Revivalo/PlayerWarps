package dev.revivalo.playerwarps.configuration.enums;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.YamlFile;
import dev.revivalo.playerwarps.utils.TextUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Lang {
    PREFIX("prefix"),
    PASSWORD_SETTING_CANCELLED("password-setting-cancelled"),
    PASSWORD_CHANGED("password-changed"),
    PASSWORD_TOO_SHORT("password-too-short"),
    INVALID_INPUT("invalid-input"),
    ENTER_PASSWORD("enter-password"),
    ENTERED_WRONG_PASSWORD("entered-wrong-password"),
    ENTER_WARPS_NAME("enter-warps-name"),
    CATEGORIES_ARE_DISABLED("categories-are-disabled"),
    ENTERED_HIGHER_PRICE_THAN_ALLOWED("entered-higher-price-than-allowed"),
    ACCEPT_TELEPORT_WITH_ADMISSION("accept-teleport-with-admission"),
    TELEPORT_TO_WARP_WITH_ADMISSION("teleport-to-warp-with-admission"),
    HELP("help-message-lore"),
    HELP_DISPLAY_NAME("help-display-name"),
    HELP_LORE("help-lore"),
    OWN_WARP_ITEM_NAME("own-warp-item-name"),
    RELOAD("reload-message"),
    WARP_CREATION_NOTIFICATION("warp-creation-notification"),
    WARP_VISIT_NOTIFICATION("warp-visit-notification"),
    LIMIT_REACHED("limit-reached"),
    LIMIT_REACHED_OTHER("limit-reached-other"),
    NO_WARPS("no-warps"),
    NO_WARP_AT_POSITION("no-warp-at-position"),
    ALREADY_OWNING("already-owning"),
    LATEST("latest"),
    VISITS("visits"),
    RATING("rating"),
    NON_EXISTING_WARP("non-existing-warp"),
    INSUFFICIENT_PERMS_FOR_CATEGORY("insufficient-perms-for-category"),
    INSUFFICIENT_PERMS_FOR_CATEGORY_LORE("insufficient-perms-for-category-lore"),
    WARP_ALREADY_CREATED("warp-already-created"),
    WARP_CREATED("warp-created"),
    CLICK_TO_CONFIGURE("click-to-configure"),
    CLICK_TO_CANCEL_INPUT("click-to-cancel-input"),
    WARP_CREATED_WITH_PRICE("warp-created-with-price"),
    INSUFFICIENT_BALANCE_FOR_ACTION("insufficient-balance-for-action"),
    INSUFFICIENT_BALANCE_TO_TELEPORT("insufficient-balance-to-teleport"),
    WARP_REMOVED("warp-removed"),
    WARP_REMOVED_WITH_REFUND("warp-removed-with-refund"),
    WARP_IN_DELETED_WORLD("warp-in-deleted-world"),
    TELEPORTATION("teleportation"),
    TELEPORTATION_CANCELLED("teleportation-cancelled"),
    TELEPORT_TO_WARP("teleport-to-warp"),
    NAME_CANT_CONTAINS_SPACE("name-cant-contains-space"),
    INSUFFICIENT_PERMS("insufficient-permissions"),
    PASSWORD_LENGTH("password-length"),
    ITEM_CHANGED("item-changed"),
    TITLE_WRITE_MSG("title-write-msg"),
    RENAME_MSG("rename-write-msg"),
    SET_DESCRIPTION_MESSAGE("set-description-msg"),
    ITEM_WRITE_MSG("item-write-msg"),
    CANCEL_INPUT("cancel-input"),
    INPUT_CANCELLED("input-cancelled"),
    TEXT_SIZE_ERROR("text-size-error"),
    DESCRIPTION_CHANGED("description-changed"),
    NOT_OWNING("not-owning"),
    INVALID_ITEM("invalid-item"),
    ALREADY_REVIEWED("already-reviewed"),
    INVALID_REVIEW("invalid-review"),
    SELF_REVIEW("self-review"),
    WARP_REVIEWED("warp-reviewed"),
    TRIED_TO_SET_BANNED_ITEM("tried-to-set-banned-item"),
    NOT_A_NUMBER("not-a-number"),
    PRICE_WRITE_MESSAGE("price-write-msg"),
    PRICE_CHANGED("price-changed"),
    INVALID_ENTERED_PRICE("invalid-entered-price"),
    WARPS_ITEM_NAME("warps-item-name"),
    MY_WARPS_ITEM_NAME("my-warps-item-name"),
    REVIEW_WARP_TITLE("review-warp-title"),
    FAVORITE_WARPS_ITEM_NAME("favorite-warps-item-name"),
    MY_WARPS_TITLE("my-warp-title"),
    WARPS_TITLE("warps-title"),
    EDIT_WARP_MENU_TITLE("edit-warp-menu-title"),
    FAVORITES_TITLE("favorites-title"),
    CONFIRMATION_MENU_TITLE("confirmation-menu-title"),
    ACCEPT("accept"),
    DENY("deny"),
    OPENED_STATUS("opened-status"),
    CLOSED_STATUS("closed-status"),
    PASSWORD_PROTECTED_STATUS("password-protected-status"),
    SORT_WARPS("sort-warps"),
    CLICK_TO_SORT_BY("click-to-sort-by"),
    NEXT_PAGE("next-page"),
    PREVIOUS_PAGE("previous-page"),
    NO_DESCRIPTION("no-description"),
    BACK_NAME("back-name"),
    ONE_STAR("1star"),
    TWO_STARS("2star"),
    THREE_STARS("3star"),
    FOUR_STARS("4star"),
    FIVE_STARS("5star"),
    SEARCH_WARP("search-warp"),
    SEARCH_WARP_LORE("search-warp-lore"),
    WARP_IS_DISABLED("warp-is-disabled"),
    ENTERED_INVALID_TYPE("entered-invalid-type-of-warp"),
    WARP_TYPE_CHANGED("warp-type-changed"),
    PRIVACY_CHANGED("privacy-changed"),
    UNKNOWN_COMMAND("unknown-command"),
    BAD_COMMAND_SYNTAX("bad-command-syntax"),
    ADD_FAVORITE("favorite-warp-added"),
    REMOVE_FAVORITE("remove-favorite-warp"),
    FAV_NOT_CONTAINS("warp-not-contains-favlist"),
    ALREADY_FAVORITE("warp-already-contains-favlist"),
    TRANSFER_SUCCESSFUL("ownership-transfer-successful"),
    TRANSFER_INFO("ownership-transfer-info"),
    TRANSFER_ERROR("ownership-transfer-error"),
    OWNER_CHANGE_MSG("owner-change-msg"),
    PASSWORD_CHANGE_MSG("password-change-msg"),
    WARP_RENAMED("warp-name-changed"),
    WARPS_STATUS_CHANGED("warp-status-changed"),
    WARP_RELOCATED("warp-relocated"),
    WARP_RELOCATION("warp-relocation"),
    WARP_RELOCATION_LORE("warp-relocation-lore"),
    TRIED_TO_CREATE_PWARP_IN_DISABLED_WORLD("tried-to-create-warp-in-disabled-world"),
    WARP_NAME_IS_ABOVE_LETTER_LIMIT("warp-name-is-above-letters-limit"),
    CATEGORY_TITLE("category-title"),
    CHANGE_WARP_CATEGORY_TITLE("change-warp-category-title"),
    SET_WARP_STATUS_TITLE("set-warp-status-title"),
    FREE_OF_CHARGE("free-of-charge"),
    REMOVE_WARP("remove-warp"),
    CHANGE_TYPE("change-type"),
    CHANGE_ITEM("change-item"),
    CHANGE_DESCRIPTION("change-label"),
    CHANGE_DISPLAY_NAME("change-display-name"),
    DISPLAY_NAME_CHANGED("display-name-changed"),
    CHANGE_DISPLAY_NAME_LORE("change-display-name-lore"),
    WRITE_NEW_DISPLAY_NAME("write-new-display-name"),
    PWARP_ACCESSIBILITY("pwarp-accessibility"),
    SET_PRICE("set-price"),
    CHANGE_OWNER("change-owner"),
    RENAME_WARP("rename-warp"),
    WARP_LORE("warp-lore"),
    OWN_WARP_LORE("own-warp-lore"),
    REMOVE_WARP_LORE("remove-warp-lore"),
    CHANGE_TYPE_LORE("change-type-lore"),
    CHANGE_ITEM_LORE("change-item-lore"),
    CHANGE_DESCRIPTION_LORE("change-label-lore"),
    PWARP_ACCESSIBILITY_LORE("pwarp-accessibility-lore"),
    SET_PRICE_LORE("set-price-lore"),
    CHANGE_OWNER_LORE("change-owner-lore"),
    RENAME_WARP_LORE("rename-warp-lore");

    private static final YamlFile langYamlFile = new YamlFile("lang.yml",
            PlayerWarpsPlugin.get().getDataFolder(), YamlFile.UpdateMethod.EVERYTIME);
    private static final Map<String, String> messages = new HashMap<>();
    private static final Map<String, String> listsStoredAsStrings = new HashMap<>();
    private final String text;

    Lang(String text) {
        this.text = text;
    }

    public static void reload() {
        langYamlFile.reload();
        final YamlConfiguration configuration = langYamlFile.getConfiguration();

        ConfigurationSection langSection = configuration.getConfigurationSection("lang");

        langSection
                .getKeys(false)
                .forEach(key -> {
                    if (langSection.isList(key)) {
                        listsStoredAsStrings.put(key, String.join("ᴪ", langSection.getStringList(key)));
                    } else
                        messages.put(key, StringUtils.replace(langSection.getString(key), "%prefix%", Lang.PREFIX.asColoredString(), 1));
                });
    }

    public List<String> asReplacedList() {
        return TextUtils.getColorizedList(null, TextUtils.replaceListAsString(listsStoredAsStrings.get(text), Collections.emptyMap()));
    }

    public List<String> asReplacedList(final Map<String, String> definitions) {
        return TextUtils.getColorizedList(null, TextUtils.replaceListAsString(listsStoredAsStrings.get(text), definitions));
    }

    public List<String> asReplacedList(Player player, final Map<String, String> definitions) {
        return TextUtils.getColorizedList(player, TextUtils.replaceListAsString(listsStoredAsStrings.get(text), definitions));
    }

    public String asColoredString() {return asColoredString(null);}
    public String asColoredString(Player player) {
        return TextUtils.getColorizedString(player, messages.get(text));
    }
    public String asReplacedString(Map<String, String> definitions) {
        return TextUtils.getColorizedString(null, TextUtils.replaceString(messages.get(text), definitions));
    }
    public String asReplacedString(Player player, Map<String, String> definitions) {
        return TextUtils.getColorizedString(player, TextUtils.replaceString(messages.get(text), definitions));
    }
}
