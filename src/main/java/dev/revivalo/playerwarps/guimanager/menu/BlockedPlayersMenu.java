package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.action.BlockPlayerAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BlockedPlayersMenu implements Menu {
    private final Warp warp;
    //private final Gui gui;

    public BlockedPlayersMenu(Warp warp) {
        this.warp = warp;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.BLOCKED_PLAYERS_MENU;
    }

    @Override
    public short getMenuSize() {
        return 27;
    }

    @Override
    public void open(Player player) {
        long nano = System.nanoTime();
        Gui gui = Gui.gui()
                .disableAllInteractions()
                .rows(getMenuSize() / 9)
                .title(Component.text(Lang.SET_WARP_STATUS_TITLE.asReplacedString(null, new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }})))
                .create();
        long estimated = System.nanoTime() - nano;
        PlayerWarpsPlugin.get().getLogger().info("Menu vytvořeno: " + estimated);

        for (UUID uuid : warp.getBlockedPlayers()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            gui.addItem(ItemBuilder
                    .from(Material.PLAYER_HEAD)
                    .setName(Lang.BLOCKED_PLAYER_MANAGE.asColoredString().replace("%player%", offlinePlayer.getName() == null ? "Unknown" : offlinePlayer.getName()))
                    .setLore(Lang.BLOCKED_PLAYER_MANAGE_LORE.asReplacedList())
                    .asGuiItem(event -> {
                        warp.unblock(offlinePlayer);
                        update(player);
                    }));
        }

        gui.addItem(ItemBuilder
                .from(Material.CONDUIT)
                .setName(Lang.BLOCKED_PLAYER_ADD.asColoredString())
                .asGuiItem(event -> {
                    BlockPlayerAction blockPlayerAction = new BlockPlayerAction();
                    PlayerWarpsPlugin.getWarpHandler().waitForPlayerInput(player, warp, blockPlayerAction).thenAccept(input -> blockPlayerAction.preExecute(player, warp, input, MenuType.BLOCKED_PLAYERS_MENU));
                }));

        gui.open(player);
    }
}
