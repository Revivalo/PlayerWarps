package cz.revivalo.playerwarps.guimanager;

import cz.revivalo.playerwarps.categories.Category;
import cz.revivalo.playerwarps.lang.Lang;
import cz.revivalo.playerwarps.playerconfig.PlayerConfig;
import cz.revivalo.playerwarps.warp.Warp;
import cz.revivalo.playerwarps.warp.WarpHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class GUIManager {
    private final WarpHandler warpHandler;

    private final HashMap<UUID, List<Inventory>> pages = new HashMap<>();
    private final HashMap<UUID, Integer> actualPage = new HashMap<>();
    private final HashMap<UUID, String> chat = new HashMap<>();

    private final DateFormat formatter;

    private final HashMap<String, Warp> warpList;
    private List<Category> categories;

    public GUIManager(final WarpHandler warpHandler){
        this.warpHandler = warpHandler;

        warpList = warpHandler.getWarpList();
        formatter = new SimpleDateFormat(Lang.DATE_FORMAT.getString());
    }

    public void openCategories(final Player player){
        categories = warpHandler.getCategories();
        final Inventory inv = Bukkit.createInventory(new Holders.Categories(), 54, Lang.CATEGORY_TITLE.getString());

        if (!Lang.ENABLE_CATEGORIES_BACKGROUND.getString().equalsIgnoreCase("none")){
            for (int i = 0; i < 54; i++){
                inv.setItem(i, createGuiItem(Lang.ENABLE_CATEGORIES_BACKGROUND.getString().toUpperCase(), 1, false, " ", null));
            }
        }

        for (final Category category : categories){
            inv.setItem(category.getPosition(), createGuiItem(category.getItem(), 1, false, category.getName().replace("%number%", String.valueOf(warpHandler.getWarpOfType(category.getType()))), category.getLore()));
        }
        createGuiItems(inv, "list");

        player.openInventory(inv);
    }

    public void openWarpsMenu(final Player player, String type, boolean myWarps){
        final UUID id = player.getUniqueId();
        actualPage.put(id, 0);
        if (!warpList.isEmpty()) {
            List<Warp> warps = new ArrayList<>();
            if (type.equalsIgnoreCase("all")){
                for (final Warp warp : warpList.values()) {
                    if (myWarps) {
                        if (!Objects.equals(id, warp.getOwner())) continue;
                    }
                    else if (warp.isPrivateState()) continue;
                    warps.add(warp);
                }
            } else {
                for (final Warp warp : warpList.values()) {
                    if (warp.isPrivateState()) continue;
                    if (warp.getType() == null) continue;
                    if (warp.getType().equalsIgnoreCase(type)){
                        warps.add(warp);
                    }
                }
            }
            List<Inventory> page = new ArrayList<>();
            int i = warps.size();
            int j = 0;
            int number = 1;
            while (i >= 0) {
                i -= 45;
                ++j;
            }
            for (int k = 0; k < j; k++) {
                final Inventory inv = Bukkit.createInventory(myWarps ? new Holders.MyWarps() : new Holders.WarpsList(), 54, myWarps ? Lang.MYWARPSTITLE.getString().replace("%page%", String.valueOf(k + 1)) : Lang.WARPSTITLE.getString().replace("%page%", String.valueOf(k + 1)));
                if ((k + 1) == j) {
                    for (int l = 0; l < (i + 45); l++) {
                        final Warp warp = warpList.get(warps.get(number - 1).getName());
                        final String warpName = Lang.WARP_NAME_FORMAT.getString().replace("%warpName%", warp.getName());
                        inv.setItem(l, createGuiItem(warp.getItem(), 1,false, warpName, myWarps ? replace(Lang.OWN_WARP_LORE.getStringList(), warp.getName()) : replace(Lang.WARP_LORE.getStringList(), warp.getName())));
                        ++number;
                    }
                } else {
                    for (int l = 0; l < 45; l++) {
                        final Warp warp = warpList.get(warps.get(number - 1).getName());
                        final String warpName = Lang.WARP_NAME_FORMAT.getString().replace("%warpName%", warp.getName());
                        inv.setItem(l, createGuiItem(warp.getItem(), 1,false, warpName, myWarps ? replace(Lang.OWN_WARP_LORE.getStringList(), warp.getName()) : replace(Lang.WARP_LORE.getStringList(), warp.getName())));
                        ++number;
                    }
                }

                createGuiItems(inv, myWarps ? "mywarps" : "list");
                if (k != 0){
                    inv.setItem(45, createGuiItem(Lang.PAGEITEM.getString().toUpperCase(), 1,false, Lang.PREVIOUS_PAGE.getString(), null));
                } if (k != (j - 1)){
                    inv.setItem(53, createGuiItem(Lang.PAGEITEM.getString().toUpperCase(), 1,false, Lang.NEXTPAGE.getString(), null));
                }

                page.add(inv);
            }

            pages.put(id, page);
            player.openInventory(pages.get(id).get(0));
        } else {
            final Inventory inv = Bukkit.createInventory(myWarps ? new Holders.MyWarps() : new Holders.WarpsList(), 54, myWarps ? Lang.MYWARPSTITLE.getString().replace("%page%", "1") : Lang.WARPSTITLE.getString().replace("%page%", "1"));
            if (!myWarps) inv.setItem(22, createGuiItem("BARRIER", 1,false, Lang.NO_WARPS.getString(), null));
            else inv.setItem(22, createGuiItem("BARRIER", 1, false, Lang.HELPITEMNAME.getString(), Lang.HELP_LORE.getStringList()));

            createGuiItems(inv, myWarps ? "mywarps" : "list");
            player.openInventory(inv);
        }
    }

    public void openSetUpMenu(Player p, String warpName){
        final Inventory inv = Bukkit.createInventory(new Holders.SetUp(), 54, warpName);

        final Warp warp = warpList.get(warpName);

        String item = warp.getItem();
        String type = warp.getType();
        List<String> lore = replace(Lang.OWN_WARP_LORE.getStringList(), warpName);
        boolean disabled = warp.isDisabled();
        boolean privacy = warp.isPrivateState();
        inv.setItem(4, createGuiItem(item,1, false, Lang.OWN_WARP_ITEM_NAME.getString().replace("%warp%", warpName), lore));
        inv.setItem(11, createGuiItem("SUNFLOWER",1, false, Lang.SET_PRICE.getString(), Lang.SET_PRICE_LORE.getStringList()));
        inv.setItem(12, createGuiItem(type == null ? "WHITE_BANNER" : getItemOfCategory(type),1, false, Lang.CHANGE_TYPE.getString(), Lang.CHANGE_TYPE_LORE.getStringList()));
        inv.setItem(13, createGuiItem("IRON_DOOR",1, privacy, Lang.PRIVACY.getString(), privacy ? Lang.PRIVATE_ENABLE_LORE.getStringList() : Lang.PRIVATE_DISABLE_LORE.getStringList()));
        inv.setItem(14, createGuiItem("ITEM_FRAME", 1,false, Lang.CHANGE_ITEM.getString(), Lang.CHANGE_ITEM_LORE.getStringList()));
        inv.setItem(15, createGuiItem("NAME_TAG",1, false, Lang.CHANGE_LABEL.getString(), Lang.CHANGE_LABEL_LORE.getStringList()));
        inv.setItem(22, createGuiItem(disabled ? "GRAY_DYE" : "LIME_DYE",1, false, Lang.PWARP_ENABLE.getString(), disabled ? Lang.PWARP_DISABLE_LORE.getStringList() : Lang.PWARP_ENABLE_LORE.getStringList()));
        inv.setItem(39, createGuiItem("OAK_SIGN", 1, false, Lang.RENAME_WARP.getString(), Lang.RENAME_WARP_LORE.getStringList()));
        inv.setItem(40, createGuiItem("BARRIER",1, false, Lang.REMOVE_WARP.getString(), Lang.REMOVE_WARP_LORE.getStringList()));
        inv.setItem(41, createGuiItem("PLAYER_HEAD", 1, false, Lang.CHANGE_OWNER.getString(), Lang.CHANGE_OWNER_LORE.getStringList()));
        createGuiItems(inv, "");

        p.openInventory(inv);
    }

    public void openChangeTypeMenu(final Player player, String warp){
        final Inventory inv = Bukkit.createInventory(new Holders.ChangeType(), 36, warp);

        int i = 0;
        if (categories != null) {
            for (Category category : categories) {
                inv.setItem(i, createGuiItem(category.getItem(), 1, false, Lang.applyColor("&e" + StringUtils.capitalize(category.getType())), null));
                ++i;
            }
        } else inv.setItem(13, createGuiItem("BARRIER", 1, false, Lang.applyColor("&cCategories are disabled"), null));

        player.openInventory(inv);
    }

    public void openReviewMenu(final Player player, String warp){
        final Inventory inv = Bukkit.createInventory(new Holders.Review(), 36, Lang.applyColor(warp));

        String item = Lang.STAR_REVIEW_ITEM.getString().toUpperCase();
        inv.setItem(11, createGuiItem(item,1, false, Lang.ONE_STAR.getString(), null));
        inv.setItem(12, createGuiItem(item,2, false, Lang.TWO_STARS.getString(), null));
        inv.setItem(13, createGuiItem(item,3, false, Lang.THREE_STARS.getString(), null));
        inv.setItem(14, createGuiItem(item,4, false, Lang.FOUR_STARS.getString(), null));
        inv.setItem(15, createGuiItem(item,5, false, Lang.FIVE_STARS.getString(), null));
        inv.setItem(31, createGuiItem(Lang.BACK_ITEM.getString().toUpperCase(),1, false, Lang.BACK_NAME.getString(), null));

        player.openInventory(inv);
    }

    public void openTeleportAcceptMenu(final Player player, int price){
        final Inventory inv = Bukkit.createInventory(new Holders.TeleportAccept(), 27, Lang.ACCEPT_TELEPORT_WITH_ADMISSION.getString().replace("%price%", String.valueOf(price)));

        inv.setItem(11, createGuiItem("LIME_STAINED_GLASS_PANE",1, false, Lang.ACCEPT.getString(), null));
        inv.setItem(15, createGuiItem("RED_STAINED_GLASS_PANE",1, false, Lang.DENY.getString(), null));

        player.openInventory(inv);
    }

    public void openAcceptMenu(Player p, String warp){
        Inventory inv = Bukkit.createInventory(new Holders.Accept(), 27, Lang.ACCEPTMENUTITLE.getString().replace("%warp%", warp));

        inv.setItem(11, createGuiItem("LIME_STAINED_GLASS_PANE",1, false, Lang.ACCEPT.getString(), null));
        inv.setItem(15, createGuiItem("RED_STAINED_GLASS_PANE",1, false, Lang.DENY.getString(), null));

        p.openInventory(inv);
    }

    public void openFavorites(Player p){
        final UUID id = p.getUniqueId();
        final PlayerConfig playerData = PlayerConfig.getConfig(id);

        actualPage.put(id, 0);
        if (warpHandler.isWarps()) {
            List<String> warps = new ArrayList<>(playerData.getStringList("favorites"));
            warps.removeIf(warpHandler::checkWarp);
            List<Inventory> page = new ArrayList<>();
            int i = warps.size();
            int j = 0;
            int number = 1;
            while (i >= 0) {
                i -= 45;
                ++j;
            }
            for (int k = 0; k < j; k++) {
                Inventory inv = Bukkit.createInventory(new Holders.Favorites(), 54, Lang.FAVORITESTITLE.getString().replace("%page%", String.valueOf(k + 1)));
                if ((k + 1) == j) {
                    for (int l = 0; l < (i + 45); l++) {
                        Warp warp = warpList.get(warps.get(number - 1));
                        inv.setItem(l, createGuiItem(warp.getItem(), 1, false, warp.getName(), replace(Lang.WARP_LORE.getStringList(), warp.getName())));
                        ++number;
                    }
                } else {
                    for (int l = 0; l < 45; l++) {
                        Warp warp = warpList.get(warps.get(number - 1));
                        inv.setItem(l, createGuiItem(warp.getItem(), 1, false, warp.getName(), replace(Lang.WARP_LORE.getStringList(), warp.getName())));
                        ++number;
                    }
                }

                createGuiItems(inv, "favorites");
                if (k != 0) {
                    inv.setItem(45, createGuiItem(Lang.PAGEITEM.getString().toUpperCase(), 1,false, Lang.PREVIOUS_PAGE.getString(), null));
                }
                if (k != (j - 1)) {
                    inv.setItem(53, createGuiItem(Lang.PAGEITEM.getString().toUpperCase(),1, false, Lang.NEXTPAGE.getString(), null));
                }

                page.add(inv);
            }
            pages.put(id, page);

            p.openInventory(pages.get(id).get(0));
        } else{
            Inventory inv = Bukkit.createInventory(new Holders.Favorites(), 54, Lang.FAVORITESTITLE.getString().replace("%page%", "1"));
            inv.setItem(22, createGuiItem("BARRIER", 1,false, Lang.NO_WARPS.getString(), null));
            createGuiItems(inv, "favorites");
            p.openInventory(inv);
        }
    }

    private void createGuiItems(final Inventory inventory, final String glow){
        inventory.setItem(48, createGuiItem(Lang.WARP_LIST_ITEM.getString().toUpperCase(),1, glow.equalsIgnoreCase("list"), Lang.WARPSITEMNAME.getString(), null));
        inventory.setItem(49, createGuiItem(Lang.MY_WARPS_ITEM.getString().toUpperCase(),1, glow.equalsIgnoreCase("mywarps"), Lang.MYWARPSITEMNAME.getString(), null));
        inventory.setItem(50, createGuiItem(Lang.FAVORITE_WARPS_ITEM.getString().toUpperCase(),1, glow.equalsIgnoreCase("favorites"), Lang.FAVORITEWARPSITEMNAME.getString(), null));
    }

    private ItemStack createGuiItem(String id, int amount, boolean glow, String name, List<String> lore) {
        final ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(id)), amount);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (!id.equalsIgnoreCase("PLAYER_HEAD")) {
                if (glow) {
                    Objects.requireNonNull(meta).addEnchant(Enchantment.LURE, 1, false);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
            }
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS);
            Objects.requireNonNull(meta).setDisplayName(name);
            meta.setLore(lore);
        }
        item.setItemMeta(meta);

        return item;
    }

    private String getItemOfCategory(String type){
        for (Category category : categories){
            if (category.getType().equalsIgnoreCase(type)){
                return category.getItem();
            }
        }
        return "WHITE_BANNER";
    }

    private List<String> replace(List<String> lore, String warpName){
        List<String> newLore = new ArrayList<>();
        if (warpName != null) {
            Warp warp = warpList.get(warpName);
            int price = warp.getPrice();
            for (String str : lore){
                String text = Lang.NO_DESCRIPTION.getString();
                StringBuilder ratings = new StringBuilder();
                Collection<UUID> reviewers = warp.getReviewers();
                double pocet;
                if (reviewers.size() == 0){
                    pocet = warp.getRating();
                } else {
                    pocet = (double) warp.getRating()/reviewers.size();
                }
                double j = pocet;
                for (int i = 0; i < 5; i++){
                    if (j - 1 >= 0){
                        j -= 1;
                        ratings.append("§e★");
                    } else {
                        ratings.append("§e☆");
                    }
                }
                if (warp.getLore() != null){
                    text = warp.getLore();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(warp.getDateCreated());
                newLore.add(Lang.applyColor(str.replace("%creationDate%", formatter.format(calendar.getTime())).replace("%world%", warp.getLoc().getWorld().getName()).replace("%voters%", String.valueOf(warp.getReviewers().size())).replace("%price%", price == 0 ? Lang.FREE_OF_CHARGE.getString() : price + " " + Lang.CURRENCY.getString()).replace("%today%", String.valueOf(warp.getTodayVisits())).replace("%ratings%", String.valueOf(round(pocet, 1))).replace("%availability%", warp.isDisabled() ? Lang.WARP_INACTIVE.getString() : Lang.WARP_ACTIVE.getString())).replace("%stars%", ratings.toString()).replace("%lore%", Objects.requireNonNull(text)).replace("%visits%", String.valueOf(warp.getVisits())).replace("%owner-name%", Objects.requireNonNull(Bukkit.getOfflinePlayer(warp.getOwner()).getName())));
            }
        } else {
            for (String str : lore){
                newLore.add(Lang.applyColor(str));
            }
        }
        return newLore;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public HashMap<UUID, List<Inventory>> getPages() {
        return pages;
    }

    public HashMap<UUID, Integer> getActualPage() {
        return actualPage;
    }

    public HashMap<UUID, String> getChat() {
        return chat;
    }
}