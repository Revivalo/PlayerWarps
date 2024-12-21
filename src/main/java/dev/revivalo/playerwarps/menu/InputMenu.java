package dev.revivalo.playerwarps.menu;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.action.WarpAction;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;

public class InputMenu extends Menu {
    private final Warp warp;
    private SignGUI gui;
    private Player player;
    private WarpAction<String> warpAction;

    public InputMenu(Warp warp) {
        this.warp = warp;
    }

    @Override
    public void create() {
        try {
            this.gui = SignGUI.builder()
                    .setType(Material.OAK_SIGN)
                    .setColor(DyeColor.BLACK)
                    .setLine(1, warpAction.getInputText().asColoredString())
                    .setHandler((p, result) -> {
                        String input = result.getLineWithoutColor(0);

                        warpAction.proceed(player, warp, input);

                        return Collections.emptyList();
                    })

                    .build();
        } catch (SignGUIVersionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fill() {
        // nothing to fill
    }

    @Override
    public BaseGui getMenu() {
        return null;
    }

    //    @Override
//    public MenuType getMenuType() {
//        return MenuType.INPUT_MENU;
//    }

    @Override
    public short getMenuSize() {
        return -1;
    }

    @Override
    public Lang getMenuTitle() {
        return null;
    }

    @Override
    public void open(Player player) {
        this.player = player;
        create();

        gui.open(player);
    }

    public InputMenu setWarpAction(WarpAction<String> warpAction) {
        this.warpAction = warpAction;
        return this;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
