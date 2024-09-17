package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.guimanager.menu.sort.Sortable;
import dev.revivalo.playerwarps.util.ItemUtil;
import dev.revivalo.playerwarps.warp.WarpHandler;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface Menu {
    default WarpHandler getWarpHandler() {
        return PlayerWarpsPlugin.getWarpHandler();
    }

    default Sortable getDefaultSortType() {
        return getWarpHandler().getSortingManager().getDefaultSortType();
    }

    MenuType getMenuType();

    default void setDefaultItems(Player player, BaseGui gui) {
        gui.setItem(48,
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

        gui.setItem(49,
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

        gui.setItem(50,
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

    void open(Player player);
}