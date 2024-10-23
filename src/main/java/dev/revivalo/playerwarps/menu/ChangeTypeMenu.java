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
import java.util.concurrent.CompletableFuture;

public class ChangeTypeMenu implements Menu {
    private final Warp warp;
    private final Gui gui;

    public ChangeTypeMenu(Warp warp) {
        this.warp = warp;
        this.gui = Gui.gui()
                .disableAllInteractions()
                .rows(getMenuSize() / 9)
                .title(Component.text(Lang.CHANGE_WARP_CATEGORY_TITLE.asReplacedString(null, new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }})))
                .create();
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
        Collection<Category> categories = CategoryManager.getCategories();
        if (!categories.isEmpty()) {
            categories
                    .forEach(category -> gui.addItem(ItemBuilder.from(category.getItem()).lore(Collections.emptyList()).setName(StringUtils.capitalize(category.getType())).asGuiItem(event -> {
                        //PlayerWarpsPlugin.getWarpHandler().setType(player, warp, category.getType());
                        new SetTypeAction().preExecute(player, warp, category, MenuType.MANAGE_MENU, 1);
                    })));
        } else {
            gui.setItem(13, ItemBuilder.from(Material.BARRIER).setName(Lang.CATEGORIES_ARE_DISABLED.asColoredString()).asGuiItem());
        }

        gui.open(player);
    }
}
