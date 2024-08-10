package dev.revivalo.playerwarps.warp;

import com.tchristofferson.configupdater.ConfigUpdater;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.category.CategoryManager;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.playerconfig.PlayerConfig;
import dev.revivalo.playerwarps.user.DataSelectorType;
import dev.revivalo.playerwarps.user.UserHandler;
import dev.revivalo.playerwarps.user.WarpAction;
import dev.revivalo.playerwarps.util.ItemUtil;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.util.TextUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WarpHandler {

    private final Set<Warp> warps;
    private final List<String> bannedWorlds;

    public WarpHandler() {
        warps = new HashSet<>();

        bannedWorlds = new ArrayList<>();
        bannedWorlds.addAll(Config.BANNED_WORLDS.asList());
    }

    public void reloadWarps(CommandSender sender) {
        if (!PermissionUtil.hasPermission(sender, PermissionUtil.Permission.RELOAD_PLUGIN)) {
            sender.sendMessage(Lang.INSUFFICIENT_PERMISSIONS.asColoredString().replace("%permission%", PermissionUtil.Permission.RELOAD_PLUGIN.get()));
        } else {
            PlayerWarpsPlugin.get().reloadConfig();
            File configFile = new File(PlayerWarpsPlugin.get().getDataFolder(), "config.yml");

            try {
                ConfigUpdater.update(PlayerWarpsPlugin.get(), "config.yml", configFile, Collections.emptyList());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            CategoryManager.loadCategories();
            Config.reload();
            sender.sendMessage(Lang.RELOAD_MESSAGE.asColoredString());
        }
    }

    public void loadWarps() {
        boolean legacyConfig = !Optional.ofNullable(PlayerWarpsPlugin.getData().getConfiguration().getString("version")).isPresent();
        Optional<ConfigurationSection> warpDataSection = Optional.ofNullable(PlayerWarpsPlugin.getData().getConfiguration().getConfigurationSection("warps"));
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
                    ItemStack item = warpSection.getItemStack("item", ItemUtil.getItem(warpSection.getString("item")));
                    String description = warpSection.getString("lore");
                    int ratings = warpSection.getInt("ratings");
                    List<String> reviewers = warpSection.getStringList("reviewers");
                    String status = warpSection.getBoolean("disabled") ? "CLOSED" : "OPENED";
                    int visits = warpSection.getInt("visits");
                    int admission = warpSection.getInt("price");
                    long dateCreated = warpSection.getLong("date-created");
                    long lastActivity = warpSection.getLong("warp-action");
                    String category = warpSection.getString("type");

                    PlayerWarpsPlugin.get().getLogger().info("Adding new warp " + warp + " with " + category);

                    addWarp(new Warp(
                            new HashMap<String, Object>() {{
                                put("uuid", UUID.randomUUID().toString());
                                put("name", warp);
                                put("display-name", warp);
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
                PlayerWarpsPlugin.getData().getConfiguration().set("version", "2.0");
                PlayerWarpsPlugin.getData().getYamlFile().save();
            } else {
                warpDataSection.ifPresent(warpSection ->
                        warpSection
                                .getKeys(false)
                                .forEach(warpID -> addWarp(warpSection.getSerializable(warpID, Warp.class)))
                );
            }
        });
    }

    public void saveWarps() {
        final ConfigurationSection warpsSection = PlayerWarpsPlugin.getData().getConfiguration().createSection("warps");

        warps.forEach(warp -> warpsSection.set(warp.getWarpID().toString(), warp));

        PlayerWarpsPlugin.getData().getYamlFile().save();
        if (Config.AUTOSAVE_ANNOUNCE.asBoolean()) {
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
            return getAmount(player, Config.DEFAULT_LIMIT_SIZE.asInteger()) != getOwnedWarps(id);
        }
        return true;
    }

    private int getOwnedWarps(UUID id) {
        int owned = 0;
        if (areWarps()) {
            for (Warp warp : warps) {
                if (Objects.equals(id, warp.getOwner())) {
                    ++owned;
                }
            }
        }
        return owned;
    }

    public void markPlayerForChatInput(final Player player, Warp warp, WarpAction warpAction) {
        player.closeInventory();
        UserHandler.getUser(player)
                .addData(DataSelectorType.SELECTED_WARP, warp)
                .addData(DataSelectorType.CURRENT_WARP_ACTION, warpAction);

        String messageToSent;
        switch (warpAction) {
            case SET_GUI_ITEM:
                messageToSent = Lang.ITEM_WRITE_MSG.asReplacedString(player, new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            case SET_ADMISSION:
                messageToSent = Lang.PRICE_WRITE_MSG.asReplacedString(player, new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            case CHANGE_DISPLAY_NAME:
                messageToSent = Lang.WRITE_NEW_DISPLAY_NAME.asReplacedString(player, new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            case RENAME:
                messageToSent = Lang.RENAME_WRITE_MSG.asReplacedString(player, new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            case CHANGE_OWNERSHIP:
                messageToSent = Lang.OWNER_CHANGE_MSG.asReplacedString(player, new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            case SET_DESCRIPTION:
                messageToSent = Lang.SET_DESCRIPTION_MSG.asReplacedString(player, new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            case WRITE_PASSWORD:
                messageToSent = Lang.PASSWORD_CHANGE_MSG.asReplacedString(player, new HashMap<String, String>() {{
                    put("%warp%", warp.getName());
                }});
                break;
            default:
                messageToSent = "error";
        }

        player.sendMessage(TextUtil.replaceString(messageToSent, new HashMap<String, String>() {{
            put("%warp%", warp.getName());
        }}));

        BaseComponent[] msg = TextComponent.fromLegacyText(Lang.CANCEL_INPUT.asColoredString());
        for (BaseComponent bc : msg) {
            bc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Lang.CLICK_TO_CANCEL_INPUT.asColoredString())));
            bc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pw cancel"));
        }
        player.spigot().sendMessage(msg);
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

    public boolean areWarps() {
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