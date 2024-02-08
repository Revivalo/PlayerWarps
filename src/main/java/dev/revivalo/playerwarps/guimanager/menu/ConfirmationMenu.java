package dev.revivalo.playerwarps.guimanager.menu;

import com.cryptomorin.xseries.XMaterial;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.user.WarpAction;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.actions.RemoveWarpAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ConfirmationMenu implements Menu {
    private final Warp warp;
    private final Gui gui;

    public ConfirmationMenu(Warp warp) {
        this.warp = warp;
        this.gui = Gui.gui()
                .disableAllInteractions()
                .rows(3)
                .title(Component.text(Lang.ACCEPT_MENU_TITLE.asReplacedString(new HashMap<String, String>() {{
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
        open(player, WarpAction.SET_ADMISSION);
    }

    public void open(Player player, WarpAction action) {
        gui.setItem(11, ItemBuilder.from(XMaterial.LIME_STAINED_GLASS_PANE.parseMaterial()).name(Component.text(Lang.ACCEPT.asColoredString())).asGuiItem(event -> {
            switch (action) {
                case TELEPORT:
                    PlayerWarpsPlugin.getWarpHandler().preWarp(player, warp);
                    break;
                case REMOVE:
                    new RemoveWarpAction().preExecute(player, warp, null, MenuType.OWNED_LIST_MENU);
                    break;
            }
            gui.close(player);
        }));
        gui.setItem(15, ItemBuilder.from(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial()).name(Component.text(Lang.DENY.asColoredString())).asGuiItem(event -> {
            if (action == WarpAction.REMOVE) {
                new ManageMenu(warp).open(player);
            }
        }));
        gui.open(player);
    }
}
