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
            Player p = (Player) event.getWhoClicked();
            UUID id = p.getUniqueId();
            event.setCancelled(true);
            if (Objects.equals(event.getClickedInventory().getHolder(), p)) {return;}
            if (event.getCurrentItem() == null) {return;}
            if (Objects.equals(Objects.requireNonNull(event.getClickedInventory()).getHolder(), p)) {return;}
            int slot = event.getSlot();
            ClickType click = event.getClick();
            if (slot == 46 || slot == 48 || slot == 49 || slot == 50) {
                if (slot == 48) {
                    if (Lang.ENABLECATEGORIES.getBoolean()) {
                        guiManager.openCategories(p);
                    } else {
                        guiManager.openWarpsMenu(p, "all", false);
                    }
                }
                else if (slot == 49) guiManager.openWarpsMenu(p, "all", true);
                else if (slot == 50) guiManager.openFavorites(p);
                return;
            }
            if (holder instanceof Holders.Accept){
                String[] data = warpHandler.remove.get(id).split(":");
                String warp = data[0];
                boolean fromCommand = Boolean.parseBoolean(data[1]);
                switch (slot){
                    case 11:
                        warpHandler.removeWarp(p, warp);
                        warpHandler.remove.remove(id);
                        if (fromCommand){
                            p.closeInventory();
                        } else {
                            guiManager.openWarpsMenu(p, "all", true);
                        }
                        break;
                    case 15:
                        if (fromCommand){
                            p.closeInventory();
                        } else {
                            guiManager.openSetUpMenu(p, warp);
                        }
                        warpHandler.remove.remove(id);
                        break;
                }
            } else if (holder instanceof Holders.ChangeType){
                String warp = event.getView().getTitle();
                warpHandler.setType(p, warp, ChatColor.stripColor(Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName().toUpperCase()));
                guiManager.openSetUpMenu(p, warp);
            } else if (holder instanceof Holders.SetUp){
                String warpName = event.getView().getTitle();
                Warp warp = warpHandler.getWarpList().get(warpName);
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
                        p.closeInventory();
                        p.sendMessage(Lang.PRICEWRITEMSG.content().replace("%warp%", warpName));
                        break;
                    case 12:
                        guiManager.openChangeTypeMenu(p, warpName);
                        break;
                    case 13:
                        warpHandler.makePrivate(p, warpName, false);
                        guiManager.openSetUpMenu(p, warpName);
                        break;
                    case 14:
                        guiManager.getChat().put(id, warpName + ":item:true");
                        p.closeInventory();
                        p.sendMessage(Lang.ITEMWRITEMSG.content().replace("%warp%", warpName));
                        break;
                    case 15:
                        guiManager.getChat().put(id, warpName + ":lore:true");
                        p.closeInventory();
                        p.sendMessage(Lang.TITLEWRITEMSG.content().replace("%warp%", warpName));
                        break;
                    case 22:
                        warpHandler.disable(p, warpName);
                        guiManager.openSetUpMenu(p, warpName);
                        break;
                    case 39:
                        p.closeInventory();
                        guiManager.getChat().put(id, warpName + ":rename:true");
                        p.sendMessage(Lang.RENAMEMSG.content().replace("%warp%", warpName));
                        break;
                    case 40:
                        warpHandler.remove.put(id, warpName + ":false");
                        guiManager.openAcceptMenu(p, warpName);
                        break;
                    case 41:
                        p.closeInventory();
                        guiManager.getChat().put(id, warpName + ":owner:true");
                        p.sendMessage(Lang.OWNERCHANGEMSG.content().replace("%warp%", warpName));
                        break;
                }
            } else if (holder instanceof Holders.Review){
                String warp = event.getView().getTitle();
                int actualPage = guiManager.getActualPage().get(id);
                switch (slot){
                    case 11:
                        warpHandler.review(p, warp, 1);
                        break;
                    case 12:
                        warpHandler.review(p, warp, 2);
                        break;
                    case 13:
                        warpHandler.review(p, warp, 3);
                        break;
                    case 14:
                        warpHandler.review(p, warp, 4);
                        break;
                    case 15:
                        warpHandler.review(p, warp, 5);
                        break;
                    case 31:
                }
                p.openInventory(guiManager.getPages().get(id).get(actualPage));
            } else if (holder instanceof Holders.Categories){
                for (Category category : warpHandler.getCategories()){
                    if (slot == category.getPosition()){
                        guiManager.openWarpsMenu(p, category.getType(), false);
                        break;
                    }
                }
            } else if (holder instanceof Holders.WarpsList) {
                int actualPage = guiManager.getActualPage().get(id);
                switch (slot) {
                    case 53:
                        if (guiManager.getPages().get(id).size() > actualPage + 1) {
                            guiManager.getActualPage().put(id, ++actualPage);
                            p.openInventory(guiManager.getPages().get(id).get(actualPage));
                        }
                        break;
                    case 45:
                        if (actualPage >= 1) {
                            guiManager.getActualPage().put(id, --actualPage);
                            p.openInventory(guiManager.getPages().get(id).get(actualPage));
                        }
                        break;
                    default:
                        if (warpHandler.isWarps()) {
                            String warpName = ChatColor.stripColor(Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName());
                            Warp warp = warpsHashMap.get(warpName);
                            UUID owner = warp.getOwner();
                            int price = warp.getPrice();
                            if (click == ClickType.LEFT) {
                                p.closeInventory();
                                if (!id.equals(owner)) {
                                    if (price != 0 && Lang.ALLOWACCEPTTELEMPORTMENU.getBoolean()) {
                                        warpHandler.remove.put(id, warpName);
                                        guiManager.openTeleportAcceptMenu(p, price);
                                        break;
                                    }
                                }
                                warpHandler.warp(p, warpName);
                            } else if (click == ClickType.RIGHT) guiManager.reviewMenu(p, warpName);
                            else if (click == ClickType.SHIFT_LEFT) warpHandler.favorite(p, warpName);
                        }
                }
            } else if (holder instanceof Holders.TeleportAccept){
                switch (slot){
                    case 11:
                        warpHandler.warp(p, warpHandler.remove.get(id));
                        p.closeInventory();
                        break;
                    case 15:
                        if (!warpHandler.openedFromCommand.contains(p)){
                            int actualPage = guiManager.getActualPage().get(id);
                            p.openInventory(guiManager.getPages().get(id).get(actualPage));
                        } else {
                            p.closeInventory();
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
                            p.openInventory(guiManager.getPages().get(id).get(actualPage));
                        }
                        break;
                    case 45:
                        if (actualPage >= 1) {
                            guiManager.getActualPage().put(id, --actualPage);
                            p.openInventory(guiManager.getPages().get(id).get(actualPage));
                        }
                        break;
                    default:
                        if (warpHandler.isWarps()) {
                            String warpName = Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName();
                            if (event.getClick() == ClickType.LEFT) {
                                warpHandler.warp(p, ChatColor.stripColor(warpName));
                                p.closeInventory();
                            } else if (click == ClickType.RIGHT) {
                                if (!p.hasPermission("playerwarps.settings")){
                                    p.sendMessage(Lang.INSUFFICIENTPERMS.content());
                                    return;
                                }
                                guiManager.openSetUpMenu(p, ChatColor.stripColor(warpName));
                            }
                        }
                }
            } else {
                int actualPage = guiManager.getActualPage().get(id);
                switch (slot){
                    case 53:
                        if (guiManager.getPages().get(id).size() > actualPage + 1) {
                            guiManager.getActualPage().put(id, ++actualPage);
                            p.openInventory(guiManager.getPages().get(id).get(actualPage));
                        }
                        break;
                    case 45:
                        if (actualPage >= 1) {
                            guiManager.getActualPage().put(id, --actualPage);
                            p.openInventory(guiManager.getPages().get(id).get(actualPage));
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
                                    p.closeInventory();
                                    if (!id.equals(owner)) {
                                        if (price != 0 && Lang.ALLOWACCEPTTELEMPORTMENU.getBoolean()) {
                                            warpHandler.remove.put(id, warpName);
                                            guiManager.openTeleportAcceptMenu(p, price);
                                        } else {
                                            warpHandler.warp(p, warpName);
                                        }
                                    } else {
                                        warpHandler.warp(p, warpName);
                                    }
                                    break;
                                case RIGHT:
                                    guiManager.reviewMenu(p, warpName);
                                    break;
                                case SHIFT_LEFT:
                                    warpHandler.unfavored(p, warpName);
                                    guiManager.openFavorites(p);
                                    break;
                            }
                        }
                }
            }
        }
    }

    @EventHandler
    public void onTeleportMenuClose(InventoryCloseEvent event){
        if (event.getInventory().getHolder() instanceof Holders.TeleportAccept){
            Player p = (Player) event.getPlayer();
            warpHandler.openedFromCommand.remove(p);
        }
    }
}
