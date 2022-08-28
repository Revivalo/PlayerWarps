package cz.revivalo.playerwarps.listeners;

import cz.revivalo.playerwarps.categories.Category;
import cz.revivalo.playerwarps.guimanager.Holders;
import cz.revivalo.playerwarps.guimanager.GUIManager;
import cz.revivalo.playerwarps.lang.Lang;
import cz.revivalo.playerwarps.warp.Warp;
import cz.revivalo.playerwarps.warp.WarpHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class InventoryClickListener implements Listener {

    private final WarpHandler warpHandler;
    private final GUIManager guiManager;

    private final HashMap<String, Warp> warpsHashMap;

    public InventoryClickListener(final WarpHandler warpHandler, final GUIManager guiManager) {
        this.warpHandler = warpHandler;
        this.guiManager = guiManager;

        warpsHashMap = warpHandler.getWarpList();
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Holders.Favorites || holder instanceof Holders.TeleportAccept || holder instanceof Holders.ChangeType || holder instanceof Holders.SetUp || holder instanceof Holders.Categories || holder instanceof Holders.WarpsList || holder instanceof Holders.MyWarps || holder instanceof Holders.Review || holder instanceof Holders.Accept) {
            if (event.getClickedInventory() == null) return;
            final Player player = (Player) event.getWhoClicked();
            final UUID id = player.getUniqueId();
            event.setCancelled(true);
            if (Objects.equals(event.getClickedInventory().getHolder(), player)) {return;}
            if (event.getCurrentItem() == null) {return;}
            if (Objects.equals(Objects.requireNonNull(event.getClickedInventory()).getHolder(), player)) {return;}
            int slot = event.getSlot();
            ClickType click = event.getClick();
            if (slot == 46 || slot == 48 || slot == 49 || slot == 50) {
                if (slot == 48) {
                    if (Lang.ENABLE_CATEGORIES.getBoolean()) {
                        guiManager.openCategories(player);
                    } else {
                        guiManager.openWarpsMenu(player, "all", false);
                    }
                }
                else if (slot == 49) guiManager.openWarpsMenu(player, "all", true);
                else if (slot == 50) guiManager.openFavorites(player);
                return;
            }
            if (holder instanceof Holders.Accept){
                String[] data = warpHandler.remove.get(id).split(":");
                String warp = data[0];
                boolean fromCommand = Boolean.parseBoolean(data[1]);
                switch (slot){
                    case 11:
                        warpHandler.removeWarp(player, warp);
                        warpHandler.remove.remove(id);
                        if (fromCommand){
                            player.closeInventory();
                        } else {
                            guiManager.openWarpsMenu(player, "all", true);
                        }
                        break;
                    case 15:
                        if (fromCommand){
                            player.closeInventory();
                        } else {
                            guiManager.openSetUpMenu(player, warp);
                        }
                        warpHandler.remove.remove(id);
                        break;
                }
            } else if (holder instanceof Holders.ChangeType){
                String warp = event.getView().getTitle();
                warpHandler.setType(player, warp, ChatColor.stripColor(Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName().toUpperCase()));
                guiManager.openSetUpMenu(player, warp);
            } else if (holder instanceof Holders.SetUp){
                final String warpName = event.getView().getTitle();
                switch (slot) {
                    case 11:
                       /* new AnvilGUI.Builder()
                                .onClose(anvilPlayer -> {
                                    guiManager.openSetUpMenu(p, warpName);
                                })
                                .onComplete((anvilPlayer, text) -> {
                                    warp.setName(text);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            guiManager.openSetUpMenu(p, warpName);
                                        }
                                    }.runTaskLater(playerWarps, 3);
                                    return AnvilGUI.Response.close();
                                })
                                .preventClose()
                                .text(warp.getName())
                                .itemLeft(new ItemStack(Material.IRON_HELMET))
                                .itemRight(new ItemStack(Material.IRON_HELMET))
                                .title("Rename warp:")
                                .plugin(playerWarps)
                                .open(p);
                        openInput(p, warpHandler.getWarpList().get(warp));*/
                        // TODO: Dodělat
                        guiManager.getChat().put(id, warpName + ":price:true");
                        player.closeInventory();
                        player.sendMessage(Lang.PRICEWRITEMSG.getString().replace("%warp%", warpName));
                        break;
                    case 12:
                        guiManager.openChangeTypeMenu(player, warpName);
                        break;
                    case 13:
                        warpHandler.makePrivate(player, warpName, false);
                        guiManager.openSetUpMenu(player, warpName);
                        break;
                    case 14:
                        guiManager.getChat().put(id, warpName + ":item:true");
                        player.closeInventory();
                        player.sendMessage(Lang.ITEMWRITEMSG.getString().replace("%warp%", warpName));
                        break;
                    case 15:
                        guiManager.getChat().put(id, warpName + ":lore:true");
                        player.closeInventory();
                        player.sendMessage(Lang.TITLE_WRITE_MSG.getString().replace("%warp%", warpName));
                        break;
                    case 22:
                        warpHandler.disable(player, warpName);
                        guiManager.openSetUpMenu(player, warpName);
                        break;
                    case 39:
                        player.closeInventory();
                        guiManager.getChat().put(id, warpName + ":rename:true");
                        player.sendMessage(Lang.RENAME_MSG.getString().replace("%warp%", warpName));
                        break;
                    case 40:
                        warpHandler.remove.put(id, warpName + ":false");
                        guiManager.openAcceptMenu(player, warpName);
                        break;
                    case 41:
                        player.closeInventory();
                        guiManager.getChat().put(id, warpName + ":owner:true");
                        player.sendMessage(Lang.OWNER_CHANGE_MSG.getString().replace("%warp%", warpName));
                        break;
                }
            } else if (holder instanceof Holders.Review){
                final String warp = event.getView().getTitle();
                int actualPage = guiManager.getActualPage().get(id);
                switch (slot){
                    case 11:
                        warpHandler.review(player, warp, 1);
                        break;
                    case 12:
                        warpHandler.review(player, warp, 2);
                        break;
                    case 13:
                        warpHandler.review(player, warp, 3);
                        break;
                    case 14:
                        warpHandler.review(player, warp, 4);
                        break;
                    case 15:
                        warpHandler.review(player, warp, 5);
                        break;
                    case 31:
                }
                player.openInventory(guiManager.getPages().get(id).get(actualPage));
            } else if (holder instanceof Holders.Categories){
                for (Category category : warpHandler.getCategories()){
                    if (slot == category.getPosition()){
                        guiManager.openWarpsMenu(player, category.getType(), false);
                        break;
                    }
                }
            } else if (holder instanceof Holders.WarpsList) {
                int actualPage = guiManager.getActualPage().get(id);
                switch (slot) {
                    case 53:
                        if (guiManager.getPages().get(id).size() > actualPage + 1) {
                            guiManager.getActualPage().put(id, ++actualPage);
                            player.openInventory(guiManager.getPages().get(id).get(actualPage));
                        }
                        break;
                    case 45:
                        if (actualPage >= 1) {
                            guiManager.getActualPage().put(id, --actualPage);
                            player.openInventory(guiManager.getPages().get(id).get(actualPage));
                        }
                        break;
                    default:
                        if (warpHandler.isWarps()) {
                            String warpName = ChatColor.stripColor(Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName());
                            Warp warp = warpsHashMap.get(warpName);
                            UUID owner = warp.getOwner();
                            int price = warp.getPrice();
                            if (click == ClickType.LEFT) {
                                player.closeInventory();
                                if (!id.equals(owner)) {
                                    if (price != 0 && Lang.ALLOW_ACCEPT_TELEPORT_MENU.getBoolean()) {
                                        warpHandler.remove.put(id, warpName);
                                        guiManager.openTeleportAcceptMenu(player, price);
                                        break;
                                    }
                                }
                                warpHandler.warp(player, warpName);
                            } else if (click == ClickType.RIGHT) guiManager.openReviewMenu(player, warpName);
                            else if (click == ClickType.SHIFT_LEFT) warpHandler.favorite(player, warpName);
                        }
                }
            } else if (holder instanceof Holders.TeleportAccept){
                switch (slot){
                    case 11:
                        warpHandler.warp(player, warpHandler.remove.get(id));
                        player.closeInventory();
                        break;
                    case 15:
                        if (!warpHandler.openedFromCommand.contains(player)){
                            int actualPage = guiManager.getActualPage().get(id);
                            player.openInventory(guiManager.getPages().get(id).get(actualPage));
                        } else {
                            player.closeInventory();
                        }
                        break;
                }
                warpHandler.remove.remove(id);
            } else if (holder instanceof Holders.MyWarps) {
                int actualPage = guiManager.getActualPage().get(id);
                switch (slot) {
                    case 53:
                        if (guiManager.getPages().get(id).size() > actualPage + 1) {
                            guiManager.getActualPage().put(id, ++actualPage);
                            player.openInventory(guiManager.getPages().get(id).get(actualPage));
                        }
                        break;
                    case 45:
                        if (actualPage >= 1) {
                            guiManager.getActualPage().put(id, --actualPage);
                            player.openInventory(guiManager.getPages().get(id).get(actualPage));
                        }
                        break;
                    default:
                        if (warpHandler.isWarps()) {
                            String warpName = Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName();
                            if (event.getClick() == ClickType.LEFT) {
                                warpHandler.warp(player, ChatColor.stripColor(warpName));
                                player.closeInventory();
                            } else if (click == ClickType.RIGHT) {
                                if (!player.hasPermission("playerwarps.settings")){
                                    player.sendMessage(Lang.INSUFFICIENT_PERMS.getString());
                                    return;
                                }
                                guiManager.openSetUpMenu(player, ChatColor.stripColor(warpName));
                            }
                        }
                }
            } else {
                int actualPage = guiManager.getActualPage().get(id);
                switch (slot){
                    case 53:
                        if (guiManager.getPages().get(id).size() > actualPage + 1) {
                            guiManager.getActualPage().put(id, ++actualPage);
                            player.openInventory(guiManager.getPages().get(id).get(actualPage));
                        }
                        break;
                    case 45:
                        if (actualPage >= 1) {
                            guiManager.getActualPage().put(id, --actualPage);
                            player.openInventory(guiManager.getPages().get(id).get(actualPage));
                        }
                        break;
                    default:
                        if (warpHandler.isWarps()) {
                            String warpName = Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName();
                            Warp warp = warpsHashMap.get(warpName);
                            int price = warp.getPrice();
                            UUID owner = warp.getOwner();
                            switch (click) {
                                case LEFT:
                                    player.closeInventory();
                                    if (!id.equals(owner)) {
                                        if (price != 0 && Lang.ALLOW_ACCEPT_TELEPORT_MENU.getBoolean()) {
                                            warpHandler.remove.put(id, warpName);
                                            guiManager.openTeleportAcceptMenu(player, price);
                                        } else {
                                            warpHandler.warp(player, warpName);
                                        }
                                    } else {
                                        warpHandler.warp(player, warpName);
                                    }
                                    break;
                                case RIGHT:
                                    guiManager.openReviewMenu(player, warpName);
                                    break;
                                case SHIFT_LEFT:
                                    warpHandler.unfavored(player, warpName);
                                    guiManager.openFavorites(player);
                                    break;
                            }
                        }
                }
            }
        }
    }

    @EventHandler
    public void onTeleportMenuClose(final InventoryCloseEvent event){
        if (event.getInventory().getHolder() instanceof Holders.TeleportAccept){
            final Player player = (Player) event.getPlayer();
            warpHandler.openedFromCommand.remove(player);
        }
    }
}
