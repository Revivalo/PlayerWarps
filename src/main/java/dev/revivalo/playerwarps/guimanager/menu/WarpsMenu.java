package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.categories.Category;
import dev.revivalo.playerwarps.categories.CategoryManager;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.user.DataSelectorType;
import dev.revivalo.playerwarps.user.User;
import dev.revivalo.playerwarps.user.UserHandler;
import dev.revivalo.playerwarps.utils.*;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.actions.FavoriteWarpAction;
import dev.revivalo.playerwarps.warp.actions.PreTeleportToWarpAction;
import dev.revivalo.playerwarps.warp.actions.SearchWarpAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WarpsMenu implements Menu {
    private int page = 1;
    private final MenuType menuType;
    private final PaginatedGui paginatedGui;

    private final ItemBuilder NEXT_PAGE = ItemBuilder.from(ItemUtils.getItem(Config.NEXT_PAGE_ITEM.asUppercase())).setName(Lang.NEXT_PAGE.asColoredString());
    private final ItemBuilder PREVIOUS_PAGE = ItemBuilder.from(ItemUtils.getItem(Config.PREVIOUS_PAGE_ITEM.asUppercase())).setName(Lang.PREVIOUS_PAGE.asColoredString());

    public WarpsMenu(MenuType menuType) {
        this.menuType = menuType;
        this.paginatedGui = Gui.paginated()
                .pageSize(45)
                .rows(6)
                .title(Component.text(getMenuType().getTitle().replace("%page%", String.valueOf(page))))
                .disableAllInteractions()
                .create();
    }

    @Override
    public MenuType getMenuType() {
        return menuType;
    }

    @Override
    public void open(Player player) {
        open(player, "all", SortingUtils.SortType.LATEST, null);
    }

    public void open(Player player, String categoryName, SortingUtils.SortType sortType) {
        open(player, categoryName, sortType, null);
    }

    public void open(Player player, String categoryName, SortingUtils.SortType sortType, List<Warp> foundWarps) {
        final User user = UserHandler.getUser(player);
        user.addData(DataSelectorType.ACTUAL_PAGE, paginatedGui.getCurrentPageNum());
        user.addData(DataSelectorType.ACTUAL_MENU, getMenuType());

        final Category openedCategory = CategoryManager.getCategoryFromName(categoryName);

        //if (paginatedGui.previous())
        paginatedGui.setItem(45, PREVIOUS_PAGE.asGuiItem(event -> {
            paginatedGui.previous();
            paginatedGui.updateTitle(getMenuType().getTitle().replace("%page%", String.valueOf(paginatedGui.getCurrentPageNum())));
        }));

        //if (paginatedGui.next())
        paginatedGui.setItem(53, NEXT_PAGE.asGuiItem(event -> {
            paginatedGui.next();
            paginatedGui.updateTitle(getMenuType().getTitle().replace("%page%", String.valueOf(paginatedGui.getCurrentPageNum())));
        }));

        SortingUtils.SortType nextSortType = sortType == SortingUtils.SortType.LATEST ? SortingUtils.SortType.VISITS
                : sortType == SortingUtils.SortType.VISITS
                ? SortingUtils.SortType.RATING : SortingUtils.SortType.LATEST;

        if (Config.ENABLE_WARP_SEARCH.asBoolean()) {
            paginatedGui.setItem(52, ItemBuilder.from(ItemUtils.getItem(Config.SEARCH_WARP_ITEM.asUppercase()))
                    .setName(Lang.SEARCH_WARP.asColoredString())
                    .setLore(Lang.SEARCH_WARP_LORE.asReplacedList())
                    .asGuiItem(
                            event -> {
                                new InputMenu(null)
                                        .setWarpAction(new SearchWarpAction())
                                        .open(player);
                            }
                    ));
        }

        if (getMenuType() != MenuType.OWNED_LIST_MENU)
            paginatedGui.setItem(46, ItemBuilder.from(ItemUtils.getItem(Config.SORT_WARPS_ITEM.asUppercase()))
                    .setName(Lang.SORT_WARPS.asColoredString())
                    .setLore(
                            " ",
                            TextUtils.getColorizedString(player, sortType == SortingUtils.SortType.LATEST ? "&a" : "&7") + "► " + Lang.LATEST.asColoredString(),
                            TextUtils.getColorizedString(player, sortType == SortingUtils.SortType.VISITS ? "&a" : "&7") + "► " + Lang.VISITS.asColoredString(),
                            TextUtils.getColorizedString(player, sortType == SortingUtils.SortType.RATING ? "&a" : "&7") + "► " + Lang.RATING.asColoredString(),
                            " ",
                            Lang.CLICK_TO_SORT_BY.asReplacedString(player, new HashMap<String, String>() {{
                                put("%selector%", nextSortType.getName());
                            }})
                    )

                    .asGuiItem(event -> {
                        paginatedGui.clearPageItems();
                        open(player, categoryName, nextSortType, foundWarps);
                    }));

        final List<Warp> warps = new ArrayList<>();
        switch (getMenuType()) {
            case DEFAULT_LIST_MENU:
                if (openedCategory.isDefaultCategory()) {
                    if (foundWarps == null) {
                        warps.addAll(PlayerWarpsPlugin.getWarpHandler().getWarps().stream()
                                .filter(Warp::isAccessible)
                                .collect(Collectors.toList()));
                    } else {
                        warps.addAll(foundWarps);
                    }
                } else {
                    warps.addAll(PlayerWarpsPlugin.getWarpHandler().getWarps().stream()
                            .filter(warp -> warp.isAccessible() && (warp.getCategory() == null || warp.getCategory().getType().equalsIgnoreCase(categoryName)))
                            .collect(Collectors.toList()));
                }
                warps.sort(sortType.getComparator());

                break;
            case OWNED_LIST_MENU:
                warps.addAll(PlayerWarpsPlugin.getWarpHandler().getWarps().stream().filter(warp -> warp.canManage(player)).collect(Collectors.toList()));
                break;
            case FAVORITE_LIST_MENU:
                warps.addAll(PlayerWarpsPlugin.getWarpHandler().getPlayerFavoriteWarps(player));
                break;
        }

        final Lang warpLore = getMenuType() == MenuType.OWNED_LIST_MENU ? Lang.OWN_WARP_LORE : Lang.WARP_LORE;

        GuiItem guiItem;
        if (warps.isEmpty()) {
            if (menuType == MenuType.OWNED_LIST_MENU && Config.ENABLE_HINTS.asBoolean()) {
                guiItem = new GuiItem(
                        ItemBuilder.from(ItemUtils.getItem(Config.HELP_ITEM.asUppercase()))
                                .setName(Lang.HELP_DISPLAY_NAME.asColoredString())
                                .setLore(Lang.HELP_LORE.asReplacedList())
                                .build()
                );
                paginatedGui.setItem(22, guiItem);
            } else {
                guiItem = new GuiItem(
                        ItemBuilder.from(ItemUtils.getItem(Config.NO_WARP_FOUND_ITEM.asUppercase()))
                                .setName(Lang.NO_WARP_FOUND.asColoredString())
                                .build()
                );
                paginatedGui.setItem(22, guiItem);
            }
        } else {
            for (Warp warp : warps) {
                if (warp.getLocation() == null) {
                    guiItem = new GuiItem(ItemBuilder.from(Material.BARRIER)
                            .setName(TextUtils.color(warp.getDisplayName()))
                            .setLore(Lang.WARP_IN_DELETED_WORLD.asColoredString())
                            .build());
                } else {
                    guiItem = ItemBuilder.from((warp.getMenuItem() == null ? ItemUtils.getItem(Config.DEFAULT_WARP_ITEM.asString(), player) : warp.getMenuItem().clone()))
                            .setName(TextUtils.getColorizedString(player, Config.WARP_NAME_FORMAT.asString().replace("%warpName%", warp.getDisplayName())))
                            .setLore(warpLore.asReplacedList(player, new HashMap<String, String>() {{
                                        put("%creationDate%", DateUtils.getFormatter().format(warp.getDateCreated()));
                                        put("%world%", warp.getLocation().getWorld().getName());
                                        put("%voters%", String.valueOf(warp.getReviewers().size()));
                                        put("%price%", warp.getAdmission() == 0
                                                ? Lang.FREE_OF_CHARGE.asColoredString()
                                                : TextUtils.formatNumber(warp.getAdmission()) + " " + Config.CURRENCY_SYMBOL.asString());
                                        put("%today%", String.valueOf(warp.getTodayVisits()));
                                        put("%status%", warp.getStatus().getText());
                                        put("%ratings%", String.valueOf(NumberUtils.round(warp.getConvertedRating(), 1)));
                                        put("%stars%", TextUtils.createRatingFormat(warp));
                                        put("%lore%", warp.getDescription() == null
                                                ? Lang.NO_DESCRIPTION.asColoredString()
                                                : warp.getDescription());
                                        put("%visits%", String.valueOf(warp.getVisits()));
                                        put("%owner-name%", Objects.requireNonNull(Bukkit.getOfflinePlayer(warp.getOwner()).getName()));
                                    }}
                            )).asGuiItem();

                    guiItem.setAction(event -> {
                        if (PlayerWarpsPlugin.getWarpHandler().areWarps()) {
                            switch (event.getClick()) {
                                case LEFT:
                                    player.closeInventory();
                                    new PreTeleportToWarpAction().preExecute(player, warp, null, null);
                                    break;
                                case RIGHT:
                                case SHIFT_RIGHT:
                                    if (getMenuType() == MenuType.OWNED_LIST_MENU) {
                                        if (!player.hasPermission("playerwarps.settings")) {
                                            player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString().replace("%permission%", "playerwarps.settings"));
                                            return;
                                        }
                                        new ManageMenu(warp).open(player);
                                    } else {
                                        if (Config.ENABLE_WARP_RATING.asBoolean()) {
                                            new ReviewMenu(warp).open(player);
                                        }
                                    }
                                    break;
                                case SHIFT_LEFT:
                                    MenuType actualMenu = (MenuType) user.getData(DataSelectorType.ACTUAL_MENU);
                                    new FavoriteWarpAction().preExecute(player, warp, null, actualMenu, page);
                                    break;
                            }
                        }
                    });
                }

                paginatedGui.addItem(guiItem);
            }
        }

        setDefaultItems(player, paginatedGui);
        paginatedGui.open(player);
    }

    public WarpsMenu setPage(int page) {
        this.page = page;
        return this;
    }
}
