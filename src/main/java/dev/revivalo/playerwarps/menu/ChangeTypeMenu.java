package dev.revivalo.playerwarps.menu;

import dev.revivalo.playerwarps.category.Category;
import dev.revivalo.playerwarps.category.CategoryManager;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.action.SetTypeAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class ChangeTypeMenu implements Menu {
    private final Warp warp;
    private Gui gui;
    private Player player;

    public ChangeTypeMenu(Warp warp) {
        this.warp = warp;
    }

    @Override
    public void create() {
        this.gui = Gui.gui()
                .disableAllInteractions()
                .rows(getMenuSize() / 9)
                .title(Component.text(Lang.CHANGE_WARP_CATEGORY_TITLE.asReplacedString(null, new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }})))
                .create();
    }

    @Override
    public void fill() {
        Collection<Category> categories = CategoryManager.getCategories();
        if (!categories.isEmpty()) {
            categories
                    .forEach(category -> gui.addItem(ItemBuilder.from(category.getItem()).lore(Collections.emptyList()).setName(StringUtils.capitalize(category.getType())).asGuiItem(event -> {
                        new SetTypeAction().preExecute(player, warp, category, MenuType.MANAGE_MENU, 1);
                    })));
        } else {
            gui.setItem(13, ItemBuilder.from(Material.BARRIER).setName(Lang.CATEGORIES_ARE_DISABLED.asColoredString()).asGuiItem());
        }
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.CHANGE_TYPE_MENU;
    }

    @Override
    public short getMenuSize() {
        return (short) 27;
    }

    @Override
    public void open(Player player) {
        this.player = player;

        create();
        fill();

        gui.open(player);
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
