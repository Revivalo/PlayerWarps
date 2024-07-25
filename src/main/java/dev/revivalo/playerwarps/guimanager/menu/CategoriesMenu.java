package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.categories.CategoryManager;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.ItemUtils;
import dev.revivalo.playerwarps.utils.SortingUtils;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;

public class CategoriesMenu implements Menu {
    private final Gui gui;

    private final ItemBuilder insufficientPermissionsItem =  ItemBuilder.from(ItemUtils.getItem(Config.INSUFFICIENT_PERMISSIONS_ITEM.asUppercase()))
            .setName(Lang.INSUFFICIENT_PERMS_FOR_CATEGORY.asColoredString());

    public CategoriesMenu() {
        this.gui = Gui.gui()
                .rows(6)
                .title(Component.text(Lang.CATEGORY_TITLE.asColoredString()))
                .disableAllInteractions()
                .create();
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.CATEGORIES;
    }

    @Override
    public void open(Player player) {
        final Optional<ItemStack> fillItem = Optional.ofNullable(ItemUtils.getItem(Config.CATEGORIES_BACKGROUND_ITEM.asString()));
        fillItem.ifPresent((itemStack -> {
            final GuiItem backgroundItem = ItemBuilder.from(itemStack).setName(" ").asGuiItem();
            for (int i = 0; i < 54; i++) {
                gui.setItem(i, backgroundItem);
            }
        }));

        CategoryManager.getCategories()
                .forEach(category -> gui.setItem(
                        category.getPosition(),
                        category.hasPermission(player)
                            ? ItemBuilder.from(
                                        category.getItem()
                                )
                                .setName(
                                        category.getName()
                                                .replace("%number%", String.valueOf(PlayerWarpsPlugin.getWarpHandler().getCountOfWarps(category.getType())))
                                )
                                .setLore(category.getLore())
                                .asGuiItem(event -> new WarpsMenu(MenuType.DEFAULT_LIST_MENU)
                                        .setPage(1)
                                        .open(player, category.toString(), SortingUtils.SortType.LATEST))
                                : insufficientPermissionsItem
                                    .setLore(Lang.INSUFFICIENT_PERMS_FOR_CATEGORY_LORE.asReplacedList(new HashMap<String, String>(){{put("%permission%", category.getPermission());}}))
                                    .asGuiItem()
                        )
                );

        setDefaultItems(player, gui);

        gui.open(player);
    }
}