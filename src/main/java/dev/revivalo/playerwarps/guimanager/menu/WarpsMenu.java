package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.category.Category;
import dev.revivalo.playerwarps.category.CategoryManager;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.guimanager.menu.sort.*;
import dev.revivalo.playerwarps.user.DataSelectorType;
import dev.revivalo.playerwarps.user.User;
import dev.revivalo.playerwarps.user.UserHandler;
import dev.revivalo.playerwarps.util.*;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.action.FavoriteWarpAction;
import dev.revivalo.playerwarps.warp.action.PreTeleportToWarpAction;
import dev.revivalo.playerwarps.warp.action.SearchWarpAction;
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
    private PaginatedGui paginatedGui;

    private final ItemBuilder NEXT_PAGE = ItemBuilder.from(ItemUtil.getItem(Config.NEXT_PAGE_ITEM.asUppercase())).setName(Lang.NEXT_PAGE.asColoredString());
    private final ItemBuilder PREVIOUS_PAGE = ItemBuilder.from(ItemUtil.getItem(Config.PREVIOUS_PAGE_ITEM.asUppercase())).setName(Lang.PREVIOUS_PAGE.asColoredString());

    public WarpsMenu(MenuType menuType) {
        this.menuType = menuType;
    }

    @Override
    public MenuType getMenuType() {
        return menuType;
    }

    @Override
    public void open(Player player) {
        open(player, "all", getWarpHandler().getSortingManager().getDefaultSortType(), null);
    }

    public void open(Player player, String categoryName, Sortable sortType) {
        open(player, categoryName, sortType, null);
    }

    public void open(Player player, String categoryName, Sortable sortType, List<Warp> foundWarps) {
        this.paginatedGui = Gui.paginated()
                .pageSize(45)
                .rows(6)
                .title(Component.text(getMenuType().getTitle().replace("%page%", String.valueOf(page))))
                .disableAllInteractions()
                .create();

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

        Sortable nextSortType = getWarpHandler().getSortingManager().nextSortType(sortType);

        List<String> sortLore = new ArrayList<>();
        for (Sortable cachedSortType : getWarpHandler().getSortingManager().getSortTypes()) {
            sortLore.add(TextUtil.color((sortType.equals(cachedSortType) ? Config.SELECTED_SORT.asString() : Config.OTHER_SORT.asString()) + cachedSortType.getName().asColoredString()));
        }

        sortLore.add(" ");

        sortLore.add(Lang.CLICK_TO_SORT_BY.asReplacedString(player, new HashMap<String, String>() {{
            put("%selector%", nextSortType.getName().asColoredString());
        }}));

        if (Config.ENABLE_WARP_SEARCH.asBoolean()) {
            paginatedGui
                    .setItem(52, ItemBuilder.from(ItemUtil.getItem(Config.SEARCH_WARP_ITEM.asUppercase()))
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
            paginatedGui
                    .setItem(46, ItemBuilder.from(ItemUtil.getItem(Config.SORT_WARPS_ITEM.asUppercase()))
                    .setName(Lang.SORT_WARPS.asColoredString())
                    .setLore(sortLore)
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

                getWarpHandler().getSortingManager().sortWarps(warps, sortType);

                break;
            case OWNED_LIST_MENU:
                warps.addAll(PlayerWarpsPlugin.getWarpHandler().getWarps().stream().filter(warp -> warp.isOwner(player)).collect(Collectors.toList()));
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
                        ItemBuilder
                                .from(ItemUtil.getItem(Config.HELP_ITEM.asUppercase()))
                                .setName(Lang.HELP_DISPLAY_NAME.asColoredString())
                                .setLore(Lang.HELP_LORE.asReplacedList())
                                .build()
                );
                paginatedGui.setItem(22, guiItem);
            } else {
                guiItem = new GuiItem(
                        ItemBuilder
                                .from(ItemUtil.getItem(Config.NO_WARP_FOUND_ITEM.asUppercase()))
                                .setName(Lang.NO_WARP_FOUND.asColoredString())
                                .build()
                );
                paginatedGui.setItem(22, guiItem);
            }
        } else {
            for (Warp warp : warps) {
                if (warp.getLocation() == null) {
                    guiItem = new GuiItem(ItemBuilder
                            .from(Material.BARRIER)
                            .setName(TextUtil.color(warp.getDisplayName()))
                            .setLore(Lang.WARP_IN_DELETED_WORLD.asColoredString())
                            .build());
                } else {

                    //guiItem = ItemBuilder.skull()
                    guiItem = ItemBuilder
                            .from((warp.getMenuItem() == null ? ItemUtil.getItem(Config.DEFAULT_WARP_ITEM.asString(), warp.getOwner()) : warp.getMenuItem().clone()))
                            .setName(TextUtil.getColorizedString(player, Config.WARP_NAME_FORMAT.asString().replace("%warpName%", warp.getDisplayName())))
                            .setLore(warpLore.asReplacedList(player, new HashMap<String, String>() {{
                                        put("%creationDate%", DateUtil.getFormatter().format(warp.getDateCreated()));
                                        put("%world%", warp.getLocation().getWorld().getName());
                                        put("%voters%", String.valueOf(warp.getReviewers().size()));
                                        put("%price%", warp.getAdmission() == 0
                                                ? Lang.FREE_OF_CHARGE.asColoredString()
                                                : TextUtil.formatNumber(warp.getAdmission()) + " " + Config.CURRENCY_SYMBOL.asString());
                                        put("%today%", String.valueOf(warp.getTodayVisits()));
                                        put("%status%", warp.getStatus().getText());
                                        put("%ratings%", String.valueOf(NumberUtil.round(warp.getConvertedRating(), 1)));
                                        put("%stars%", TextUtil.createRatingFormat(warp));
                                        put("%lore%", warp.getDescription() == null
                                                ? Lang.NO_DESCRIPTION.asColoredString()
                                                : TextUtil.splitLoreIntoLines(warp.getDescription(), 5)); // Použití funkce na rozdělení textu
                                        put("%visits%", String.valueOf(warp.getVisits()));
                                        put("%owner-name%", Bukkit.getOfflinePlayer(warp.getOwner()).getName() == null ? "Unknown" : Bukkit.getOfflinePlayer(warp.getOwner()).getName());
                                    }}
                            )).asGuiItem();

                    guiItem.setAction(event -> {
                        if (PlayerWarpsPlugin.getWarpHandler().areWarps()) {
                            switch (event.getClick()) {
                                case LEFT:
                                    player.closeInventory();
                                    new PreTeleportToWarpAction().setMenuToOpen(this).preExecute(player, warp, null, null);
                                    break;
                                case RIGHT:
                                case SHIFT_RIGHT:
                                    if (getMenuType() == MenuType.OWNED_LIST_MENU) {
                                        if (!player.hasPermission("playerwarps.settings")) {
                                            player.sendMessage(Lang.INSUFFICIENT_PERMISSIONS.asColoredString().replace("%permission%", "playerwarps.settings"));
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
