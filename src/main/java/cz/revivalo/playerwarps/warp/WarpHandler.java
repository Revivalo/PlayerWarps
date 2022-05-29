package cz.revivalo.playerwarps.warp;

import com.tchristofferson.configupdater.ConfigUpdater;
import cz.revivalo.playerwarps.PlayerWarps;
import cz.revivalo.playerwarps.categories.Category;
import cz.revivalo.playerwarps.datamanager.DataManager;
import cz.revivalo.playerwarps.lang.Lang;
import cz.revivalo.playerwarps.playerconfig.PlayerConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class WarpHandler {

    private final PlayerWarps playerWarps;
    private final Economy econ;
    private final DataManager dataManager;
    private final FileConfiguration data;

    public HashMap<UUID, String> remove = new HashMap<>();
    private final HashMap<String, Warp> warpsHashMap;
    private final HashMap<Player, Integer> tp = new HashMap<>();

    private List<Category> categories;
    private final List<Material> bannedItems;
    private final List<String> bannedWorlds;
    public  List<Player> openedFromCommand = new ArrayList<>();

    private File categoriesFile;
    private FileConfiguration categoriesData;

    public WarpHandler(final PlayerWarps playerWarps, final DataManager dataManager, final Economy econ){
        this.playerWarps = playerWarps;
        this.dataManager = dataManager;
        this.data = dataManager.getData();
        this.econ = econ;

        warpsHashMap = new HashMap<>();

        bannedItems = new ArrayList<>();
        bannedItems.add(Material.NETHER_PORTAL);
        bannedItems.add(Material.END_PORTAL);
        bannedItems.add(Material.AIR);
        for (String item : Lang.BANNEDITEMS.contentLore()) {
            bannedItems.add(Material.valueOf(item.toUpperCase()));
        }

        bannedWorlds = new ArrayList<>();
        bannedWorlds.addAll(Lang.BANNEDWORLDS.contentLore());
    }

    public void warp(Player p, Warp warp){
        warp(p, warp.getName());
    }

    public void warp(Player p, String warpName) {
        if (!warpsHashMap.containsKey(warpName)) {
            p.sendMessage(Lang.NONEXISTINGWARP.content());
        } else {
            Warp warp = warpsHashMap.get(warpName);
            UUID id = warp.getOwner();
            boolean isOwner = Objects.equals(p.getUniqueId(), id);
            if (warp.isDisabled() && (!p.hasPermission("playerwarps.admin") || !isOwner)) {
                p.sendMessage(Lang.WARPISDISABLED.content().replace("%warp%", warpName));
            } else {
                int price = warp.getPrice();
                Location loc = warp.getLoc();
                boolean hasBypass = p.hasPermission("playerwarps.delay.bypass");
                if (!isOwner) {
                    if (econ == null) teleportPlayer(p, loc, hasBypass);
                    else {
                        if (price == 0) {
                            teleportPlayer(p, loc, hasBypass);
                        } else {
                            if (econ.withdrawPlayer(p, price).transactionSuccess()) {
                                add(Bukkit.getOfflinePlayer(id), price);
                                teleportPlayer(p, loc, hasBypass);
                            } else {
                                p.sendMessage(Lang.INSUFFICIENTBALANCETOTELEPORT.content().replace("%warp%", warpName));
                                return;
                            }
                        }
                    }
                } else teleportPlayer(p, loc, hasBypass);
                if (price != 0 && !isOwner) {
                    p.sendMessage(Lang.TELEPORTTOWARPWITHADMISSION.content().replace("%price%", String.valueOf(price)).replace("%warp%", warpName).replace("%player%", Objects.requireNonNull(Bukkit.getOfflinePlayer(id).getName())));
                } else
                    p.sendMessage(Lang.TELEPORTTOWARP.content().replace("%warp%", warpName).replace("%player%", Objects.requireNonNull(Bukkit.getOfflinePlayer(id).getName())));
                if (!isOwner) {
                    warp.setVisits(warp.getVisits() + 1);
                    warp.setTodayVisits(warp.getTodayVisits() + 1);
                }
            }
        }
    }

    public void createWarp(Player p, String warpName){
        if (p.hasPermission("playerwarps.create") || p.hasPermission("playerwarps.admin")){
            if (!canHaveWarp(p)){
                p.sendMessage(Lang.LIMITREACHED.content().replace("%limit%", String.valueOf(getAmount(p, Lang.DEFAULTLIMITSIZE.getInt()))));
            } else {
                String worldName = p.getLocation().getWorld().getName();
                if (bannedWorlds.contains(worldName) && !p.hasPermission("playerwarps.admin")){
                    p.sendMessage(Lang.TRIEDTOCREATEPWARPINDISABLEDWORLD.content().replace("%world%", worldName));
                } else {
                    if (warpsHashMap.containsKey(warpName)) {
                        p.sendMessage(Lang.WARPALREADYCREATED.content());
                    } else {
                        int limit = Lang.WARPNAMEMAXLENGTH.getInt();
                        if (warpName.length() > limit) {
                            p.sendMessage(Lang.WARPNAMEISABOVELETTERLIMIT.content().replace("%limit%", String.valueOf(limit)));
                        } else {
                            if (warpName.contains(".") || warpName.contains(" ")) {
                                p.sendMessage(Lang.NAMECANTCONTAINSDOT.content());
                            } else {
                                if (econ != null) {
                                    int price = Lang.WARPPRICE.getInt();
                                    if (!econ.withdrawPlayer(p, price).transactionSuccess()) {
                                        p.sendMessage(Lang.INSUFFICIENTBALANCE.content());
                                        return;
                                    }
                                }
                                UUID ownerId = p.getUniqueId();
                                Location loc = p.getLocation();
                                String item = Lang.DEFAULTWARPITEM.content().toUpperCase();
                                warpsHashMap.put(warpName, new Warp(warpName, ownerId, loc, item, 0, new ArrayList<>(), 0, null, 0, null, false, false, System.currentTimeMillis(), 0));
                                if (econ != null) p.sendMessage(Lang.WARPCREATEDWITHPRICE.content().replace("%name%", warpName).replace("%price%", Lang.WARPPRICE.content()));
                                else p.sendMessage(Lang.WARPCREATED.content().replace("%name%", warpName));
                            }
                        }
                    }
                }
            }
        } else p.sendMessage(Lang.INSUFFICIENTPERMS.content());
    }

    public void removeWarp(Player p, String warpName){
        if (!warpsHashMap.containsKey(warpName)){
            p.sendMessage(Lang.NONEXISTINGWARP.content());
        } else {
            Warp warp = warpsHashMap.get(warpName);
            UUID id = p.getUniqueId();
            boolean isOwner = Objects.equals(warp.getOwner(), id);
            if (isOwner || p.hasPermission("playerwarps.remove.others") || p.hasPermission("playerwarps.admin")){
                add(Bukkit.getOfflinePlayer(warp.getOwner()), Lang.DELETEWARPREFUND.getInt());
                warpsHashMap.remove(warpName);
                if (econ != null){
                    p.sendMessage(Lang.WARPREMOVEDWITHREFUND.content().replace("%warp%", warpName).replace("%refund%", Lang.DELETEWARPREFUND.content()));
                } else p.sendMessage(Lang.WARPREMOVED.content().replace("%warp%", warpName));
            } else p.sendMessage(Lang.NOTOWNING.content());
        }
    }

    public void favorite(Player p, String warpName){
        PlayerConfig playerData = PlayerConfig.getConfig(p);
        List<String> favorites = playerData.getStringList("favorites");
        if (!favorites.contains(warpName)){
            favorites.add(warpName);
            playerData.set("favorites", favorites);
            playerData.save();
            p.sendMessage(Lang.ADDFAVORITE.content().replace("%warp%", warpName));
        } else p.sendMessage(Lang.ALREADYFAVORITE.content().replace("%warp%", warpName));
    }

    public void unfavored(Player p, String warp){
        PlayerConfig playerData = PlayerConfig.getConfig(p);
        List<String> favorites = playerData.getStringList("favorites");
        if (favorites.contains(warp)){
            favorites.remove(warp);
            playerData.set("favorites", favorites);
            playerData.save();
            p.sendMessage(Lang.REMOVEFAVORITE.content().replace("%warp%", warp));
        } else p.sendMessage(Lang.FAVNOTCONTAINS.content().replace("%warp%", warp));
    }

    public void review(Player p, String warpName, int stars) {
        if (checkWarp(warpName)) {
            p.sendMessage(Lang.INVALIDREVIEW.content());
        } else {
            Warp warp = warpsHashMap.get(warpName);
            UUID id = p.getUniqueId();
            if (stars <= 5 && stars >= 1) {
                if (warp.getReviewers().contains(id)) {
                    p.sendMessage(Lang.ALREADYREVIEWED.content());
                } else {
                    if (!warp.getOwner().equals(id)) {
                        warp.getReviewers().add(id);
                        warp.setRating(warp.getRating() + stars);
                        p.sendMessage(Lang.WARPREVIEWED.content().replace("%warp%", warpName).replace("%stars%", String.valueOf(stars)));
                    } else p.sendMessage(Lang.SELFREVIEW.content());
                }
            }
        }
    }

    public void setType(Player p, String warpName, String type){
        if (p.hasPermission("playerwarps.settype") || p.hasPermission("playerwarps.admin")){
            if (checkWarp(warpName)) {
                p.sendMessage(Lang.NONEXISTINGWARP.content());
            } else {
                Warp warp = warpsHashMap.get(warpName);
                UUID id = p.getUniqueId();
                if (Objects.equals(warp.getOwner(), id) || p.hasPermission("playerwarps.admin")) {
                    for (Category category : categories){
                        if (category.getType().equalsIgnoreCase(type)){
                            warp.setType(type);
                            p.sendMessage(Lang.WARPTYPECHANGED.content().replace("%warp%", warpName).replace("%type%", type));
                            return;
                        }
                    }
                    StringBuilder types = new StringBuilder();
                    for (Category category : categories){
                        types.append(category.getType()).append(" ");
                    }
                    p.sendMessage(Lang.ENTEREDINVALIDTYPE.content().replace("%types%", types.toString()));
                } else p.sendMessage(Lang.NOTOWNING.content());
            }
        } else {
            p.sendMessage(Lang.INSUFFICIENTPERMS.content());
        }
    }

    public void setItem(Player p, String warpName, String item, boolean open){
        if (p.hasPermission("playerwarps.setitem") || p.hasPermission("playerwarps.admin")){
            if (checkWarp(warpName)) {
                p.sendMessage(Lang.NONEXISTINGWARP.content());
            } else {
                Warp warp = warpsHashMap.get(warpName);
                UUID id = p.getUniqueId();
                if (p.hasPermission("playerwarps.admin") || Objects.equals(id, warp.getOwner())) {
                    String displayItem = item.toUpperCase();
                    if (Material.getMaterial(displayItem) != null) {
                        if (bannedItems.contains(Material.valueOf(displayItem))) {
                            p.sendMessage(Lang.TRIEDTOSETBANNEDITEM.content());
                        } else {
                            warp.setItem(displayItem);
                            p.sendMessage(Lang.ITEMCHANGED.content().replace("%item%", item));
                        }
                    } else p.sendMessage(Lang.INVALIDITEM.content());
                } else p.sendMessage(Lang.NOTOWNING.content());
            }
        } else p.sendMessage(Lang.INSUFFICIENTPERMS.content());
        if (open){playerWarps.getGuiManager().openSetUpMenu(p, warpName);}
    }

    public void setLore(Player p, String warpName, String msg, boolean open){
        boolean isAdmin = p.hasPermission("playerwarps.admin");
        if (p.hasPermission("playerwarps.lore") || isAdmin) {
            UUID id = p.getUniqueId();
            if (checkWarp(warpName)) {
                p.sendMessage(Lang.NONEXISTINGWARP.content());
            } else {
                Warp warp = warpsHashMap.get(warpName);
                if (Objects.equals(id, warp.getOwner()) || isAdmin) {
                    if (msg.equalsIgnoreCase("cancel")) {
                        p.sendMessage(Lang.TEXTWRITECANCELED.content());
                    } else {
                        if (msg.length() >= 5 && msg.length() <= 32) {
                            warp.setLore(msg);
                            p.sendMessage(Lang.TEXTCHANGED.content().replace("%warp%", warpName));
                        } else p.sendMessage(Lang.TEXTSIZEERROR.content());
                    }
                } else p.sendMessage(Lang.NOTOWNING.content());
            }
        } else p.sendMessage(Lang.INSUFFICIENTPERMS.content());
        if (open){playerWarps.getGuiManager().openSetUpMenu(p, warpName);}
    }

    public void setPrice(Player p, String warpName, String input, boolean open){
        if (p.hasPermission("playerwarps.setprice") || p.hasPermission("playerwarps.admin")){
            if (isInt(input)){
                if (checkWarp(warpName)){
                    p.sendMessage(Lang.NONEXISTINGWARP.content());
                } else {
                    Warp warp = warpsHashMap.get(warpName);
                    UUID id = p.getUniqueId();
                    if (Objects.equals(id, warp.getOwner()) || p.hasPermission("playerwarps.admin")){
                        int price = Integer.parseInt(input);
                        if (price >= 0){
                            if (price <= Lang.MAXWARPADMISSION.getInt()){
                                warp.setPrice(price);
                                p.sendMessage(Lang.PRICECHANGED.content().replace("%warp%", warpName).replace("%price%", price == 0 ? Lang.FREEOFCHARGE.content() : input));
                            } else p.sendMessage(Lang.ENTEREDHIGHERPRICETHATNALLOWED.content().replace("%max%", Lang.MAXWARPADMISSION.content()));
                        } else p.sendMessage(Lang.INVALIDENTEREDPRICE.content());
                    } else p.sendMessage(Lang.NOTOWNING.content());
                }
            } else p.sendMessage(Lang.NOTANUMBER.content().replace("%input%", input));
        } else p.sendMessage(Lang.INSUFFICIENTPERMS.content());
        if (open) playerWarps.getGuiManager().openSetUpMenu(p, warpName);
    }

    public void makePrivate(Player p, String warpName, boolean fromCommand){
        UUID id = p.getUniqueId();
        if (checkWarp(warpName)){
            p.sendMessage(Lang.NONEXISTINGWARP.content());
        } else {
            Warp warp = warpsHashMap.get(warpName);
            if (Objects.equals(id, warp.getOwner()) || p.hasPermission("playerwarps.admin")) {
                warp.setPrivateState(!warp.isPrivateState());
                if (fromCommand) p.sendMessage(Lang.PRIVACYCHANGED.content().replace("%warp%", warpName));
            } else p.sendMessage(Lang.NOTOWNING.content());
        }
    }

    public void transferOwnership(Player originalOwner, Player newOwner, String warpName, boolean open) {
        if (newOwner == null) {
            originalOwner.sendMessage(Lang.TRANSFERERROR.content());
        } else {
            boolean isAdmin = originalOwner.hasPermission("playerwarps.admin");
            if (originalOwner.hasPermission("playerwarps.transfer") || isAdmin){
                if (checkWarp(warpName)){
                    originalOwner.sendMessage(Lang.NONEXISTINGWARP.content());
                } else {
                    Warp warp = warpsHashMap.get(warpName);
                    UUID id = originalOwner.getUniqueId();
                    if (Objects.equals(id, warp.getOwner()) || isAdmin){
                        if (Objects.equals(newOwner, originalOwner)) {
                            originalOwner.sendMessage(Lang.TRANSFERERROR.content());
                        } else {
                            if (newOwner.hasPlayedBefore() || newOwner.isOnline()) {
                                if (Objects.equals(originalOwner.getUniqueId(), warp.getOwner())) {
                                    if (canHaveWarp(newOwner)) {
                                        warp.setOwner(newOwner.getUniqueId());
                                        originalOwner.sendMessage(Lang.TRANSFERSUCCESFUL.content().replace("%player%", newOwner.getName()).replace("%warp%", warpName));
                                        if (newOwner.isOnline()) newOwner.sendMessage(Lang.TRANSFERINFO.content().replace("%player%", originalOwner.getName()).replace("%warp%", warpName));
                                    }
                                }
                            }
                        }
                    } else originalOwner.sendMessage(Lang.NOTOWNING.content());
                }
            } else originalOwner.sendMessage(Lang.INSUFFICIENTPERMS.content());
        }
        if (open) playerWarps.getGuiManager().openSetUpMenu(originalOwner, warpName);
    }

    public void rename(Player p, String warpName, String newWarpName, boolean open){
        //String warpNameTemp = warpName;
        boolean isAdmin = p.hasPermission("playerwarps.admin");
        if (p.hasPermission("playerwarps.rename") || isAdmin){
            if (checkWarp(warpName)){
                p.sendMessage(Lang.NONEXISTINGWARP.content());
            } else {
                if (warpsHashMap.containsKey(newWarpName)){
                    p.sendMessage(Lang.WARPALREADYCREATED.content());
                } else {
                    int limit = Lang.WARPNAMEMAXLENGTH.getInt();
                    if (newWarpName.length() > limit) {
                        p.sendMessage(Lang.WARPNAMEISABOVELETTERLIMIT.content().replace("%limit%", String.valueOf(limit)));
                    } else {
                        if (newWarpName.contains(".") || newWarpName.contains(" ")) {
                            p.sendMessage(Lang.NAMECANTCONTAINSDOT.content());
                        } else {
                            final Warp warp = warpsHashMap.get(warpName);
                            final UUID id = p.getUniqueId();
                            if (Objects.equals(id, warp.getOwner()) || isAdmin) {
                                warpsHashMap.put(newWarpName, warpsHashMap.remove(warpName));
                                warp.setName(newWarpName);
                                p.sendMessage(Lang.WARPRENAMED.content().replace("%oldName%", warpName).replace("%newName%", newWarpName));
                            } else p.sendMessage(Lang.NOTOWNING.content());
                        }
                    }
                }
            }
        } else p.sendMessage(Lang.INSUFFICIENTPERMS.content());
        if (open) playerWarps.getGuiManager().openSetUpMenu(p, newWarpName);
    }

    public void teleportPlayer(Player p, Location loc, boolean cooldown){
        if (!cooldown){
            tp.put(p, p.getLocation().getBlockX() + p.getLocation().getBlockZ());
            p.sendMessage(Lang.TELEPORTATION.content().replace("%time%", Lang.TELEPORATIONDELAY.content()));

            new BukkitRunnable(){

                int cycle = 0;
                @Override
                public void run() {
                    if (!p.isOnline()) cancel();
                    else {
                        if (tp.get(p) != (p.getLocation().getBlockX() + p.getLocation().getBlockZ())) {
                            p.sendMessage(Lang.TELEPORTATIONCANCELLED.content());
                            cancel();
                        } else {
                            if (cycle == Lang.TELEPORATIONDELAY.getInt() * 2) {
                                cancel();
                                p.teleport(loc);
                            }
                        }
                        ++cycle;
                    }
                }}.runTaskTimer(playerWarps, 0, 10);
        } else p.teleport(loc);
    }

    public void disable(Player p, String warpName) {
        if (p.hasPermission("playerwarps.freeze") || p.hasPermission("playerwarps.admin")) {
            if (checkWarp(warpName)) {
                p.sendMessage(Lang.NONEXISTINGWARP.content());
            } else {
                Warp warp = warpsHashMap.get(warpName);
                UUID id = p.getUniqueId();
                if (Objects.equals(id, warp.getOwner()) || p.hasPermission("playerwarps.admin")) {
                    if (!warp.isDisabled()) {
                        warp.setDisabled(true);
                        p.sendMessage(Lang.WARPDISABLED.content().replace("%warp%", warpName));
                    } else {
                        warp.setDisabled(false);
                        p.sendMessage(Lang.WARPENABLED.content().replace("%warp%", warpName));
                    }
                } else p.sendMessage(Lang.NOTOWNING.content());
            }
        } else p.sendMessage(Lang.INSUFFICIENTPERMS.content());
    }

    public void loadWarps(){
        if (data.isConfigurationSection("warps")) {
            Set<String> warps = Objects.requireNonNull(data.getConfigurationSection("warps")).getKeys(false);
            for (String warp : warps) {
                String path = "warps." + warp;
                UUID owner = UUID.fromString(Objects.requireNonNull(data.getString(path + ".owner-id")));
                Location loc = (Location) data.get(path + ".loc");
                String item = data.getString(path + ".item");
                int rating = data.getInt(path + ".ratings");
                List<UUID> reviewers = new ArrayList<>();
                for (String id : data.getStringList(path + ".reviewers")){
                    reviewers.add(UUID.fromString(id));
                }
                int visits = data.getInt(path + ".visits");
                String type = data.getString(path + ".type");
                int price = data.getInt(path + ".price");
                boolean disabled = data.getBoolean(path + ".disabled");
                boolean privateState = data.getBoolean(path + ".private");
                String lore = data.getString(path + ".lore");
                long dateCreated = data.getLong(path + ".date-created");
                if (dateCreated == 0) dateCreated = System.currentTimeMillis();
                warpsHashMap.put(warp, new Warp(warp, owner, loc, item, rating, reviewers, visits, type, price, lore, disabled, privateState, dateCreated, 0));
            }
        }
    }

    public void reloadWarps(Player sender){
        if (!sender.hasPermission("playerwarps.admin")) {
            sender.sendMessage(Lang.INSUFFICIENTPERMS.content());
        } else {
            playerWarps.reloadConfig();
            File configFile = new File(playerWarps.getDataFolder(), "config.yml");

            try {
                ConfigUpdater.update(playerWarps, "config.yml", configFile, Collections.emptyList());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            loadCategories();
            Lang.reload();
            sender.sendMessage(Lang.RELOAD.content());
        }
    }

    public void loadCategories() {
        categoriesFile = new File(playerWarps.getDataFolder(), "categories.yml");
        categoriesData = YamlConfiguration.loadConfiguration(categoriesFile);

        reloadData();
        if (!categoriesFile.exists()){
            getData().options().copyDefaults(true);
        }
        saveData();

        List<Category> categories = new ArrayList<>();
        for (String category : Objects.requireNonNull(categoriesData.getConfigurationSection("categories")).getKeys(false)){
            String path = "categories." + category;
            String name = Lang.applyColor(categoriesData.getString(path + ".name"));
            int pos = categoriesData.getInt(path + ".position");
            String material = categoriesData.getString(path + ".item");
            List<String> lore = new ArrayList<>();
            for (String line : categoriesData.getStringList(path + ".lore")){
                lore.add(Lang.applyColor(line));
            }
            categories.add(new Category(category, name, material, pos, lore));
        }
        this.categories = categories;
    }

    public void saveWarps(){
        data.set("warps", null);
        int privateWarps = 0;
        for (Map.Entry<String, Warp> warpMap : warpsHashMap.entrySet()){
            String path = "warps." + warpMap.getKey();
            Warp warp = warpMap.getValue();
            data.set(path + ".owner-id", warp.getOwner().toString());
            data.set(path + ".loc", warp.getLoc());
            data.set(path + ".item", warp.getItem());
            data.set(path + ".ratings", warp.getRating());
            List<String> reviewers = new ArrayList<>();
            for (UUID id : warp.getReviewers()){
                reviewers.add(id.toString());
            }
            data.set(path + ".reviewers", reviewers);
            data.set(path + ".visits", warp.getVisits());
            data.set(path + ".private", warp.isPrivateState());
            data.set(path + ".type", warp.getType());
            data.set(path + ".disabled", warp.isDisabled());
            data.set(path + ".price", warp.getPrice());
            data.set(path + ".lore", warp.getLore());
            data.set(path + ".date-created", warp.getDateCreated());
            if (warp.isPrivateState()) ++privateWarps;
        }
        dataManager.saveData();
        if (Lang.AUTOSAVEANNOUNCE.getBoolean()) {
            Bukkit.getLogger().info("Saving " + warpsHashMap.size() + " warps (including " + privateWarps + " private warps)");
        }
    }

    private int getAmount(Player player, int defaultValue) {
        String permissionPrefix = "playerwarps.limit.";

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getPermission().startsWith(permissionPrefix)) {
                return Integer.parseInt(attachmentInfo.getPermission().substring(attachmentInfo.getPermission().lastIndexOf(".") + 1));
            }
        }

        return defaultValue;
    }

    private boolean canHaveWarp(Player p){
        UUID id = p.getUniqueId();
        if (!p.hasPermission("playerwarps.limit.unlimited")) {
            return getAmount(p, Lang.DEFAULTLIMITSIZE.getInt()) != getOwnedWarps(id);
        }
        return true;
    }

    private int getOwnedWarps(UUID id){
        int owned = 0;
        if (isWarps()){
            for (Warp warp : warpsHashMap.values()){
                if (Objects.equals(id, warp.getOwner())){
                    ++owned;
                }
            }
        }
        return owned;
    }

    public int getWarpOfType(String type) {
        int number = 0;
        if (type.equalsIgnoreCase("all")){
            for (Warp warp : warpsHashMap.values()){
                if (warp.isPrivateState()) continue;
                ++number;
            }
        } else {
            for (Warp warp : warpsHashMap.values()){
                String warpType = warp.getType();
                if (warpType == null) continue;
                if (warp.isPrivateState()) continue;
                if (warp.getType().equalsIgnoreCase(type)){
                    ++number;
                }
            }
        }
        return number;
    }

    public boolean isInt(String str) {
        try {
            Integer.parseInt(str);
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    private void add(OfflinePlayer p, int money){
        if (money > 0) {
            if (econ != null) {
                econ.depositPlayer(p, money);
            }
        }
    }

    public void saveData() {
        if (this.categoriesData == null || this.categoriesFile == null) {
            return;
        }
        try {
            this.getData().save(this.categoriesFile);
        }
        catch (IOException thrown) {
            playerWarps.getLogger().log(Level.SEVERE, "Could not save config to " + this.categoriesFile, thrown);
        }
    }

    public FileConfiguration getData() {
        if (this.categoriesData == null) {
            this.reloadData();
        }
        return this.categoriesData;
    }

    public void reloadData() {
        if (this.categoriesFile == null) {
            this.categoriesFile = new File(playerWarps.getDataFolder(), "categories.yml");
        }
        this.categoriesData = YamlConfiguration.loadConfiguration(this.categoriesFile);
        final InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(playerWarps.getResource("categories.yml")), StandardCharsets.UTF_8);
        this.categoriesData.setDefaults(YamlConfiguration.loadConfiguration(inputStreamReader));
    }

    public boolean isOwner(UUID id, Warp warp){return (warp.getOwner().equals(id));}

    public boolean isWarps() {return !warpsHashMap.isEmpty();}

    public boolean checkWarp(String warpName){return !warpsHashMap.containsKey(warpName);}

    public HashMap<String, Warp> getWarpList(){return warpsHashMap;}

    public List<Category> getCategories(){return this.categories;}
}