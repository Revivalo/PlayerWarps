package cz.revivalo.playerwarps.warp;

import cz.revivalo.playerwarps.configuration.enums.Config;
import cz.revivalo.playerwarps.guimanager.GUIManager;
import cz.revivalo.playerwarps.configuration.enums.Lang;
import cz.revivalo.playerwarps.user.WarpAction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PWarpCommand implements CommandExecutor, TabExecutor {

    private final WarpHandler warpHandler;
    private final GUIManager guiManager;

    private final HashMap<UUID, Warp> warpsHashMap;

    public PWarpCommand(final WarpHandler warpHandler, final GUIManager guiManager) {
        this.warpHandler = warpHandler;
        this.guiManager = guiManager;

        warpsHashMap = warpHandler.getWarpList();
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("pwarp")){
            if (!warpsHashMap.isEmpty()){
                List<String> warps = new Vector<>();
                for (final Warp warp : warpsHashMap.values()){
                    if (warp.isPrivateState()) continue;
                    if (warp.getName().contains(args[0].toLowerCase())){
                        warps.add(warp.getName());
                    }
                }
                return warps;
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        boolean fromPlayer = sender instanceof Player;
        if (!fromPlayer) {
            sender.sendMessage("[PlayerWarps] Commands are only executable in-game!");
            return true;
        } else {
            final Player player = (Player) sender;
            final UUID id = player.getUniqueId();

            switch (args.length) {
                case 0:
                    if (player.hasPermission("playerwarps.use")) {
                        if (Config.ENABLE_CATEGORIES.asBoolean()){
                            guiManager.openCategories(player);
                        } else {
                            guiManager.openWarpsMenu(player, GUIManager.WarpMenuType.DEFAULT, "all", 1, GUIManager.SortType.VISITS);
                        }
                    } else {
                        player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
                    }
                    break;
                case 1:
                    Warp warp;
                    switch (args[0]) {
                        case "reload":
                            warpHandler.reloadWarps(player);
                            break;
                        case "help":
                            Lang.sendListToPlayer(player, Lang.HELP.asReplacedList(Collections.emptyMap()));
                            break;
                        default:
                            if (player.hasPermission("playerwarps.use")){
                                warp = warpHandler.getWarpFromName(args[0]);
                                boolean valid = !warpHandler.checkWarp(warp);
                                if (!valid){
                                    player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
                                    return true;
                                }
                                int price = warp.getPrice();
                                if (!Objects.equals(id, warp.getOwner())) {
                                    if (price != 0 && Config.ALLOW_ACCEPT_TELEPORT_MENU.asBoolean()) {
                                        warpHandler.openedFromCommand.add(player);
                                        guiManager.openAcceptMenu(player, warp, WarpAction.TELEPORT);
                                    } else {
                                        warpHandler.warp(player, warp);
                                    }
                                } else {
                                    warpHandler.warp(player, warp);
                                }
                            } else {
                                player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
                            }
                    }
                    break;
                case 2:
                    warp = warpHandler.getWarpFromName(args[1]);
                    if (!args[0].equalsIgnoreCase("create")) {
                        boolean valid = !warpHandler.checkWarp(warp);
                        if (!valid) {
                            player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
                            return true;
                        }
                    }
                    boolean isAdmin = player.hasPermission("playerwarps.admin");
                    switch (args[0]) {
                        case "create":
                            player.sendMessage(warpHandler.createWarp(player, args[1]));
                            break;
                        case "private":
                            warpHandler.makePrivate(player, warp, true);
                            break;
                        case "disable":
                        case "freeze":
                            if (player.hasPermission("playerwarps.disable") || isAdmin){
                                warpHandler.disable(player, warp, true);
                            } else player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
                            break;
                        case "text":
                        case "title":
                        case "lore":
                            if (player.hasPermission("playerwarps.lore") || isAdmin){
                                if (isAdmin || Objects.equals(id, warp.getOwner())) {
                                    //guiManager.getChat().put(id, warpName + ":lore:false");
                                    player.sendMessage(Lang.TITLE_WRITE_MSG.asColoredString().replace("%warp%", warp.getName()));
                                } else player.sendMessage(Lang.NOT_OWNING.asColoredString());
                            } else player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
                            break;
                        case "settings":
                        case "setup":
                            if (player.hasPermission("playerwarps.settings") || isAdmin) {
                                if (warpHandler.checkWarp(warp)) {
                                    if (warpHandler.isOwner(id, warp) || player.hasPermission("playerwarps.admin")) {
                                        guiManager.openSetUpMenu(player, warp);
                                    } else player.sendMessage(Lang.NOT_OWNING.asColoredString());
                                } else player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
                            } else player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
                            break;
                        case "delete":
                        case "remove":
                            guiManager.openAcceptMenu(player, warp, WarpAction.REMOVE);
                            break;
                        case "favorite":
                            warpHandler.favorite(player, warp);
                            break;
                        case "unfavorite":
                            warpHandler.unfavored(player, warp);
                            break;
                        default:
                            player.sendMessage(Lang.BAD_COMMAND_SYNTAX.asColoredString());
                            break;
                    }
                    break;
                case 3:
                    warp = warpHandler.getWarpFromName(args[1]);
                    //String warpName2 = args[1];
                    if (warpHandler.checkWarp(warp)){
                        player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
                        return true;
                    }
                    String input = args[2];
                    switch (args[0]){
                        case "price":
                            warpHandler.setAdmission(player, warp, input, false);
                            break;
                        case "settype":
                        case "type":
                            warpHandler.setType(player, warp, input);
                            break;
                        case "item":
                        case "setitem":
                            warpHandler.setItem(player, warp, input, false);
                            break;
                        case "rename":
                            warpHandler.rename(player, warp, input, false);
                            break;
                        case "transfer":
                            warpHandler.transferOwnership(player, Bukkit.getPlayer(input), warp, false);
                            break;
                        default:
                            player.sendMessage(Lang.BAD_COMMAND_SYNTAX.asColoredString());
                            break;
                    }
                    break;
                default:
                    player.sendMessage(Lang.BAD_COMMAND_SYNTAX.asColoredString());
            }
        }
        return true;
    }
}

