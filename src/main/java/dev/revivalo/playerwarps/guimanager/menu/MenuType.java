package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.configuration.file.Lang;

public enum MenuType {
    MANAGE_MENU(Lang.EDIT_WARP_MENU_TITLE),
    CONFIRMATION_MENU(Lang.CONFIRMATION_MENU_TITLE),
    CHANGE_TYPE_MENU(Lang.CHANGE_WARP_CATEGORY_TITLE),
    SET_STATUS_MENU(Lang.SET_WARP_STATUS_TITLE),
    DEFAULT_LIST_MENU(Lang.WARPS_TITLE),
    OWNED_LIST_MENU(Lang.MY_WARPS_TITLE),
    INPUT_MENU(null),
    REVIEW_MENU(Lang.REVIEW_WARP_TITLE),
    CATEGORIES(Lang.CATEGORY_TITLE),
    FAVORITE_LIST_MENU(Lang.FAVORITES_TITLE);

    final private Lang title;

    MenuType(Lang title) {
        this.title = title;
    }

    public String getTitle() {
        return title.asColoredString();
    }
}
