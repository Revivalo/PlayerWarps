package dev.revivalo.playerwarps.user;

import dev.revivalo.playerwarps.guimanager.menu.Menu;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;

public enum DataSelectorType {
    ACTUAL_PAGE(Integer.class),
    ACTUAL_PAGE_TEST(Menu.class),
    PREVIOUS_PAGE_TEST(Menu.class),
    CURRENT_WARP_ACTION(WarpAction.class),
    SELECTED_WARP(Warp.class);

    private final Class<?> dataType;

    DataSelectorType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public Class<?> getDataType() {
        return dataType;
    }
}

