package dev.revivalo.playerwarps.menu;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.category.Category;
import dev.revivalo.playerwarps.category.CategoryManager;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.menu.sort.Sortable;
import dev.revivalo.playerwarps.user.DataSelectorType;
import dev.revivalo.playerwarps.user.User;
import dev.revivalo.playerwarps.user.UserHandler;
import dev.revivalo.playerwarps.util.*;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.action.FavoriteWarpAction;
import dev.revivalo.playerwarps.warp.action.PreTeleportToWarpAction;
import dev.revivalo.playerwarps.warp.action.SearchWarpAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class WarpsMenu extends Menu {
    private int page = 1;
    private PaginatedGui paginatedGui;
    private Player player;
    private String categoryName;
    private Sortable sortType;
    private List<Warp> foundWarps;

    private final Set<Player> sortingCooldowns = new HashSet<>();
    //private static final long SORT_COOLDOWN_MS = 500;

    private final ItemBuilder NEXT_PAGE = ItemBuilder.from(ItemUtil.getItem(Config.NEXT_PAGE_ITEM.asUppercase())).setName(Lang.NEXT_PAGE.asColoredString());
    private final ItemBuilder PREVIOUS_PAGE = ItemBuilder.from(ItemUtil.getItem(Config.PREVIOUS_PAGE_ITEM.asUppercase())).setName(Lang.PREVIOUS_PAGE.asColoredString());

    @Override
    public void create() {
        this.paginatedGui = Gui.paginated()
                .pageSize(Config.WARP_LISTING_MENU_SIZE.asInteger() - 9)
                .rows(Config.WARP_LISTING_MENU_SIZE.asInteger() / 9)
                .title(Component.text(this.getMenuTitle().asColoredString().replace("%page%", String.valueOf(page))))
                .disableAllInteractions()
                .create();
    }

    @Override
    public void fill(/*List<Warp> foundWarps*/) {
        final User user = UserHandler.getUser(player);
        user.addData(DataSelectorType.ACTUAL_PAGE, paginatedGui.getCurrentPageNum());
        user.addData(DataSelectorType.ACTUAL_MENU, this);
        user.addData(DataSelectorType.SELECTED_CATEGORY, categoryName);
        user.addData(DataSelectorType.SELECTED_SORT, sortType);

        final Category openedCategory = CategoryManager.getCategoryFromName(categoryName);

        //if (paginatedGui.getPrevPageNum() != paginatedGui.getCurrentPageNum())
        paginatedGui.setItem(Config.WARP_LISTING_MENU_SIZE.asInteger() - 9, PREVIOUS_PAGE.asGuiItem(event -> {
            paginatedGui.previous();
            paginatedGui.updateTitle(this.getMenuTitle().asColoredString().replace("%page%", String.valueOf(paginatedGui.getCurrentPageNum())));
        }));

        //if (paginatedGui.getNextPageNum() != paginatedGui.getCurrentPageNum())
        paginatedGui.setItem(Config.WARP_LISTING_MENU_SIZE.asInteger() - 1, NEXT_PAGE.asGuiItem(event -> {
            paginatedGui.next();
            paginatedGui.updateTitle(this.getMenuTitle().asColoredString().replace("%page%", String.valueOf(paginatedGui.getCurrentPageNum())));
        }));

        Sortable nextSortType = getWarpHandler().getSortingManager().nextSortType(sortType);

        List<String> sortLore = new ArrayList<>();
        for (Sortable cachedSortType : getWarpHandler().getSortingManager().getSortTypes()) {
            sortLore.add(TextUtil.colorize((sortType.equals(cachedSortType) ? Config.SELECTED_SORT.asString() : Config.OTHER_SORT.asString()) + cachedSortType.getName().asColoredString()));
        }

        sortLore.add(" ");

        sortLore.add(Lang.CLICK_TO_SORT_BY.asReplacedString(player, new HashMap<String, String>() {{
            put("%selector%", nextSortType.getName().asColoredString());
        }}));

        if (Config.ENABLE_WARP_SEARCH.asBoolean()) {
            paginatedGui
                    .setItem(Config.WARP_LISTING_MENU_SIZE.asInteger() - 2, ItemBuilder.from(ItemUtil.getItem(Config.SEARCH_WARP_ITEM.asUppercase()))
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

        if (!(this instanceof MyWarpsMenu))
            paginatedGui
                    .setItem(Config.WARP_LISTING_MENU_SIZE.asInteger() - 8, ItemBuilder.from(ItemUtil.getItem(Config.SORT_WARPS_ITEM.asUppercase()))
                            .setName(Lang.SORT_WARPS.asColoredString())
                            .setLore(sortLore)
                            .asGuiItem(event -> {
                                if (canSort(player)) {
                                    paginatedGui.clearPageItems();
                                    open(player, categoryName, nextSortType, foundWarps);
                                } else {
                                    player.sendMessage(Lang.WAIT_BEFORE_NEXT_ACTION.asColoredString());
                                }
                            })
                    );

        final List<Warp> warps = (foundWarps == null) ? new ArrayList<>() : foundWarps;

        if (warps.isEmpty() && foundWarps == null) {
            if (this instanceof DefaultWarpsMenu) {
                if (openedCategory.isDefaultCategory()) {
                    warps.addAll(PlayerWarpsPlugin.getWarpHandler().getWarps().stream()
                            .filter(Warp::isAccessible)
                            .collect(Collectors.toList()));
                } else {
                    warps.addAll(PlayerWarpsPlugin.getWarpHandler().getWarps().stream()
                            .filter(warp -> warp.isAccessible() && (warp.getCategory() == null || warp.getCategory().getType().equalsIgnoreCase(categoryName)))
                            .collect(Collectors.toList()));
                }


                getWarpHandler().getSortingManager().sortWarps(warps, sortType); //TODO: Async

            } else if (this instanceof MyWarpsMenu) {
                warps.addAll(PlayerWarpsPlugin.getWarpHandler().getWarps().stream().filter(warp -> warp.isOwner(player)).collect(Collectors.toList()));
            } else if (this instanceof FavoriteWarpsMenu) {
                warps.addAll(PlayerWarpsPlugin.getWarpHandler().getPlayerFavoriteWarps(player));
            }
        }

        //getWarpHandler().getSortingManager().sortWarps(warps, sortType);
        //new SortWarpAction().execute(player, null, new SortWarpAction.Pair(warps, sortType));

        final Lang warpLore = this instanceof MyWarpsMenu ? Lang.OWN_WARP_LORE : Lang.WARP_LORE;

        GuiItem guiItem;
        if (warps.isEmpty()) {
            if (this instanceof MyWarpsMenu && Config.ENABLE_HINTS.asBoolean()) {
                guiItem = new GuiItem(
                        ItemBuilder
                                .from(ItemUtil.getItem(Config.HELP_ITEM.asUppercase()))
                                .setName(Lang.HELP_DISPLAY_NAME.asColoredString())
                                .setLore(Lang.HELP_LORE.asReplacedList())
                                .build()
                );
                paginatedGui.setItem(Config.WARP_LISTING_MENU_SIZE.asInteger() - 32, guiItem);
            } else {
                guiItem = new GuiItem(
                        ItemBuilder
                                .from(ItemUtil.getItem(Config.NO_WARP_FOUND_ITEM.asUppercase()))
                                .setName(Lang.NO_WARP_FOUND.asColoredString())
                                .build()
                );
                paginatedGui.setItem(Config.WARP_LISTING_MENU_SIZE.asInteger() - 32, guiItem);
            }
        } else {
            for (Warp warp : warps) {
                if (warp.getLocation() == null) {
                    guiItem = new GuiItem(ItemBuilder
                            .from(Material.BARRIER)
                            .setName(TextUtil.colorize(warp.getDisplayName()))
                            .setLore(Lang.WARP_IN_DELETED_WORLD.asColoredString())
                            .build());
                } else {
                    List<String> lore = warpLore.asReplacedList(player, new HashMap<String, String>() {{
                                put("%creationDate%", DateUtil.getFormatter().format(warp.getDateCreated()));
                                put("%world%", warp.getLocation().getWorld().getName());
                                put("%voters%", String.valueOf(warp.getReviewers().size()));
                                put("%price%", warp.getAdmission() == 0
                                        ? Lang.FREE_OF_CHARGE.asColoredString()
                                        : NumberUtil.formatNumber(warp.getAdmission()) + " " + Config.CURRENCY_SYMBOL.asString());
                                put("%today%", String.valueOf(warp.getTodayVisits()));
                                put("%status%", warp.getStatus().getText());
                                put("%ratings%", String.valueOf(NumberUtil.round(warp.getConvertedRating(), 1)));
                                put("%stars%", TextUtil.createRatingFormat(warp));
//                                        put("%lore%", warp.getDescription() == null
//                                                ? Lang.NO_DESCRIPTION.asColoredString()
//                                                : TextUtil.splitLoreIntoLines(warp.getDescription(), 5)); // Použití funkce na rozdělení textu
                                put("%visits%", String.valueOf(warp.getVisits()));
                                put("%owner-name%", Bukkit.getOfflinePlayer(warp.getOwner()).getName() == null ? "Unknown" : Bukkit.getOfflinePlayer(warp.getOwner()).getName());
                            }}
                    );


                    guiItem = ItemBuilder
                            .from((warp.getMenuItem() == null ? ItemUtil.getItem(Config.DEFAULT_WARP_ITEM.asString(), warp.getOwner()) : warp.getMenuItem().clone()))
                            .setName(TextUtil.colorize(Config.WARP_NAME_FORMAT.asString().replace("%warpName%", warp.getDisplayName())))
                            .setLore(TextUtil.colorize(TextUtil.insertListIntoList(
                                            lore,
                                            TextUtil.splitByWords(warp.getDescription() == null ? Lang.NO_DESCRIPTION.asColoredString() : warp.getDescription(), 5, Config.WARP_DESCRIPTION_COLOR.asString())
                                    )
                            )).asGuiItem();

                    guiItem.setAction(event -> {
                        if (PlayerWarpsPlugin.getWarpHandler().areWarps()) {
                            switch (event.getClick()) {
                                case LEFT:
                                    player.closeInventory();
                                    new PreTeleportToWarpAction().setMenuToOpen(this).proceed(player, warp);
                                    break;
                                case RIGHT:
                                case SHIFT_RIGHT:
                                    if (this instanceof MyWarpsMenu) {
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
                                    Menu actualMenu = (Menu) user.getData(DataSelectorType.ACTUAL_MENU);
                                    new FavoriteWarpAction().proceed(player, warp, null, actualMenu, page);
                                    break;
                            }
                        }
                    });
                }

                paginatedGui.addItem(guiItem);
            }
        }

        setDefaultItems(player, paginatedGui);
    }

    private boolean canSort(Player player) {
        return !sortingCooldowns.contains(player);
    }

    @Override
    public BaseGui getMenu() {
        return this.paginatedGui;
    }

    @Override
    public short getMenuSize() {
        return Config.WARP_LISTING_MENU_SIZE.asShort();
    }

    @Override
    public Lang getMenuTitle() {
        return Lang.DENY;
    }

    @Override
    public void open(Player player) {
        open(player, "all", getWarpHandler().getSortingManager().getDefaultSortType(), null);
    }

    public void open(Player player, String categoryName, Sortable sortType) {
        open(player, categoryName, sortType, null);
    }

    public void open(Player player, String categoryName, Sortable sortType, List<Warp> foundWarps) {
        this.player = player;
        this.categoryName = categoryName;
        this.sortType = sortType;
        this.foundWarps = foundWarps;

        sortingCooldowns.add(player);

        create();

        CompletableFuture.runAsync(this::fill, MENU_EXECUTOR).thenAccept(unused -> {
            sortingCooldowns.remove(player);
            getMenu().update();
        });

        paginatedGui.open(player);
    }

    public WarpsMenu setPage(int page) {
        this.page = page;
        return this;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public static class MyWarpsMenu extends WarpsMenu {

        @Override
        public Lang getMenuTitle() {
            return Lang.MY_WARP_TITLE;
        }
    }

    public static class FavoriteWarpsMenu extends WarpsMenu {

        @Override
        public Lang getMenuTitle() {
            return Lang.FAVORITES_TITLE;
        }
    }

    public static class DefaultWarpsMenu extends WarpsMenu {

        @Override
        public Lang getMenuTitle() {
            return Lang.WARPS_TITLE;
        }
    }
}
