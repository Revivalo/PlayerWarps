package dev.revivalo.playerwarps.menu;

import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpState;
import dev.revivalo.playerwarps.warp.action.SetPasswordAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SetStatusMenu implements Menu {
    private final Warp warp;
    private Gui gui;
    private Player player;

    public SetStatusMenu(Warp warp) {
        this.warp = warp;
    }

    @Override
    public void create() {
        this.gui = Gui.gui()
                .disableAllInteractions()
                .rows(getMenuSize() / 9)
                .title(Component.text(Lang.SET_WARP_STATUS_TITLE.asReplacedString(null, new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }})))
                .create();
    }

    @Override
    public void fill() {
        gui.setItem(12, ItemBuilder.from(Material.BARRIER).setName(Lang.CLOSED_STATUS.asColoredString()).asGuiItem(event -> {
            warp.setStatus(WarpState.CLOSED);
            new ManageMenu(warp).open(player);
        }));
        gui.setItem(13, ItemBuilder.from(Material.OAK_DOOR).setName(Lang.OPENED_STATUS.asColoredString()).asGuiItem(event -> {
            warp.setStatus(WarpState.OPENED);
            new ManageMenu(warp).open(player);
        }));
        gui.setItem(14, ItemBuilder.from(Material.IRON_DOOR).setName(Lang.PASSWORD_PROTECTED_STATUS.asColoredString()).asGuiItem(event -> {
            new InputMenu(warp)
                    .setWarpAction(new SetPasswordAction())
                    .open(player);
        }));
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.ACCESSIBILITY_MENU;
    }

    @Override
    public short getMenuSize() {
        return 3 * 9;
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