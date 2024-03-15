package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.ItemUtils;
import dev.revivalo.playerwarps.utils.SortingUtils;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface Menu {
    MenuType getMenuType();

    default void setDefaultItems(Player player, BaseGui gui) {
        gui.setItem(48, ItemBuilder.from(ItemUtils.getItem(Config.WARP_LIST_ITEM.asString())).glow(getMenuType() == MenuType.DEFAULT_LIST_MENU || getMenuType() == MenuType.CATEGORIES).name(Component.text(Lang.WARPS_ITEM_NAME.asColoredString())).asGuiItem(event -> new CategoriesMenu().open(player))); //openCategories(player)));
        gui.setItem(49, ItemBuilder.from(ItemUtils.getItem(Config.MY_WARPS_ITEM.asString())).glow(getMenuType() == MenuType.OWNED_LIST_MENU).name(Component.text(Lang.MY_WARPS_ITEM_NAME.asColoredString())).asGuiItem(event -> new WarpsMenu(MenuType.OWNED_LIST_MENU)
                .setPage(1)
                .open(player, null, SortingUtils.SortType.LATEST))); //openWarpsMenu(player, GUIManager.WarpMenuType.OWNED, null, 1, SortingUtils.SortType.LATEST)));//(int) UserHandler.getUsersTemp(player.getUniqueId())[0]))); //createGuiItem(Config.MY_WARPS_ITEM.asUppercase(),1, glow.equalsIgnoreCase("mywarps"), Lang.MY_WARPS_ITEM_NAME.asColoredString(), null));
        gui.setItem(50, ItemBuilder.from(ItemUtils.getItem(Config.FAVORITE_WARPS_ITEM.asString())).glow(getMenuType() == MenuType.FAVORITE_LIST_MENU).name(Component.text(Lang.FAVORITE_WARPS_ITEM_NAME.asColoredString())).asGuiItem(event -> new WarpsMenu(MenuType.FAVORITE_LIST_MENU)
                .setPage(1)
                .open(player, null, SortingUtils.SortType.LATEST))); //openWarpsMenu(player, GUIManager.WarpMenuType.FAVORITES, null, 1, SortingUtils.SortType.LATEST))); //em(Config.FAVORITE_WARPS_ITEM.asUppercase(),1, glow.equalsIgnoreCase("favorites"), Lang.FAVORITE_WARPS_ITEM_NAME.asColoredString(), null));
    }

    void open(Player player);
}
