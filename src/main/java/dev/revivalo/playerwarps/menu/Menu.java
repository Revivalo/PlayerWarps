package dev.revivalo.playerwarps.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.menu.sort.Sortable;
import dev.revivalo.playerwarps.util.ItemUtil;
import dev.revivalo.playerwarps.warp.WarpHandler;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;

public interface Menu {
    default WarpHandler getWarpHandler() {
        return PlayerWarpsPlugin.getWarpHandler();
    }

    default Sortable getDefaultSortType() {
        return getWarpHandler().getSortingManager().getDefaultSortType();
    }

    MenuType getMenuType();

    void open(Player player);

    void create();

    void fill();

    default void update() {
        create();
        fill();
        open(getPlayer());
    }

    default void setDefaultItems(Player player, BaseGui gui) {
        gui.setItem(getMenuSize() - 6,
                ItemBuilder
                        .from(ItemUtil.getItem(Config.WARP_LIST_ITEM.asString()))
                        .glow(getMenuType() == MenuType.DEFAULT_LIST_MENU || getMenuType() == MenuType.CATEGORIES)
                        .setName(Lang.WARPS_ITEM_NAME.asColoredString())
                        .asGuiItem(event -> {
                            if (Config.ENABLE_CATEGORIES.asBoolean()) {
                                new CategoriesMenu().open(player);
                            } else {
                                new WarpsMenu(MenuType.DEFAULT_LIST_MENU).setPage(1).open(player, "all", getWarpHandler().getSortingManager().getDefaultSortType());
                            }
                        })
        );

        gui.setItem(getMenuSize() - 5,
                ItemBuilder
                        .from(ItemUtil.getItem(Config.MY_WARPS_ITEM.asString()))
                        .glow(getMenuType() == MenuType.OWNED_LIST_MENU)
                        .setName(Lang.MY_WARPS_ITEM_NAME.asColoredString())
                        .asGuiItem(event ->
                                new WarpsMenu(MenuType.OWNED_LIST_MENU)
                                        .setPage(1)
                                        .open(player, null, getWarpHandler().getSortingManager().getDefaultSortType())
                        )
        );

        gui.setItem(getMenuSize() - 4,
                ItemBuilder
                        .from(ItemUtil.getItem(Config.FAVORITE_WARPS_ITEM.asString()))
                        .glow(getMenuType() == MenuType.FAVORITE_LIST_MENU)
                        .setName(Lang.FAVORITE_WARPS_ITEM_NAME.asColoredString())
                        .asGuiItem(event ->
                                new WarpsMenu(MenuType.FAVORITE_LIST_MENU)
                                        .setPage(1)
                                        .open(player, null, getWarpHandler().getSortingManager().getDefaultSortType())
                        )
        );
    }

    short getMenuSize();

    Player getPlayer();
}