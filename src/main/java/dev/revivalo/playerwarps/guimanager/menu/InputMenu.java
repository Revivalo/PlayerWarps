package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpState;
import io.github.rapha149.signgui.SignGUI;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;

public class InputMenu implements Menu {
    private final Warp warp;

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
                .setLine(1, Lang.ENTER_PASSWORD.asColoredString())
                .setHandler((p, result) -> {
                    String input = result.getLineWithoutColor(0);

                    if (input.isEmpty()) {
                        return Collections.emptyList();
                    }

                    if (input.length() < 3 || input.length() > 15) {
                        return Collections.emptyList();
                    }


                    warp.setPassword(input);
                    player.sendMessage(Lang.PASSWORD_CHANGED.asColoredString());

                    warp.setStatus(WarpState.PASSWORD_PROTECTED);

                    PlayerWarpsPlugin.get().runDelayed(() -> new SetUpMenu(warp).open(player)/*openSetUpMenu(player, warp)*/, 3);

                    return Collections.emptyList();
                })

                .build();

        gui.open(player);
    }
}
