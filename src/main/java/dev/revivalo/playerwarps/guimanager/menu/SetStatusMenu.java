package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpState;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SetStatusMenu implements Menu {
    private final Warp warp;
    private final Gui gui;

    public SetStatusMenu(Warp warp) {
        this.warp = warp;
        this.gui = Gui.gui()
                .disableAllInteractions()
                .rows(3)
                .title(Component.text(Lang.SET_WARP_STATUS_TITLE.asReplacedString(new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }})))
                .create();
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SET_STATUS_MENU;
    }

    @Override
    public void open(Player player) {
        gui.setItem(12, ItemBuilder.from(Material.BARRIER).name(Component.text(Lang.CLOSED_STATUS.asColoredString())).asGuiItem(event -> {
            warp.setStatus(WarpState.CLOSED);
            new SetUpMenu(warp).open(player);
        }));
        gui.setItem(13, ItemBuilder.from(Material.OAK_DOOR).name(Component.text(Lang.OPENED_STATUS.asColoredString())).asGuiItem(event -> {
            warp.setStatus(WarpState.OPENED);
            new SetUpMenu(warp).open(player);
        }));
        gui.setItem(14, ItemBuilder.from(Material.IRON_DOOR).name(Component.text(Lang.PASSWORD_PROTECTED_STATUS.asColoredString())).asGuiItem(event -> {
            new InputMenu(warp).open(player);
        }));

        gui.open(player);
    }
}
