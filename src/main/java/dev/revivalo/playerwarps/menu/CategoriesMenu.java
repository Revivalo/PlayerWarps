package dev.revivalo.playerwarps.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.category.CategoryManager;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.ItemUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CategoriesMenu extends Menu {
    private Gui gui;
    private Player player;

    private final GuiItem BACKGROUND_FILLER = Config.CATEGORIES_BACKGROUND_ITEM.asString().equalsIgnoreCase("none") ? null : ItemBuilder.from(ItemUtil.getItem(Config.CATEGORIES_BACKGROUND_ITEM.asUppercase())).setName(" ").asGuiItem();
    private final ItemBuilder INSUFFICIENT_PERMISSION_ITEM =  ItemBuilder.from(ItemUtil.getItem(Config.INSUFFICIENT_PERMISSIONS_ITEM.asUppercase()))
            .setName(Lang.INSUFFICIENT_PERMS_FOR_CATEGORY.asColoredString());

    @Override
    public void create() {
        this.gui = Gui.gui()
                .rows(getMenuSize() / 9)
                .title(Component.text(getMenuTitle().asColoredString()))
                .disableAllInteractions()
                .create();
    }

    @Override
    public void fill() {
        if (BACKGROUND_FILLER != null) {
            for (int i = 0; i < 54; i++) {
                gui.setItem(i, BACKGROUND_FILLER);
            }
        }

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
                                        .asGuiItem(event -> new WarpsMenu.DefaultWarpsMenu()
                                                .setPage(1)
                                                .open(player, category.toString(), getDefaultSortType()))
                                        : INSUFFICIENT_PERMISSION_ITEM
                                        .setLore(Lang.INSUFFICIENT_PERMS_FOR_CATEGORY_LORE.asReplacedList(new HashMap<String, String>(){{put("%permission%", category.getPermission());}}))
                                        .asGuiItem()
                        )
                );

        setDefaultItems(player, gui);
    }

    @Override
    public void open(Player player) {
        this.player = player;
        create();
        fill();

        gui.open(player);
    }

    @Override
    public BaseGui getMenu() {
        return this.gui;
    }

    @Override
    public short getMenuSize() {
        return Config.CATEGORIES_MENU_SIZE.asShort();
    }

    @Override
    public Lang getMenuTitle() {
        return Lang.CATEGORY_TITLE;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}