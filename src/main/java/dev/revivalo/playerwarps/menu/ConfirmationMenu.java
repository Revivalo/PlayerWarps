package dev.revivalo.playerwarps.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.ItemUtil;
import dev.revivalo.playerwarps.util.NumberUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.action.WarpAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class ConfirmationMenu<T> extends Menu {
    private final Warp warp;
    private final T data;
    private Gui gui;
    private Player player;
    private WarpAction<T> action;

    private static final ItemBuilder DENY_ITEM = ItemBuilder.from(ItemUtil.getItem(Config.DENY_ITEM.asUppercase())).setName(Lang.DENY.asColoredString());

    private Menu menuToOpen = null;

    public ConfirmationMenu(Warp warp) {
        this.warp = warp;
        this.data = null;
    }

    public ConfirmationMenu(Warp warp, T data) {
        this.warp = warp;
        this.data = data;
    }

    @Override
    public void create() {
        this.gui = Gui.gui()
                .disableAllInteractions()
                .rows(getMenuSize() / 9)
                .title(Component.text(getMenuTitle().asColoredString().replace("%warp%", warp.getName())))
                .create();
    }

    @Override
    public void fill() {
        for (String position : Config.CONFIRM_ITEM_POSITIONS.asList()) {
            int slot = Integer.parseInt(position);
            gui.setItem(slot, ItemBuilder
                    .from(ItemUtil.getItem(Config.CONFIRM_ITEM.asUppercase()))
                    .setName(action.getFee() == 0
                            ? Lang.ACCEPT.asColoredString()
                            : Lang.ACCEPT_WITH_PRICE.asColoredString().replace("%price%", NumberUtil.formatNumber(action.getFee()))
                    )
                    .asGuiItem(event -> {
                        action.proceed(player, warp, data, null, 1, true);
                        gui.close(player);
                    }));
        }

        for (String position : Config.DENY_ITEM_POSITIONS.asList()) {
            int slot = Integer.parseInt(position);
            gui.setItem(slot, DENY_ITEM.asGuiItem(event -> {
                gui.close(player);

                if (menuToOpen != null) {
                    PlayerWarpsPlugin.get().runDelayed(() -> menuToOpen.open(player), 4);
                }
            }));
        }
    }

    @Override
    public BaseGui getMenu() {
        return this.gui;
    }

    @Override
    public short getMenuSize() {
        return Config.CONFIRMATION_MENU_SIZE.asShort();
    }

    @Override
    public void open(Player player) {
        open(player, null);
    }

    public void open(Player player, WarpAction<T> action) {
        this.player = player;
        this.action = action;

        create();
        fill();

        gui.open(player);
    }

    public ConfirmationMenu setMenuToOpen(Menu menu) {
        this.menuToOpen = menu;
        return this;
    }

    @Override
    public Lang getMenuTitle() {
        return Lang.CONFIRMATION_MENU_TITLE;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}