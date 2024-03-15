package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.user.DataSelectorType;
import dev.revivalo.playerwarps.user.User;
import dev.revivalo.playerwarps.user.UserHandler;
import dev.revivalo.playerwarps.utils.ItemUtils;
import dev.revivalo.playerwarps.utils.SortingUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.actions.ReviewWarpAction;
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
        //user.setActualMenu(this);

        gui.setItem(11, ItemBuilder.from(ItemUtils.ONE_STAR).asGuiItem(event -> new ReviewWarpAction().execute(player, warp, 1)));
        gui.setItem(12, ItemBuilder.from(ItemUtils.TWO_STARS).asGuiItem(event -> new ReviewWarpAction().execute(player, warp, 2)));
        gui.setItem(13, ItemBuilder.from(ItemUtils.THREE_STARS).asGuiItem(event -> new ReviewWarpAction().execute(player, warp, 3)));
        gui.setItem(14, ItemBuilder.from(ItemUtils.FOUR_STARS).asGuiItem(event -> new ReviewWarpAction().execute(player, warp, 4)));
        gui.setItem(15, ItemBuilder.from(ItemUtils.FIVE_STARS).asGuiItem(event -> new ReviewWarpAction().execute(player, warp, 5)));

        gui.setItem(
                31,
                ItemBuilder.from(
                                ItemUtils.getItem(Config.BACK_ITEM.asString())
                        )
                        .name(Component.text(Lang.BACK_NAME.asColoredString()))
                        .asGuiItem(
                                event -> new WarpsMenu(MenuType.DEFAULT_LIST_MENU)
                                        .setPage((int) user.getData(DataSelectorType.ACTUAL_PAGE))
                                        .open(player, warp.getCategory() == null ? "all" : warp.getCategory().getType(), SortingUtils.SortType.LATEST)
                        )
        );
//        gui.setItem(31, ItemBuilder.from(ItemUtils.getItem(Config.BACK_ITEM.asString())).name(Component.text(Lang.BACK_NAME.asColoredString())).asGuiItem(event -> new WarpsMenu(MenuType.DEFAULT_LIST_MENU)
//                .setPage((int) UserHandler.getUser(player).getData(DataSelectorType.ACTUAL_PAGE))
//                .open(player, warp.getCategory() == null ? "all" : warp.getCategory().getType(), SortingUtils.SortType.LATEST)));

        gui.open(player);
    }
}
