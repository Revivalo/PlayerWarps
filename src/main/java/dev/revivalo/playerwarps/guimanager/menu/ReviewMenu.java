package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.user.DataSelectorType;
import dev.revivalo.playerwarps.user.User;
import dev.revivalo.playerwarps.user.UserHandler;
import dev.revivalo.playerwarps.util.ItemUtil;
import dev.revivalo.playerwarps.util.SortingUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.action.ReviewWarpAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class ReviewMenu implements Menu {
    private final Warp warp;
    private final Gui gui;

    public ReviewMenu(Warp warp) {
        this.warp = warp;
        this.gui = Gui.gui()
                .disableAllInteractions()
                .rows(4)
                .title(Component.text(getMenuType().getTitle().replace("%warp%", warp.getName())))
                .create();
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.REVIEW_MENU;
    }

    @Override
    public void open(Player player) {
        final User user = UserHandler.getUser(player);

        gui.setItem(11, ItemBuilder.from(ItemUtil.ONE_STAR).asGuiItem(event -> new ReviewWarpAction().preExecute(player, warp, 1, null)));
        gui.setItem(12, ItemBuilder.from(ItemUtil.TWO_STARS).asGuiItem(event -> new ReviewWarpAction().preExecute(player, warp, 2, null)));
        gui.setItem(13, ItemBuilder.from(ItemUtil.THREE_STARS).asGuiItem(event -> new ReviewWarpAction().preExecute(player, warp, 3, null)));
        gui.setItem(14, ItemBuilder.from(ItemUtil.FOUR_STARS).asGuiItem(event -> new ReviewWarpAction().preExecute(player, warp, 4, null)));
        gui.setItem(15, ItemBuilder.from(ItemUtil.FIVE_STARS).asGuiItem(event -> new ReviewWarpAction().preExecute(player, warp, 5, null)));

        gui.setItem(
                31,
                ItemBuilder.from(
                                ItemUtil.getItem(Config.BACK_ITEM.asString())
                        )
                        .setName(Lang.BACK_NAME.asColoredString())
                        .asGuiItem(
                                event -> new WarpsMenu(MenuType.DEFAULT_LIST_MENU)
                                        .setPage((int) user.getData(DataSelectorType.ACTUAL_PAGE))
                                        .open(player, warp.getCategory() == null ? "all" : warp.getCategory().getType(), SortingUtil.SortType.LATEST)
                        )
        );

        gui.open(player);
    }
}
