package cz.revivalo.playerwarps.guimanager;

import cz.revivalo.playerwarps.categories.Category;
import cz.revivalo.playerwarps.categories.CategoryManager;
import cz.revivalo.playerwarps.configuration.enums.Config;
import cz.revivalo.playerwarps.configuration.enums.Lang;
import cz.revivalo.playerwarps.playerconfig.PlayerConfig;
import cz.revivalo.playerwarps.user.UserManager;
import cz.revivalo.playerwarps.user.WarpAction;
import cz.revivalo.playerwarps.warp.Warp;
import cz.revivalo.playerwarps.warp.WarpHandler;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GUIManager {
    private final WarpHandler warpHandler;
    private final DateFormat formatter;
    private Collection<Category> categories;

    private final ItemStack ONE_STAR = ItemBuilder.from(Config.STAR_REVIEW_ITEM.asAnItem().getType()).amount(1).name(Component.text(Lang.ONE_STAR.asColoredString())).build();
    private final ItemStack TWO_STARS = ItemBuilder.from(Config.STAR_REVIEW_ITEM.asAnItem().getType()).amount(2).name(Component.text(Lang.TWO_STARS.asColoredString())).build();
    private final ItemStack THREE_STARS = ItemBuilder.from(Config.STAR_REVIEW_ITEM.asAnItem().getType()).amount(3).name(Component.text(Lang.THREE_STARS.asColoredString())).build();
    private final ItemStack FOUR_STARS = ItemBuilder.from(Config.STAR_REVIEW_ITEM.asAnItem().getType()).amount(4).name(Component.text(Lang.FOUR_STARS.asColoredString())).build();
    private final ItemStack FIVE_STARS = ItemBuilder.from(Config.STAR_REVIEW_ITEM.asAnItem().getType()).amount(5).name(Component.text(Lang.FIVE_STARS.asColoredString())).build();

    public GUIManager(final WarpHandler warpHandler){
        this.warpHandler = warpHandler;
        formatter = new SimpleDateFormat(Config.DATE_FORMAT.asString());
    }

    public void openCategories(final Player player){
        categories = CategoryManager.getCategories();
        final Gui gui = Gui.gui()
                .rows(6)
                .title(Component.text(Lang.CATEGORY_TITLE.asColoredString()))
                .disableAllInteractions()
                .create();

        final Optional<ItemStack> fillItem = Optional.ofNullable(Config.ENABLE_CATEGORIES_BACKGROUND.asAnItem());
        fillItem.ifPresent((itemStack -> {
            final GuiItem backgroundItem = ItemBuilder.from(itemStack).name(Component.text(" ")).asGuiItem();
            for (int i = 0; i < 54; i++){
                gui.setItem(i, backgroundItem);
            }
        }));

        categories
                .forEach(category -> gui.setItem(
                        category.getPosition(),
                        ItemBuilder.from(
                                category.getItem()
                        )
                                .name(Component.text(
                                category.getName()
                                        .replace("%number%", String.valueOf(warpHandler.getWarpOfType(category.getType())))
                        ))
                                .setLore(category.getLore())
                                .asGuiItem(event -> openWarpsMenu(player, WarpMenuType.DEFAULT, category.toString(), 1, SortType.VISITS))));

        createGuiItems(player, gui, WarpMenuType.DEFAULT);

        gui.open(player);
    }

    public void openWarpsMenu(final Player player, WarpMenuType menuType, String category, int page, SortType sortType){
        final UUID id = player.getUniqueId();
        final Category openedCategory = CategoryManager.getCategoryFromName(category);
        final PaginatedGui paginatedGui = Gui.paginated()
                .pageSize(45)
                .rows(6)
                .title(Component.text(menuType.getTitle().replace("%page%", String.valueOf(page))))
                .disableAllInteractions()
                .create();

        paginatedGui.setItem(45, ItemBuilder.from(Material.ARROW).name(Component.text(Lang.PREVIOUS_PAGE.asColoredString())).asGuiItem(event -> {
            paginatedGui.previous();
            paginatedGui.updateTitle(menuType.getTitle().replace("%page%", String.valueOf(paginatedGui.getCurrentPageNum())));
        }));
        paginatedGui.setItem(53, ItemBuilder.from(Material.ARROW).name(Component.text(Lang.NEXT_PAGE.asColoredString())).asGuiItem(event -> {
            paginatedGui.next();
            paginatedGui.updateTitle(menuType.getTitle().replace("%page%", String.valueOf(paginatedGui.getCurrentPageNum())));
        }));

        paginatedGui.setItem(46, ItemBuilder.from(Material.REPEATER)
                .name(Component.text(Lang.replaceString(Lang.SORTER.asColoredString(), new HashMap<String, String>(){{put("%sortingBy%", sortType.name());}})
                )).asGuiItem(event -> openWarpsMenu(player, menuType, category, page,
                        sortType == SortType.VISITS ? SortType.LATEST
                                : sortType == SortType.LATEST
                                    ? SortType.RATING : SortType.VISITS
                )));

        List<Warp> warps = new ArrayList<>();
        switch (menuType){
            case DEFAULT:
                warps.addAll(warpHandler.getWarps().stream().filter(warp ->
                        (openedCategory.isDefault() || warp.getCategory().getType().equalsIgnoreCase(category))
                        && !warp.isPrivateState()).collect(Collectors.toList()));
                warps.sort(sortType.getComparator());
                break;
            case OWNED:
                warps.addAll(warpHandler.getWarps().stream().filter(warp -> Objects.equals(id, warp.getOwner())).collect(Collectors.toList()));
                break;
            case FAVORITES:
                warps.addAll(PlayerConfig.getConfig(id).getStringList("favorites").stream()
                        .map(warpHandler::getWarpByID)
                        .collect(Collectors.toList()));
                warps.removeIf(warpHandler::checkWarp);
                break;
        }

        warps.forEach(warp -> {
            final GuiItem guiItem = new GuiItem(ItemBuilder.from(warp.getMenuItem())
                    .name(Component.text(Lang.applyColor(Config.WARP_NAME_FORMAT.asString().replace("%warpName%", warp.getName()))))
                    .setLore(Lang.WARP_LORE.asReplacedList(new HashMap<String, String>(){{
                                                               put("%creationDate%", formatter.format(warp.getDateCreated()));
                                                               put("%world%", warp.getLocation().getWorld().getName());
                                                               put("%voters%", String.valueOf(warp.getReviewers().size()));
                                                               put("%price%", warp.getPrice() == 0
                                                                       ? Lang.FREE_OF_CHARGE.asColoredString()
                                                                       : warp.getPrice() + " " + Config.CURRENCY_SYMBOL.asString());
                                                               put("%today%", String.valueOf(warp.getTodayVisits()));
                                                               put("%availability%", warp.isDisabled()
                                                                       ? Lang.WARP_INACTIVE.asColoredString()
                                                                       : Lang.WARP_ACTIVE.asColoredString());
                                                               put("%ratings%", String.valueOf(round(warp.getRating(), 1)));
                                                               put("%stars%", Config.createRatingFormat(warp));
                                                               put("%lore%", warp.getDescription() == null
                                                                       ? Lang.NO_DESCRIPTION.asColoredString()
                                                                       : Lang.applyColor(warp.getDescription()));
                                                               put("%visits%", String.valueOf(warp.getVisits()));
                                                               put("%owner-name%", Objects.requireNonNull(Bukkit.getOfflinePlayer(warp.getOwner()).getName()));
                                                           }}
                    )).build());

            guiItem.setAction(event -> {
                if (warpHandler.isWarps()) {
                    switch (event.getClick()){
                        case LEFT:
                            player.closeInventory();
                            warpHandler.warp(player, warp);
                            break;
                        case RIGHT:
                        case SHIFT_RIGHT:
                            UserManager.createUser(player, new Object[]{paginatedGui.getCurrentPageNum()});
                            if (menuType == WarpMenuType.OWNED) {
                                if (!player.hasPermission("playerwarps.settings")) {
                                    player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
                                    return;
                                }
                                openSetUpMenu(player, warp);
                            } else {
                                openReviewMenu(player, warp);
                            }
                            break;
                        case SHIFT_LEFT:
                            warpHandler.favorite(player, warp);
                            break;
                    }
                }
            });
            paginatedGui.addItem(guiItem);
        });
        //}
        createGuiItems(player, paginatedGui, menuType);
        paginatedGui.open(player, page);
    }

    public void openSetUpMenu(Player player, Warp warp){
        final Gui gui = Gui.gui()
                .title(Component.text(Lang.EDIT_WARP_MENU_TITLE.asColoredString().replace("%warp%", warp.getName())))
                .rows(6)
                .disableAllInteractions()
                .create();

        gui.setItem(4, ItemBuilder.from(warp.getMenuItem())
                .name(Component.text(Lang.OWN_WARP_ITEM_NAME.asColoredString().replace("%warp%", warp.getName())))
                .setLore(Lang.OWN_WARP_LORE.asReplacedList(new HashMap<String, String>(){{
                    put("%creationDate%", formatter.format(warp.getDateCreated()));
                    put("%world%", warp.getLocation().getWorld().getName());
                    put("%voters%", String.valueOf(warp.getReviewers().size()));
                    put("%price%", warp.getPrice() == 0
                            ? Lang.FREE_OF_CHARGE.asColoredString()
                            : warp.getPrice() + " " + Config.CURRENCY_SYMBOL.asString());
                    put("%today%", String.valueOf(warp.getTodayVisits()));
                    put("%availability%", warp.isDisabled()
                            ? Lang.WARP_INACTIVE.asColoredString()
                            : Lang.WARP_ACTIVE.asColoredString());
                    put("%ratings%", String.valueOf(round(warp.getRating(), 1)));
                    put("%stars%", Config.createRatingFormat(warp));
                    put("%lore%", warp.getDescription() == null
                            ? Lang.NO_DESCRIPTION.asColoredString()
                            : Lang.applyColor(warp.getDescription()));
                    put("%visits%", String.valueOf(warp.getVisits()));
                    put("%owner-name%", Objects.requireNonNull(Bukkit.getOfflinePlayer(warp.getOwner()).getName()));
                }}
        )).asGuiItem());

        gui.setItem(11, ItemBuilder.from(Material.SUNFLOWER).name(Component.text(Lang.SET_PRICE.asColoredString().replace("%warp%", warp.getName()))).setLore(Lang.SET_PRICE_LORE.asColoredList()).asGuiItem(event -> markPlayerForChatInput(player, warp, WarpAction.SET_ADMISSION, new Object[]{warp.getWarpID(), WarpAction.SET_ADMISSION, true})));
        gui.setItem(12, ItemBuilder.from(warp.getCategory() == null ? new ItemStack(Material.WHITE_BANNER) : warp.getCategory().getItem()).name(Component.text(Lang.CHANGE_TYPE.asColoredString())).setLore(Lang.CHANGE_TYPE_LORE.asColoredList()).asGuiItem(event -> openChangeTypeMenu(player, warp)));
        gui.setItem(13, ItemBuilder.from(Material.IRON_DOOR).glow(warp.isPrivateState()).name(Component.text(Lang.PRIVACY.asColoredString())).setLore(warp.isPrivateState() ? Lang.PRIVATE_ENABLE_LORE.asColoredList() : Lang.PRIVATE_DISABLE_LORE.asColoredList()).asGuiItem(event -> {
            warpHandler.makePrivate(player, warp,false);
            openSetUpMenu(player, warp);
        }));
        gui.setItem(14, ItemBuilder.from(Material.ITEM_FRAME).name(Component.text(Lang.CHANGE_ITEM.asColoredString())).setLore(Lang.CHANGE_ITEM_LORE.asColoredList()).asGuiItem(event -> markPlayerForChatInput(player, warp, WarpAction.SET_GUI_ITEM, new Object[]{warp.getWarpID(), WarpAction.SET_GUI_ITEM, true})));
        gui.setItem(15, ItemBuilder.from(Material.NAME_TAG).name(Component.text(Lang.CHANGE_DESCRIPTION.asColoredString())).setLore(Lang.CHANGE_DESCRIPTION_LORE.asColoredList()).asGuiItem(event -> markPlayerForChatInput(player, warp, WarpAction.SET_DESCRIPTION, new Object[]{warp.getWarpID(), WarpAction.SET_DESCRIPTION, true})));
        gui.setItem(22, ItemBuilder.from(warp.isDisabled() ? Material.GRAY_DYE : Material.LIME_DYE).name(Component.text(Lang.PWARP_ENABLE.asColoredString())).setLore(warp.isDisabled() ? Lang.PWARP_DISABLE_LORE.asColoredList() : Lang.PWARP_ENABLE_LORE.asColoredList()).asGuiItem(event -> {
            warpHandler.disable(player, warp, false);
            openSetUpMenu(player, warp);
        }));
        gui.setItem(39, ItemBuilder.from(Material.OAK_SIGN).name(Component.text(Lang.RENAME_WARP.asColoredString())).setLore(Lang.RENAME_WARP_LORE.asColoredList()).asGuiItem(event -> markPlayerForChatInput(player, warp, WarpAction.RENAME, new Object[]{warp.getWarpID(), WarpAction.RENAME, true})));
        gui.setItem(40, ItemBuilder.from(Material.BARRIER).name(Component.text(Lang.REMOVE_WARP.asColoredString())).setLore(Lang.REMOVE_WARP_LORE.asColoredList()).asGuiItem(event -> openAcceptMenu(player, warp, WarpAction.REMOVE)));
        gui.setItem(41, ItemBuilder.from(Material.PLAYER_HEAD).name(Component.text(Lang.CHANGE_OWNER.asColoredString())).setLore(Lang.CHANGE_OWNER_LORE.asColoredList()).asGuiItem(event -> markPlayerForChatInput(player, warp, WarpAction.CHANGE_OWNERSHIP, new Object[]{warp.getWarpID(), WarpAction.CHANGE_OWNERSHIP, true})));

        createGuiItems(player, gui, WarpMenuType.OWNED);

        gui.open(player);
    }

    private void markPlayerForChatInput(final Player player, Warp warp, WarpAction warpAction, Object[] data){
        player.closeInventory();
        UserManager.createUser(player, data);

        String messageToSent;
        switch (warpAction){
            case SET_GUI_ITEM: messageToSent = Lang.ITEM_WRITE_MSG.asColoredString(); break;
            case SET_ADMISSION: messageToSent = Lang.PRICE_WRITE_MESSAGE.asColoredString(); break;
            case RENAME: messageToSent = Lang.RENAME_MSG.asColoredString(); break;
            case CHANGE_OWNERSHIP: messageToSent = Lang.OWNER_CHANGE_MSG.asColoredString(); break;
            case SET_DESCRIPTION: messageToSent = Lang.SET_DESCRIPTION_MESSAGE.asColoredString(); break;
            default: messageToSent = "error";
        }

        player.sendMessage(Lang.replaceString(messageToSent, new HashMap<String, String>(){{put("%warp%", warp.getName());}}));
    }

    public void openChangeTypeMenu(final Player player, Warp warp){
        final Gui gui = Gui.gui()
                .disableAllInteractions()
                .rows(3)
                .title(Component.text(Lang.ACCEPT_MENU_TITLE.asReplacedString(new HashMap<String, String>(){{put("%warp%", warp.getName());}})))
                .create();

        if (categories != null) {
            categories
                    .forEach(category -> gui.addItem(ItemBuilder.from(category.getItem()).name(Component.text(StringUtils.capitalize(category.getType()))).asGuiItem(event -> {
                        warpHandler.setType(player, warp, category.getType());
                        openSetUpMenu(player, warp);
                    })));
        } else gui.setItem(13, ItemBuilder.from(Material.BARRIER).name(Component.text(Lang.CATEGORIES_ARE_DISABLED.asColoredString())).asGuiItem());

        gui.open(player);
    }

    public void openReviewMenu(final Player player, Warp warp){
        final Gui gui = Gui.gui()
                .disableAllInteractions()
                .rows(4)
                .title(Component.text(Lang.REVIEW_WARP_TITLE.asReplacedString(new HashMap<String, String>(){{put("%warp%", warp.getName());}})))
                .create();

        gui.setItem(11, ItemBuilder.from(ONE_STAR).asGuiItem(event -> warpHandler.review(player, warp, 1)));
        gui.setItem(12, ItemBuilder.from(TWO_STARS).asGuiItem(event -> warpHandler.review(player, warp, 2)));
        gui.setItem(13, ItemBuilder.from(THREE_STARS).asGuiItem(event -> warpHandler.review(player, warp, 3)));
        gui.setItem(14, ItemBuilder.from(FOUR_STARS).asGuiItem(event -> warpHandler.review(player, warp, 4)));
        gui.setItem(15, ItemBuilder.from(FIVE_STARS).asGuiItem(event -> warpHandler.review(player, warp, 5)));

        gui.setItem(31, ItemBuilder.from(Config.BACK_ITEM.asAnItem()).name(Component.text(Lang.BACK_NAME.asColoredString())).asGuiItem(event -> openWarpsMenu(player, WarpMenuType.DEFAULT, warp.getCategory() == null ? "all" : warp.getCategory().getType(), (int) UserManager.getUsersTemp(player.getUniqueId())[0], SortType.VISITS)));
        gui.open(player);
    }

    public void openAcceptMenu(Player player, Warp warp, WarpAction action){
        final Gui gui = Gui.gui()
                .disableAllInteractions()
                .rows(3)
                .title(Component.text(Lang.ACCEPT_MENU_TITLE.asReplacedString(new HashMap<String, String>(){{put("%warp%", warp.getName());}})))
                .create();

        gui.setItem(11, ItemBuilder.from(Material.LIME_STAINED_GLASS_PANE).name(Component.text(Lang.ACCEPT.asColoredString())).asGuiItem(event -> {
            switch (action){
                case TELEPORT:
                    warpHandler.warp(player, warp);
                    break;
                case REMOVE:
                    warpHandler.removeWarp(player, warp);
                    break;
            }
        }));
        gui.setItem(15, ItemBuilder.from(Material.RED_STAINED_GLASS_PANE).name(Component.text(Lang.DENY.asColoredString())).asGuiItem(event -> {
            if (action == WarpAction.REMOVE){
                openSetUpMenu(player,warp);
            }
        }));
        gui.open(player);
    }

    private void createGuiItems(final Player player, final BaseGui inventory, final WarpMenuType selectedMenuType){
        inventory.setItem(48, ItemBuilder.from(Config.WARP_LIST_ITEM.asAnItem()).glow(selectedMenuType == WarpMenuType.DEFAULT).name(Component.text(Lang.WARPS_ITEM_NAME.asColoredString())).asGuiItem(event -> openCategories(player)));
        inventory.setItem(49, ItemBuilder.from(Config.MY_WARPS_ITEM.asAnItem()).glow(selectedMenuType == WarpMenuType.OWNED).name(Component.text(Lang.MY_WARPS_ITEM_NAME.asColoredString())).asGuiItem(event -> openWarpsMenu(player, WarpMenuType.OWNED, null, 1, SortType.VISITS)));//(int) UserManager.getUsersTemp(player.getUniqueId())[0]))); //createGuiItem(Config.MY_WARPS_ITEM.asUppercase(),1, glow.equalsIgnoreCase("mywarps"), Lang.MY_WARPS_ITEM_NAME.asColoredString(), null));
        inventory.setItem(50, ItemBuilder.from(Config.FAVORITE_WARPS_ITEM.asAnItem()).glow(selectedMenuType == WarpMenuType.FAVORITES).name(Component.text(Lang.FAVORITE_WARPS_ITEM_NAME.asColoredString())).asGuiItem(event -> openWarpsMenu(player, WarpMenuType.FAVORITES, null, 1, SortType.VISITS))); //em(Config.FAVORITE_WARPS_ITEM.asUppercase(),1, glow.equalsIgnoreCase("favorites"), Lang.FAVORITE_WARPS_ITEM_NAME.asColoredString(), null));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public enum WarpMenuType {
        FAVORITES(Lang.FAVORITES_TITLE.asColoredString()), OWNED(Lang.MY_WARPS_TITLE.asColoredString()), DEFAULT(Lang.WARPS_TITLE.asColoredString());

        private final String title;
        WarpMenuType(String title) {
            this.title = title;
        }

        public String getTitle(){return title;}
    }

    public enum SortType {
        VISITS(Comparator.comparing(Warp::getVisits).reversed()), LATEST(Comparator.comparing(Warp::getDateCreated)), RATING(Comparator.comparing(Warp::getRating));

        @Getter
        private final Comparator comparator;
        <T> SortType(Comparator<T> comparator){
            this.comparator = comparator;
        }

    }
}