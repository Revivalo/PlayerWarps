package dev.revivalo.playerwarps.menu;

import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.user.DataSelectorType;
import dev.revivalo.playerwarps.user.User;
import dev.revivalo.playerwarps.user.UserHandler;
import dev.revivalo.playerwarps.util.ItemUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.action.ReviewWarpAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class ReviewMenu implements Menu {
    private final Warp warp;
    private Gui gui;
    private Player player;

    public ReviewMenu(Warp warp) {
        this.warp = warp;
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
        final User user = UserHandler.getUser(player);

        gui.setItem(11, ItemBuilder.from(ItemUtil.ONE_STAR).asGuiItem(event -> new ReviewWarpAction().preExecute(player, warp, 1)));
        gui.setItem(12, ItemBuilder.from(ItemUtil.TWO_STARS).asGuiItem(event -> new ReviewWarpAction().preExecute(player, warp, 2)));
        gui.setItem(13, ItemBuilder.from(ItemUtil.THREE_STARS).asGuiItem(event -> new ReviewWarpAction().preExecute(player, warp, 3)));
        gui.setItem(14, ItemBuilder.from(ItemUtil.FOUR_STARS).asGuiItem(event -> new ReviewWarpAction().preExecute(player, warp, 4)));
        gui.setItem(15, ItemBuilder.from(ItemUtil.FIVE_STARS).asGuiItem(event -> new ReviewWarpAction().preExecute(player, warp, 5)));

        gui.setItem(
                31,
                ItemBuilder.from(
                                ItemUtil.getItem(Config.BACK_ITEM.asString())
                        )
                        .setName(Lang.BACK_NAME.asColoredString())
                        .asGuiItem(
                                event -> new WarpsMenu.DefaultWarpsMenu()
                                        .setPage((int) user.getData(DataSelectorType.ACTUAL_PAGE))
                                        .open(player, warp.getCategory() == null ? "all" : warp.getCategory().getType(), getDefaultSortType())
                        )
        );
    }

    @Override
    public BaseGui getMenu() {
        return this.gui;
    }

    @Override
    public short getMenuSize() {
        return 4 * 9;
    }

    @Override
    public Lang getMenuTitle() {
        return Lang.REVIEW_WARP_TITLE;
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
