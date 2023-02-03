package cz.revivalo.playerwarps.warp;

import com.tchristofferson.configupdater.ConfigUpdater;
import cz.revivalo.playerwarps.PlayerWarps;
import cz.revivalo.playerwarps.categories.Category;
import cz.revivalo.playerwarps.categories.CategoryManager;
import cz.revivalo.playerwarps.configuration.enums.Config;
import cz.revivalo.playerwarps.configuration.enums.Lang;
import cz.revivalo.playerwarps.datamanager.DataManager;
import cz.revivalo.playerwarps.playerconfig.PlayerConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WarpHandler {

    private final PlayerWarps playerWarps;
    private final Economy econ;
    private final DataManager dataManager;
    private final YamlConfiguration data;
    private final HashMap<UUID, Warp> warpsHashMap;
    private final HashMap<Player, Integer> tp = new HashMap<>();
    private final List<Material> bannedItems;
    private final List<String> bannedWorlds;
    public  List<Player> openedFromCommand = new ArrayList<>();

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
        for (String item : Config.BANNED_ITEMS.asReplacedList(Collections.emptyMap())) {
            bannedItems.add(Material.valueOf(item.toUpperCase()));
        }

        bannedWorlds = new ArrayList<>();
        bannedWorlds.addAll(Config.BANNED_WORLDS.asReplacedList(Collections.emptyMap()));
    }

    public void warp(Player player, Warp warp){
        if (warp == null) {
            player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
        } else {
            final UUID ownerID = warp.getOwner();
            final String warpName = warp.getName();
            boolean isOwner = Objects.equals(player.getUniqueId(), ownerID);
            if (warp.isDisabled() && (!player.hasPermission("playerwarps.admin") || !isOwner)) {
                player.sendMessage(Lang.WARP_IS_DISABLED.asColoredString().replace("%warp%", warpName));
            } else {
                int price = warp.getPrice();
                Location loc = warp.getLocation();
                boolean hasBypass = player.hasPermission("playerwarps.delay.bypass");
                if (!isOwner) {
                    if (econ == null) teleportPlayer(player, loc, hasBypass);
                    else {
                        if (price == 0) {
                            teleportPlayer(player, loc, hasBypass);
                        } else {
                            if (econ.withdrawPlayer(player, price).transactionSuccess()) {
                                add(Bukkit.getOfflinePlayer(ownerID), price);
                                teleportPlayer(player, loc, hasBypass);
                            } else {
                                player.sendMessage(Lang.INSUFFICIENT_BALANCE_TO_TELEPORT.asColoredString().replace("%warp%", warpName));
                                return;
                            }
                        }
                    }
                } else teleportPlayer(player, loc, hasBypass);
                if (price != 0 && !isOwner) {
                    player.sendMessage(Lang.TELEPORT_TO_WARP_WITH_ADMISSION.asColoredString()
                            .replace("%price%", String.valueOf(price))
                            .replace("%warp%", warpName)
                            .replace("%player%", Objects.requireNonNull(Bukkit.getOfflinePlayer(ownerID).getName())));
                } else
                    player.sendMessage(Lang.TELEPORT_TO_WARP.asColoredString()
                            .replace("%warp%", warpName)
                            .replace("%player%", Objects.requireNonNull(Bukkit.getOfflinePlayer(ownerID).getName())));
                if (!isOwner) {
                    warp.setVisits(warp.getVisits() + 1);
                    warp.setTodayVisits(warp.getTodayVisits() + 1);
                }
            }
        }
    }

    public void warp(Player player, String warpName) {
        warp(player, getWarpFromName(warpName));
    }

    public void warp(Player player, UUID warpID){
        warp(player, warpsHashMap.get(warpID));
    }

    public String createWarp(final Player player, String warpName){
        if (!hasPermission(player, "playerwarps.create", "playerwarps.admin")) {
            return Lang.INSUFFICIENT_PERMS.asColoredString();
        }
        if (!canHaveWarp(player)){
            return Lang.LIMIT_REACHED.asColoredString().replace("%limit%", String.valueOf(getAmount(player, Config.DEFAULT_LIMIT_SIZE.asInt())));
        }
        final String worldName = Objects.requireNonNull(player.getLocation().getWorld()).getName();
        if (bannedWorlds.contains(worldName) && hasPermission(player, "playerwarps.admin")){
            return Lang.TRIED_TO_CREATE_PWARP_IN_DISABLED_WORLD.asColoredString().replace("%world%", worldName);
        }

        if (warpsHashMap.values().stream().anyMatch(w -> w.getName().equalsIgnoreCase(warpName))) {
            final Warp warp = warpsHashMap.values().stream().filter(w -> w.getName().equalsIgnoreCase(warpName)).collect(Collectors.toList()).get(0);
            if (warp != null) {
                return Lang.WARP_ALREADY_CREATED.asColoredString();
            }
        }

        int limit = Config.WARP_NAME_MAX_LENGTH.asInt();
        if (warpName.length() > limit) {
            return Lang.WARP_NAME_IS_ABOVE_LETTER_LIMIT.asColoredString().replace("%limit%", String.valueOf(limit));
        }
        if (/*warpName.contains(".") ||*/ warpName.contains(" ")) {
            player.sendMessage(Lang.NAME_CANT_CONTAINS_DOT.asColoredString());
        }
        if (econ != null) {
            int price = Config.WARP_PRICE.asInt();
                if (!econ.withdrawPlayer(player, price).transactionSuccess()) {
                    return Lang.INSUFFICIENT_BALANCE.asColoredString();
                }
            }
        final UUID ownerID = player.getUniqueId();
        final Location loc = player.getLocation();
        //String item = Config.DEFAULT_WARP_ITEM.getString().toUpperCase();
        final UUID warpID = UUID.randomUUID();
        warpsHashMap.put(warpID, new Warp(
                new HashMap<String, Object>(){{
                    put("uuid", warpID.toString());
                    put("name", warpName);
                    put("owner-id", ownerID.toString());
                    put("loc", loc);
                    put("ratings", 0);
                    put("visits", 0);
                    put("category", "all");
                    put("description", null);
                    put("price", 0);
                    put("reviewers", Collections.emptyList());
                    put("todayVisits", 0);
                    put("date-created", System.currentTimeMillis());
                    put("item", Config.DEFAULT_WARP_ITEM.asAnItem().getType().name());
                    put("disabled", false);
                    put("private", false);
                }}

        ));
        if (econ != null)
            return Lang.WARP_CREATED_WITH_PRICE.asColoredString()
                    .replace("%name%", warpName)
                    .replace("%price%", Config.WARP_PRICE.asString());
        else return Lang.WARP_CREATED.asColoredString().replace("%name%", warpName);
    }

    public void removeWarp(Player player, Warp warp){
        if (!warpsHashMap.containsKey(warp.getWarpID())){
            player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
        }
        final UUID id = player.getUniqueId();
        boolean isOwner = Objects.equals(warp.getOwner(), id);
        if (isOwner || hasPermission(player, "playerwarps.remove.others", "playerwarps.admin")){
            add(Bukkit.getOfflinePlayer(warp.getOwner()), Config.DELETE_WARP_REFUND.asInt());
            warpsHashMap.remove(warp.getWarpID());
            if (econ != null){
                player.sendMessage(Lang.WARP_REMOVED_WITH_REFUND.asColoredString().replace("%warp%", warp.getName()).replace("%refund%", Config.DELETE_WARP_REFUND.asString()));
            } else player.sendMessage(Lang.WARP_REMOVED.asColoredString().replace("%warp%", warp.getName()));
        } else player.sendMessage(Lang.NOT_OWNING.asColoredString());
    }

    public void favorite(Player player, Warp warp){
        final PlayerConfig playerData = PlayerConfig.getConfig(player);
        final List<String> favorites = playerData.getStringList("favorites");
        if (!favorites.contains(warp.toString())){
            favorites.add(warp.toString());
            playerData.set("favorites", favorites);
            playerData.save();
            player.sendMessage(Lang.ADD_FAVORITE.asColoredString().replace("%warp%", warp.getName()));
        } else player.sendMessage(Lang.ALREADY_FAVORITE.asColoredString().replace("%warp%", warp.getName()));
    }

    public void unfavored(Player player, Warp warp){
        PlayerConfig playerData = PlayerConfig.getConfig(player);
        List<String> favorites = playerData.getStringList("favorites");
        if (favorites.contains(warp.toString())){
            favorites.remove(warp.toString());
            playerData.set("favorites", favorites);
            playerData.save();
            player.sendMessage(Lang.REMOVE_FAVORITE.asColoredString().replace("%warp%", warp.getName()));
        } else player.sendMessage(Lang.FAV_NOT_CONTAINS.asColoredString().replace("%warp%", warp.getName()));
    }

    public void review(Player player, Warp warp, int stars) {
        if (checkWarp(warp)) {
            player.sendMessage(Lang.INVALID_REVIEW.asColoredString());
            return;
        }

        final UUID id = player.getUniqueId();
        if (stars <= 5 && stars >= 1) {
            if (Objects.equals(id, warp.getOwner())) {
                player.sendMessage(Lang.SELF_REVIEW.asColoredString());
                return;
            }
            if (warp.getReviewers().contains(id)) {
                player.sendMessage(Lang.ALREADY_REVIEWED.asColoredString());
                return;
            }
            warp.getReviewers().add(id);
            warp.setRating(warp.getRating() + stars);
            warp.setStars(Config.createRatingFormat(warp));
            player.sendMessage(Lang.WARP_REVIEWED.asColoredString().
                    replace("%warp%", warp.getName()).
                    replace("%stars%", String.valueOf(stars)));
        }
    }

    public void setType(Player player, Warp warp, String type){
        if (hasPermission(player, "playerwarps.settype", "playerwarps.admin")){
            if (checkWarp(warp)) {
                player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
            } else {
                //Warp warp = warpsHashMap.get(warpName);
                final UUID id = player.getUniqueId();
                if (Objects.equals(warp.getOwner(), id) || player.hasPermission("playerwarps.admin")) {
                    for (Category category : CategoryManager.getCategories()){
                        if (category.getType().equalsIgnoreCase(type)){
                            warp.setCategory(category);
                            player.sendMessage(Lang.WARP_TYPE_CHANGED.asColoredString().
                                    replace("%warp%", warp.getName()).
                                    replace("%type%", type));
                            return;
                        }
                    }
                    StringBuilder types = new StringBuilder();
                    for (Category category : CategoryManager.getCategories()){
                        types.append(category.getType()).append(" ");
                    }
                    player.sendMessage(Lang.ENTERED_INVALID_TYPE.asColoredString().replace("%types%", types.toString()));
                } else Lang.sendListToPlayer(player, Lang.NOT_OWNING.asReplacedList(Collections.emptyMap()));
            }
        } else {
            player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
        }
    }

    public void setItem(Player player, Warp warp, String item, boolean open){
        if (hasPermission(player, "playerwarps.setitem", "playerwarps.admin")){
            if (checkWarp(warp)) {
                player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
            } else {
                //Warp warp = warpsHashMap.get(warpName);
                UUID id = player.getUniqueId();
                if (player.hasPermission("playerwarps.admin") || Objects.equals(id, warp.getOwner())) {
                    try {
                    ItemStack displayItem = new ItemStack(Material.valueOf(item.toUpperCase()));
                    //if (Material.getMaterial(displayItem) != null) {
                        if (bannedItems.contains(displayItem.getType())) {
                            player.sendMessage(Lang.TRIED_TO_SET_BANNED_ITEM.asColoredString());
                        } else {
                            warp.setMenuItem(displayItem);
                            player.sendMessage(Lang.ITEM_CHANGED.asColoredString().replace("%item%", item));
                        }
                    //} else player.sendMessage(Lang.INVALID_ITEM.asColoredString());
                    } catch (IllegalArgumentException exception){
                        player.sendMessage(Lang.INVALID_ITEM.asColoredString());
                    }
                } else player.sendMessage(Lang.NOT_OWNING.asColoredString());
            }
        } else player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
        if (open){playerWarps.getGuiManager().openSetUpMenu(player, warp);}
    }

    public void setDescription(Player player, Warp warp, String msg, boolean open){
        boolean isAdmin = player.hasPermission("playerwarps.admin");
        if (player.hasPermission("playerwarps.lore") || isAdmin) {
            UUID id = player.getUniqueId();
            if (checkWarp(warp)) {
                player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
            } else {
                //Warp warp = warpsHashMap.get(warpName);
                if (Objects.equals(id, warp.getOwner()) || isAdmin) {
                    if (msg.equalsIgnoreCase("cancel")) {
                        player.sendMessage(Lang.TEXT_WRITE_CANCELED.asColoredString());
                    } else {
                        if (msg.length() >= 5 && msg.length() <= 32) {
                            warp.setDescription(msg);
                            player.sendMessage(Lang.TEXT_CHANGED.asColoredString().replace("%warp%", warp.getName()));
                        } else player.sendMessage(Lang.TEXT_SIZE_ERROR.asColoredString());
                    }
                } else player.sendMessage(Lang.NOT_OWNING.asColoredString());
            }
        } else player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
        if (open) {playerWarps.getGuiManager().openSetUpMenu(player, warp);}
    }

    public void setAdmission(Player player, Warp warp, String input, boolean executedFromGui){
        if (player.hasPermission("playerwarps.setprice") || player.hasPermission("playerwarps.admin")){
            if (isInt(input)){
                if (checkWarp(warp)){
                    player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
                } else {
                    //Warp warp = warpsHashMap.get(warpName);
                    UUID id = player.getUniqueId();
                    if (Objects.equals(id, warp.getOwner()) || player.hasPermission("playerwarps.admin")){
                        int price = Integer.parseInt(input);
                        if (price >= 0){
                            if (price <= Config.MAX_WARP_ADMISSION.asInt()){
                                warp.setPrice(price);
                                player.sendMessage(Lang.PRICE_CHANGED.asColoredString()
                                        .replace("%warp%", warp.getName())
                                        .replace("%price%", price == 0
                                                ? Lang.FREE_OF_CHARGE.asColoredString()
                                                : input));
                            } else player.sendMessage(Lang.ENTERED_HIGHER_PRICE_THAN_ALLOWED.asColoredString().replace("%max%", Config.MAX_WARP_ADMISSION.asString()));
                        } else player.sendMessage(Lang.INVALID_ENTERED_PRICE.asColoredString());
                    } else player.sendMessage(Lang.NOT_OWNING.asColoredString());
                }
            } else player.sendMessage(Lang.NOT_A_NUMBER.asColoredString().replace("%input%", input));
        } else player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
        if (executedFromGui) playerWarps.getGuiManager().openSetUpMenu(player, warp);
    }

    public void makePrivate(Player player, Warp warp, boolean fromCommand){
        UUID id = player.getUniqueId();
        if (checkWarp(warp)){
            player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
        } else {
            if (Objects.equals(id, warp.getOwner()) || player.hasPermission("playerwarps.admin")) {
                warp.setPrivateState(!warp.isPrivateState());
                if (fromCommand) player.sendMessage(Lang.PRIVACY_CHANGED.asColoredString().replace("%warp%", warp.getName()));
            } else player.sendMessage(Lang.NOT_OWNING.asColoredString());
        }
    }

    public void transferOwnership(Player originalOwner, Player newOwner, Warp warp, boolean open) {
        if (newOwner == null) {
            originalOwner.sendMessage(Lang.TRANSFER_ERROR.asColoredString());
        } else {
            boolean isAdmin = originalOwner.hasPermission("playerwarps.admin");
            if (originalOwner.hasPermission("playerwarps.transfer") || isAdmin){
                if (checkWarp(warp)){
                    originalOwner.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
                } else {
                    final UUID id = originalOwner.getUniqueId();
                    if (Objects.equals(id, warp.getOwner()) || isAdmin){
                        if (Objects.equals(newOwner, originalOwner)) {
                            originalOwner.sendMessage(Lang.TRANSFER_ERROR.asColoredString());
                        } else {
                            if (newOwner.hasPlayedBefore() || newOwner.isOnline()) {
                                if (Objects.equals(originalOwner.getUniqueId(), warp.getOwner())) {
                                    if (canHaveWarp(newOwner)) {
                                        warp.setOwner(newOwner.getUniqueId());
                                        originalOwner.sendMessage(Lang.TRANSFER_SUCCESSFUL.asColoredString()
                                                .replace("%player%", newOwner.getName())
                                                .replace("%warp%", warp.getName()));
                                        if (newOwner.isOnline()) newOwner.sendMessage(Lang.TRANSFER_INFO.asColoredString()
                                                .replace("%player%", originalOwner.getName())
                                                .replace("%warp%", warp.getName()));
                                    }
                                }
                            }
                        }
                    } else originalOwner.sendMessage(Lang.NOT_OWNING.asColoredString());
                }
            } else originalOwner.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
        }
        if (open) playerWarps.getGuiManager().openSetUpMenu(originalOwner, warp);
    }

    public void rename(final Player player, Warp warp, String newWarpName, boolean open){
        boolean isAdmin = player.hasPermission("playerwarps.admin");
        if (player.hasPermission("playerwarps.rename") || isAdmin){
            if (checkWarp(warp)){
                player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
            } else {
                /*if (warpsHashMap.containsKey(newWarpName)){
                    player.sendMessage(Lang.WARP_ALREADY_CREATED.asColoredString());
                } else {*/
                    int limit = Config.WARP_NAME_MAX_LENGTH.asInt();
                    if (newWarpName.length() > limit) {
                        player.sendMessage(Lang.WARP_NAME_IS_ABOVE_LETTER_LIMIT.asColoredString().replace("%limit%", String.valueOf(limit)));
                    } else {
                        if (/*newWarpName.contains(".") ||*/ newWarpName.contains(" ")) {
                            player.sendMessage(Lang.NAME_CANT_CONTAINS_DOT.asColoredString());
                        } else {
                            final UUID id = player.getUniqueId();
                            if (Objects.equals(id, warp.getOwner()) || isAdmin) {
                                player.sendMessage(Lang.WARP_RENAMED.asColoredString().replace("%oldName%", warp.getName()).replace("%newName%", newWarpName));
                                warp.setName(newWarpName);
                            } else player.sendMessage(Lang.NOT_OWNING.asColoredString());
                        }
                    }
                //}
            }
        } else player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
        if (open) playerWarps.getGuiManager().openSetUpMenu(player, warp);
    }

    public void teleportPlayer(Player player, Location loc, boolean cooldown){
        if (!cooldown){
            tp.put(player, player.getLocation().getBlockX() + player.getLocation().getBlockZ());
            player.sendMessage(Lang.TELEPORTATION.asColoredString().replace("%time%", Config.TELEPORTATION_DELAY.asString()));

            new BukkitRunnable(){

                int cycle = 0;
                @Override
                public void run() {
                    if (!player.isOnline()) cancel();
                    else {
                        if (tp.get(player) != (player.getLocation().getBlockX() + player.getLocation().getBlockZ())) {
                            player.sendMessage(Lang.TELEPORTATION_CANCELLED.asColoredString());
                            cancel();
                        } else {
                            if (cycle == Config.TELEPORTATION_DELAY.asInt() * 2) {
                                cancel();
                                player.teleport(loc);
                            }
                        }
                        ++cycle;
                    }
                }}.runTaskTimer(playerWarps, 0, 10);
        } else player.teleport(loc);
    }

    public void disable(Player player, Warp warp, boolean executedFromCommand) {
        if (player.hasPermission("playerwarps.freeze") || player.hasPermission("playerwarps.admin")) {
            if (checkWarp(warp)) {
                player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
            } else {
                final UUID id = player.getUniqueId();
                if (Objects.equals(id, warp.getOwner()) || player.hasPermission("playerwarps.admin")) {
                    if (!warp.isDisabled()) {
                        warp.setDisabled(true);
                        if (executedFromCommand) player.sendMessage(Lang.WARP_DISABLED.asColoredString().replace("%warp%", warp.getName()));
                    } else {
                        warp.setDisabled(false);
                        if (executedFromCommand) player.sendMessage(Lang.WARP_ENABLED.asColoredString().replace("%warp%", warp.getName()));
                    }
                } else player.sendMessage(Lang.NOT_OWNING.asColoredString());
            }
        } else player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
    }

    public void reloadWarps(Player sender){
        if (!sender.hasPermission("playerwarps.admin")) {
            sender.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
        } else {
            playerWarps.reloadConfig();
            File configFile = new File(playerWarps.getDataFolder(), "config.yml");

            try {
                ConfigUpdater.update(playerWarps, "config.yml", configFile, Collections.emptyList());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            CategoryManager.loadCategories();
            Lang.reload();
            sender.sendMessage(Lang.RELOAD.asColoredString());
        }
    }

    public void loadWarps(){
        Optional<ConfigurationSection> warpDataSection = Optional.ofNullable(data.getConfigurationSection("warps"));
        warpDataSection.ifPresent(warpSection ->
                warpSection
                        .getKeys(false)
                        .forEach(warpID -> addWarp(warpSection.getSerializable(warpID, Warp.class))));
//        //if (data.isConfigurationSection("warps")) {
//            final ConfigurationSection warpSection = data.getConfigurationSection("warps");
//            for (String warpID : warpSection.getKeys(false)) {
//                warpsHashMap.put(UUID.fromString(warpID), warpSection.getSerializable(warpID, Warp.class));
//            }
//        //}
    }

    public void saveWarps(){
        final ConfigurationSection warpsSection = data.createSection("warps");
        int privateWarps = 0;
        for (Warp warp : warpsHashMap.values()){
            warpsSection.set(warp.getWarpID().toString(), warp);
            if (warp.isPrivateState()) ++privateWarps;
        }

        dataManager.saveData();
        if (Config.AUTO_SAVE_ANNOUNCE.asBoolean()) {
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

    private boolean canHaveWarp(final Player player){
        UUID id = player.getUniqueId();
        if (!player.hasPermission("playerwarps.limit.unlimited")) {
            return getAmount(player, Config.DEFAULT_LIMIT_SIZE.asInt()) != getOwnedWarps(id);
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
                if (warp.getCategory() == null) continue;
                String warpType = warp.getCategory().getType();
                if (warpType == null) continue;
                if (warp.isPrivateState()) continue;
                if (warpType.equalsIgnoreCase(type)){
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

    private void add(OfflinePlayer offlinePlayer, int money){
        if (money > 0) {
            if (econ != null) {
                econ.depositPlayer(offlinePlayer, money);
            }
        }
    }

    private boolean hasPermission(Player player, String... permissions){
        for (String permission : permissions){
            if (player.hasPermission(permission)) return true;
        }
        return false;
    }

    public Collection<Warp> getWarps(){
        return warpsHashMap.values();
    }

    private void addWarp(Warp warp){
        warpsHashMap.put(warp.getWarpID(), warp);
    }

    public boolean isOwner(UUID id, Warp warp){return (warp.getOwner().equals(id));}

    public boolean isWarps() {return !warpsHashMap.isEmpty();}

    public Warp getWarpByID(final UUID warpID){return warpsHashMap.get(warpID);}
    public Warp getWarpByID(final String warpID){
        return getWarpByID(UUID.fromString(warpID));
    }
    public Warp getWarpFromName(final String warpName){
        try {
            return warpsHashMap.values().stream().filter(a -> a.getName().equalsIgnoreCase(warpName)).collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException ex){
            return null;
        }
    }
    public boolean checkWarp(Warp warp){return warp == null;}

    public HashMap<UUID, Warp> getWarpList(){return warpsHashMap;}
}