package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.ItemUtils;
import dev.revivalo.playerwarps.utils.SortingUtils;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;

public interface Menu {
    MenuType getMenuType();

    default void setDefaultItems(Player player, BaseGui gui) {
        gui.setItem(48,
                ItemBuilder
                        .from(ItemUtils.getItem(Config.WARP_LIST_ITEM.asString()))
                        .glow(getMenuType() == MenuType.DEFAULT_LIST_MENU || getMenuType() == MenuType.CATEGORIES)
                        .setName(Lang.WARPS_ITEM_NAME.asColoredString())
                        .asGuiItem(event -> {
                            if (Config.ENABLE_CATEGORIES.asBoolean()) {
                                new CategoriesMenu().open(player);
                            } else {
                                new WarpsMenu(MenuType.DEFAULT_LIST_MENU).setPage(1).open(player, "all", SortingUtils.SortType.LATEST);
                            }
                        })
        );

        gui.setItem(49,
                ItemBuilder
                        .from(ItemUtils.getItem(Config.MY_WARPS_ITEM.asString()))
                        .glow(getMenuType() == MenuType.OWNED_LIST_MENU)
                        .setName(Lang.MY_WARPS_ITEM_NAME.asColoredString())
                        .asGuiItem(event ->
                                new WarpsMenu(MenuType.OWNED_LIST_MENU)
                                        .setPage(1)
                                        .open(player, null, SortingUtils.SortType.LATEST)
                        )
        );

        gui.setItem(50,
                ItemBuilder
                        .from(ItemUtils.getItem(Config.FAVORITE_WARPS_ITEM.asString()))
                        .glow(getMenuType() == MenuType.FAVORITE_LIST_MENU)
                        .setName(Lang.FAVORITE_WARPS_ITEM_NAME.asColoredString())
                        .asGuiItem(event ->
                                new WarpsMenu(MenuType.FAVORITE_LIST_MENU)
                                        .setPage(1)
                                        .open(player, null, SortingUtils.SortType.LATEST)
                        )
        );
    }

    void open(Player player);
}