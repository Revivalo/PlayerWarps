package dev.revivalo.playerwarps.guimanager.menu;

import de.rapha149.signgui.SignGUI;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;

public class InputMenu implements Menu {
    private final Warp warp;
    private WarpAction<String> warpAction;

    public InputMenu(Warp warp) {
        this.warp = warp;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.INPUT_MENU;
    }

    @Override
    public void open(Player player) {
        SignGUI gui = SignGUI.builder()
                .setType(Material.OAK_SIGN)
                .setColor(DyeColor.BLACK)
                .setLine(1, warpAction.getInputText().asColoredString())
                .setHandler((p, result) -> {
                    String input = result.getLineWithoutColor(0);

                    warpAction.preExecute(player, warp, input, null);

                    return Collections.emptyList();
                })

                .build();

        gui.open(player);
    }

    public InputMenu setWarpAction(WarpAction<String> warpAction) {
        this.warpAction = warpAction;
        return this;
    }
}
