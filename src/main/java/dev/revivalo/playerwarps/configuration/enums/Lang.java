package dev.revivalo.playerwarps.configuration.enums;

import com.google.common.base.Splitter;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.YamlFile;
import dev.revivalo.playerwarps.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public enum Lang {
    PREFIX("prefix"),
    PASSWORD_SETTING_CANCELLED("password-setting-cancelled"),
    PASSWORD_CHANGED("password-changed"),
    ENTER_PASSWORD("enter-password"),
    ENTERED_WRONG_PASSWORD("entered-wrong-password"),
    CATEGORIES_ARE_DISABLED("categories-are-disabled"),
    ENTERED_HIGHER_PRICE_THAN_ALLOWED("entered-higher-price-than-allowed"),
    ACCEPT_TELEPORT_WITH_ADMISSION("accept-teleport-with-admission"),
    TELEPORT_TO_WARP_WITH_ADMISSION("teleport-to-warp-with-admission"),
    HELP("help-message-lore"),
    OWN_WARP_ITEM_NAME("own-warp-item-name"),
    RELOAD("reload-message"),
    LIMIT_REACHED("limit-reached"),
    LIMIT_REACHED_OTHER("limit-reached-other"),
    NO_WARPS("no-warps"),
    NO_WARP_AT_POSITION("no-warp-at-position"),
    ALREADY_OWNING("already-owning"),
    LATEST("latest"),
    VISITS("visits"),
    RATING("rating"),
    NON_EXISTING_WARP("non-existing-warp"),
    WARP_ALREADY_CREATED("warp-already-created"),
    WARP_CREATED("warp-created"),
    WARP_CREATED_WITH_PRICE("warp-created-with-price"),
    INSUFFICIENT_BALANCE("insufficient-balance"),
    INSUFFICIENT_BALANCE_TO_TELEPORT("insufficient-balance-to-teleport"),
    WARP_REMOVED("warp-removed"),
    WARP_REMOVED_WITH_REFUND("warp-removed-with-refund"),
    WARP_IN_DELETED_WORLD("warp-in-deleted-world"),
    TELEPORTATION("teleportation"),
    TELEPORTATION_CANCELLED("teleportation-cancelled"),
    TELEPORT_TO_WARP("teleport-to-warp"),
    NAME_CANT_CONTAINS_SPACE("name-cant-contains-space"),
    INSUFFICIENT_PERMS("insufficient-perms"),
    PASSWORD_LENGTH("password-length"),
    ITEM_CHANGED("item-changed"),
    TITLE_WRITE_MSG("title-write-msg"),
    RENAME_MSG("rename-write-msg"),
    SET_DESCRIPTION_MESSAGE("set-description-msg"),
    ITEM_WRITE_MSG("item-write-msg"),
    TEXT_WRITE_CANCELED("text-write-canceled"),
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
    ACCEPT_MENU_TITLE("accept-menu-title"),
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
    WARP_ENABLED("warp-enabled"),
    WARP_DISABLED("warp-disabled"),
    WARP_IS_DISABLED("warp-is-disabled"),
    ENTERED_INVALID_TYPE("entered-invalid-type-of-warp"),
    WARP_TYPE_CHANGED("warp-type-changed"),
    PRIVACY_CHANGED("privacy-changed"),
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

    private static final Map<String, String> messages = new HashMap<>();
    private static final Map<String, String> listsStoredAsString = new HashMap<>();
    private final String text;

    Lang(String text) {
        this.text = text;
    }

    public static void reload() {
        final YamlConfiguration configuration = new YamlFile("lang.yml",
                PlayerWarpsPlugin.get().getDataFolder(),
                YamlFile.UpdateMethod.EVERYTIME)
                .getConfiguration();

        final ConfigurationSection languageSection = configuration.getConfigurationSection("lang");

        Objects.requireNonNull(languageSection)
                .getKeys(false)
                .forEach(key -> {
                    if (key.endsWith("lore") || key.endsWith("notification") || key.endsWith("help")) {
                        listsStoredAsString.put(key, TextUtils.colorize(String.join("⎶", languageSection.getStringList(key))));
                        return;
                    }
                    messages.put(key, TextUtils.colorize(StringUtils.replace(languageSection.getString(key), "%prefix%", Lang.PREFIX.asColoredString())));
                });
    }
    public List<Component> asColoredList(){
        return Splitter.on("⎶").splitToList(listsStoredAsString.get(this.text)).stream()
                .map(Component::text)
                .collect(Collectors.toList());
    }

    public @NotNull List<@Nullable Component> asReplacedList(final Map<String, String> definitions) {
        final String loreAsString = listsStoredAsString.get(this.text);
        final String[] keys = definitions.keySet().toArray(new String[]{});
        final String[] values = definitions.values().toArray(new String[]{});

        return Splitter.on("⎶").splitToList(StringUtils.replaceEach(loreAsString, keys, values)).stream()
                .map(Component::text)
                .collect(Collectors.toList());
    }

    public String asColoredString() {
        return messages.get(text);
    }

    public String asReplacedString(Map<String, String> definitions){
        return TextUtils.replaceString(messages.get(text), definitions);
    }
}
