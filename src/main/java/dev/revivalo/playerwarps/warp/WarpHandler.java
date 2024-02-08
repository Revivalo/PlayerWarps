package dev.revivalo.playerwarps.warp;

import com.tchristofferson.configupdater.ConfigUpdater;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.categories.CategoryManager;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.hooks.Hooks;
import dev.revivalo.playerwarps.playerconfig.PlayerConfig;
import dev.revivalo.playerwarps.user.UserManager;
import dev.revivalo.playerwarps.user.WarpAction;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.utils.PlayerUtils;
import dev.revivalo.playerwarps.utils.TextUtils;
import io.github.rapha149.signgui.SignGUI;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WarpHandler {

    private final Set<Warp> warps;
    private final HashMap<Player, Integer> tp = new HashMap<>();
    private final List<String> bannedWorlds;

    public WarpHandler() {
        warps = new HashSet<>();

        bannedWorlds = new ArrayList<>();
        bannedWorlds.addAll(Config.BANNED_WORLDS.asReplacedList(Collections.emptyMap()));
    }

    public void preWarp(Player player, Warp warp) {
        if (warp.isPasswordProtected()) {
            SignGUI gui = SignGUI.builder()
                    .setType(Material.OAK_SIGN)
                    .setColor(DyeColor.BLACK)
                    .setLine(1, Lang.ENTER_PASSWORD.asColoredString())
                    .setHandler((p, result) -> {
                        String input = result.getLineWithoutColor(0);

                        if (input.isEmpty()) {
                            return Collections.emptyList();
                        }

                        if (input.length() < 3 || input.length() > 15) {
                            return Collections.emptyList();
                        }


                        PlayerWarpsPlugin.get().runDelayed(() -> warp(player, warp, input), 2);

                        return Collections.emptyList();
                    })

                    .build();

            gui.open(player);
//            new AnvilGUI.Builder()
//                    .onClick((slot, stateSnapshot) -> {
//                        if (slot != AnvilGUI.Slot.OUTPUT)
//                            return Collections.emptyList();
//                        if (stateSnapshot.getText().length() < 4) {
//                            return Collections.singletonList(AnvilGUI.ResponseAction.close());
//                        }
//
//                        warp(player, warp, stateSnapshot.getText());
//                        return Collections.singletonList(AnvilGUI.ResponseAction.close());
//                    })
//                    .interactableSlots(AnvilGUI.Slot.OUTPUT)
//                    .text(Lang.ENTER_PASSWORD.asColoredString())
//                    .itemLeft(new ItemStack(Material.IRON_SWORD))
//                    .itemRight(new ItemStack(Material.IRON_SWORD))
//                    .plugin(PlayerWarpsPlugin.get())
//                    .open(player);

        } else warp(player, warp, null);
    }

    public void warp(Player player, Warp warp, String password) {
        if (warp == null)
            return;

        final String warpName = warp.getName();
        boolean isOwner = warp.canManage(player);
        boolean hasBypass = PermissionUtils.hasPermission(player, PermissionUtils.Permission.BYPASS_TELEPORT_DELAY);

        if (!warp.isAccessible() && !isOwner) {
            player.sendMessage(Lang.WARP_IS_DISABLED.asColoredString().replace("%warp%", warpName));
            return;
        }

        if (warp.isPasswordProtected()) {
            if (!warp.validatePassword(password)) {
                player.sendMessage(Lang.ENTERED_WRONG_PASSWORD.asColoredString());
                return;
            }
        }

        if (warp.getAdmission() != 0) {
            Optional.ofNullable(Hooks.getVaultHook().getApi()).ifPresent(economy -> {
                if (!economy.withdrawPlayer(player, warp.getAdmission()).transactionSuccess()) {
                    player.sendMessage(Lang.INSUFFICIENT_BALANCE_TO_TELEPORT.asColoredString().replace("%warp%", warpName));
                    return;
                }

                PlayerUtils.getOfflinePlayer(warp.getOwner()).thenAccept(
                        offlinePlayer -> economy.depositPlayer(offlinePlayer, warp.getAdmission())
                );

                //economy.depositPlayer(Bukkit.getOfflinePlayer(warp.getOwner()), warp.getAdmission());
            });
        }
//        if (Hooks.getVaultHook().isOn()) {
//            if (warp.getAdmission() != 0) {
//                if (!Hooks.getVaultHook().getApi().withdrawPlayer(player, warp.getAdmission()).transactionSuccess()) {
//                    player.sendMessage(Lang.INSUFFICIENT_BALANCE_TO_TELEPORT.asReplacedString(new HashMap<String, String>() {{
//                        put("%warp%", warpName);
//                    }}));
//                    return;
//                }
//
//                PlayerWarpsPlugin.getECONOMY().depositPlayer(Bukkit.getOfflinePlayer(warp.getOwner()), warp.getAdmission());
//            }
//        }

        teleportPlayer(player, warp.getLocation(), hasBypass);

        final UUID ownerID = warp.getOwner();

        if (warp.getAdmission() != 0 && !isOwner) {
            player.sendMessage(Lang.TELEPORT_TO_WARP_WITH_ADMISSION.asColoredString()
                    .replace("%price%", String.valueOf(warp.getAdmission()))
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

    public void warp(Player player, String warpName) {
        warp(player, getWarpFromName(warpName).orElse(null), null);
    }

    public void warp(Player player, UUID warpID) {
        warp(player, getWarpByID(warpID).orElse(null), null);
    }


    public void changeStatus(Player player, Warp warp, WarpState status) {
        if (!warp.canManage(player)) {
            player.sendMessage(Lang.NOT_OWNING.asColoredString());
            return;
        }

        warp.setStatus(status);
        player.sendMessage(Lang.WARPS_STATUS_CHANGED.asReplacedString(new HashMap<String, String>() {{
            put("%status%", status.name());
        }}));
    }

    public void teleportPlayer(Player player, Location loc, boolean cooldown) {
        if (!cooldown) {
            tp.put(player, player.getLocation().getBlockX() + player.getLocation().getBlockZ());
            player.sendMessage(Lang.TELEPORTATION.asColoredString().replace("%time%", Config.TELEPORTATION_DELAY.asString()));

            new BukkitRunnable() {

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
                }
            }.runTaskTimer(PlayerWarpsPlugin.get(), 0, 10);
        } else player.teleport(loc);
    }

    public void reloadWarps(CommandSender sender) {
        if (!PermissionUtils.hasPermission(sender, PermissionUtils.Permission.RELOAD_PLUGIN)) {
            sender.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
        } else {
            PlayerWarpsPlugin.get().reloadConfig();
            File configFile = new File(PlayerWarpsPlugin.get().getDataFolder(), "config.yml");

            try {
                ConfigUpdater.update(PlayerWarpsPlugin.get(), "config.yml", configFile, Collections.emptyList());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            CategoryManager.loadCategories();
            Lang.reload();
            sender.sendMessage(Lang.RELOAD.asColoredString());
        }
    }

    public void loadWarps() {
        boolean legacyConfig = !Optional.ofNullable(PlayerWarpsPlugin.getDataManager().getData().getString("version")).isPresent();
        Optional<ConfigurationSection> warpDataSection = Optional.ofNullable(PlayerWarpsPlugin.getDataManager().getData().getConfigurationSection("warps"));
        warpDataSection.ifPresent(warpsSection -> {
            if (legacyConfig) {
                warpsSection.getKeys(false).forEach(warp -> {
                    ConfigurationSection warpSection = warpsSection.getConfigurationSection(warp);
                    String owner = warpSection.getString("owner-id");
                    Location location = warpSection.getLocation("loc");
                    if (location == null || location.getWorld() == null) {
                        PlayerWarpsPlugin.get().getLogger().info("Warp " + warp + " was not loaded because it is located in a world that does not exist.");
                        return;
                    }
                    String item = warpSection.getString("item");
                    String description = TextUtils.colorize(warpSection.getString("lore"));
                    int ratings = warpSection.getInt("ratings");
                    List<String> reviewers = warpSection.getStringList("reviewers");
                    String status = warpSection.getBoolean("disabled") ? "CLOSED" : "OPENED";
                    int visits = warpSection.getInt("visits");
                    int admission = warpSection.getInt("price");
                    long dateCreated = warpSection.getLong("date-created");
                    long lastActivity = warpSection.getLong("warp-action");
                    String category = warpSection.getString("type");

                    PlayerWarpsPlugin.get().getLogger().info("Adding new warp " + warp);

                    addWarp(new Warp(
                            new HashMap<String, Object>() {{
                                put("uuid", UUID.randomUUID().toString());
                                put("name", warp);
                                put("displayName", warp);
                                put("owner-id", owner);
                                put("loc", location);
                                put("lore", description);
                                put("category", Optional.ofNullable(category).orElse("all"));
                                put("item", item);
                                put("ratings", ratings);
                                put("reviewers", reviewers);
                                put("visits", visits);
                                put("status", status);
                                put("password", null);
                                put("admission", admission);
                                put("date-created", dateCreated);
                                put("last-activity", lastActivity);
                            }}
                    ));
                });

                //PlayerWarpsPlugin.getDataManager().getData().set("warps", null);
                PlayerWarpsPlugin.getDataManager().getData().set("version", "2.0");
                PlayerWarpsPlugin.getDataManager().saveData();
            } else {
                warpDataSection.ifPresent(warpSection ->
                        warpSection
                                .getKeys(false)
                                .forEach(warpID -> addWarp(warpSection.getSerializable(warpID, Warp.class))));
            }
        });
    }

    public void saveWarps() {
        final ConfigurationSection warpsSection = PlayerWarpsPlugin.getDataManager().getData().createSection("warps");

        warps.forEach(warp -> warpsSection.set(warp.getWarpID().toString(), warp));

        PlayerWarpsPlugin.getDataManager().saveData();
        if (Config.AUTO_SAVE_ANNOUNCE.asBoolean()) {
            Bukkit.getLogger().info("Saving " + warps.size() + " warps");
        }
    }

    public int getAmount(Player player, int defaultValue) {
        String permissionPrefix = "playerwarps.limit.";

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getPermission().startsWith(permissionPrefix)) {
                return Integer.parseInt(attachmentInfo.getPermission().substring(attachmentInfo.getPermission().lastIndexOf(".") + 1));
            }
        }

        return defaultValue;
    }

    public boolean canHaveWarp(final Player player) {
        UUID id = player.getUniqueId();
        if (!player.hasPermission("playerwarps.limit.unlimited")) {
            return getAmount(player, Config.DEFAULT_LIMIT_SIZE.asInt()) != getOwnedWarps(id);
        }
        return true;
    }

    private int getOwnedWarps(UUID id) {
        int owned = 0;
        if (isWarps()) {
            for (Warp warp : warps) {
                if (Objects.equals(id, warp.getOwner())) {
                    ++owned;
                }
            }
        }
        return owned;
    }

    public void markPlayerForChatInput(final Player player, Warp warp, WarpAction warpAction, Object[] data) {
        player.closeInventory();
        UserManager.createUser(player, data);

        String messageToSent;
        switch (warpAction) {
            case SET_GUI_ITEM:
                messageToSent = Lang.ITEM_WRITE_MSG.asReplacedString(new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            case SET_ADMISSION:
                messageToSent = Lang.PRICE_WRITE_MESSAGE.asReplacedString(new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            case CHANGE_DISPLAY_NAME:
                messageToSent = Lang.WRITE_NEW_DISPLAY_NAME.asReplacedString(new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            case RENAME:
                messageToSent = Lang.RENAME_MSG.asReplacedString(new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            case CHANGE_OWNERSHIP:
                messageToSent = Lang.OWNER_CHANGE_MSG.asReplacedString(new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            case SET_DESCRIPTION:
                messageToSent = Lang.SET_DESCRIPTION_MESSAGE.asReplacedString(new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            case WRITE_PASSWORD:
                messageToSent = Lang.PASSWORD_CHANGE_MSG.asReplacedString(new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            default:
                messageToSent = "error";
        }

        player.sendMessage(TextUtils.replaceString(messageToSent, new HashMap<String, String>() {{
            put("%warp%", warp.getName());
        }}));
    }

    public int getCountOfWarps(String type) {
        return (int) warps.stream()
                .filter(Warp::isAccessible)
                .filter(warp -> type.equalsIgnoreCase("all") || warp.getCategory() != null && warp.getCategory().getType() != null && warp.getCategory().getType().equalsIgnoreCase(type))
                .count();
    }

    public void addWarp(Warp warp) {
        warps.add(warp);
    }

    public void removeWarp(Warp warp) {
        warps.remove(warp);
    }

    public boolean existsWarp(String warpName) {
        return warps.stream().anyMatch(w -> w.getName().equalsIgnoreCase(warpName));
    }

    public boolean isWarps() {
        return !warps.isEmpty();
    }

    public Set<Warp> getWarps() {
        return warps;
    }

    public Optional<Warp> getWarpByID(final UUID warpID) {
        return warps.stream().filter(warp -> Objects.equals(warp.getWarpID(), warpID)).findAny();
    }

    public Optional<Warp> getWarpByID(final String warpID) {
        return getWarpByID(UUID.fromString(warpID));
    }

    public Optional<Warp> getWarpFromName(final String warpName) {
        return warps.stream().filter(a -> a.getName().equalsIgnoreCase(warpName)).findAny();
    }

    public List<Warp> getPlayerFavoriteWarps(Player player) {
        return PlayerConfig.getConfig(player).getStringList("favorites").stream()
                .map(PlayerWarpsPlugin.getWarpHandler()::getWarpByID)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Set<Warp> getPlayerWarps(final Player player) {
        return warps.stream().filter(warp -> warp.canManage(player)).collect(Collectors.toSet());
    }

    public boolean checkWarp(Warp warp) {
        return warp == null;
    }

    public List<String> getBannedWorlds() {
        return bannedWorlds;
    }
}