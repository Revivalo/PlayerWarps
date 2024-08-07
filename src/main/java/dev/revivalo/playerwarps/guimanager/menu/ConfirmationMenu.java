package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.ItemUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ConfirmationMenu implements Menu {
    private final Warp warp;
    private final Gui gui;

    private final ItemBuilder acceptItem =  ItemBuilder.from(ItemUtil.getItem(Config.CONFIRM_ITEM.asUppercase())).setName(Lang.ACCEPT.asColoredString());
    private final ItemBuilder denyItem =  ItemBuilder.from(ItemUtil.getItem(Config.DENY_ITEM.asUppercase())).setName(Lang.DENY.asColoredString());

    public ConfirmationMenu(Warp warp) {
        this.warp = warp;
        this.gui = Gui.gui()
                .disableAllInteractions()
                .rows(3)
                .title(Component.text(Lang.CONFIRMATION_MENU_TITLE.asReplacedString(new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }})))
                .create();
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.CONFIRMATION_MENU;
    }

    @Override
    public void open(Player player) {
        open(player, null);
    }

    public void open(Player player, dev.revivalo.playerwarps.warp.WarpAction<?> action) {
        gui.setItem(11, acceptItem.asGuiItem(event -> {
            action.preExecute(player, warp, null, null);
//            switch (action) {
//                case TELEPORT:
//                    new PreTeleportToWarpAction().preExecute(player, warp, null, null);//PlayerWarpsPlugin.getWarpHandler().preWarp(player, warp);
//                    break;
//                case REMOVE:
//                    new RemoveWarpAction().preExecute(player, warp, null, MenuType.OWNED_LIST_MENU, 1);
//                    break;
//            }
            gui.close(player);
        }));
        gui.setItem(15, denyItem.asGuiItem(event -> {
//            if (action == WarpAction.REMOVE) {
//                new ManageMenu(warp).open(player);
//            }
            gui.close(player);
        }));

        gui.open(player);
    }
}