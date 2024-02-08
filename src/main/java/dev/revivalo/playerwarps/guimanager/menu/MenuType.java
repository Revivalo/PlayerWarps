package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.configuration.enums.Lang;

public enum MenuType {
    SET_UP_MENU(Lang.EDIT_WARP_MENU_TITLE.asColoredString()),
    CONFIRMATION_MENU(Lang.ACCEPT_MENU_TITLE.asColoredString()),
    CHANGE_TYPE_MENU(Lang.CHANGE_WARP_CATEGORY_TITLE.asColoredString()),
    SET_STATUS_MENU(Lang.SET_WARP_STATUS_TITLE.asColoredString()),
    DEFAULT_LIST_MENU(Lang.WARPS_TITLE.asColoredString()),
    OWNED_LIST_MENU(Lang.MY_WARPS_TITLE.asColoredString()),
    INPUT_MENU(null),
    REVIEW_MENU(Lang.REVIEW_WARP_TITLE.asColoredString()),
    CATEGORIES(Lang.CATEGORY_TITLE.asColoredString()),
    FAVORITE_LIST_MENU(Lang.FAVORITES_TITLE.asColoredString());

    final private String title;

    MenuType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
