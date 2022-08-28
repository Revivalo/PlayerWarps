package cz.revivalo.playerwarps.warp;

import cz.revivalo.playerwarps.guimanager.GUIManager;
import cz.revivalo.playerwarps.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class PWarpCommand implements CommandExecutor, TabExecutor {

    private final WarpHandler warpHandler;
    private final GUIManager guiManager;

    private final HashMap<String, Warp> warpsHashMap;

    public PWarpCommand(final WarpHandler warpHandler, final GUIManager guiManager) {
        this.warpHandler = warpHandler;
        this.guiManager = guiManager;

        warpsHashMap = warpHandler.getWarpList();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("pwarp")){
            if (!warpsHashMap.isEmpty()){
                List<String> warps = new Vector<>();
                for (String warp : warpsHashMap.keySet()){
                    if (warpsHashMap.get(warp).isPrivateState()) continue;
                    if (warp.toLowerCase().contains(args[0].toLowerCase())){
                        warps.add(warp);
                    }
                }
                return warps;
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean fromPlayer = sender instanceof Player;
        if (!fromPlayer) {
            sender.sendMessage("[PlayerWarps] Only in-game command!");
            return true;
        } else {
            final Player player = (Player) sender;
            final UUID id = player.getUniqueId();

            switch (args.length) {
                case 0:
                    if (player.hasPermission("playerwarps.use")) {
                        if (Lang.ENABLE_CATEGORIES.getBoolean()){
                            guiManager.openCategories(player);
                        } else {
                            guiManager.openWarpsMenu(player, "all", false);
                        }
                    } else {
                        player.sendMessage(Lang.INSUFFICIENT_PERMS.getString());
                    }
                    break;
                case 1:
                    String warpName;
                    switch (args[0]) {
                        case "reload":
                            warpHandler.reloadWarps(player);
                            break;
                        case "help":
                            for (final String str : Lang.HELP.getStringList()){
                                player.sendMessage(str);
                            }
                            break;
                        default:
                            if (player.hasPermission("playerwarps.use")){
                                warpName = args[0];
                                boolean valid = !warpHandler.checkWarp(warpName);
                                if (!valid){
                                    player.sendMessage(Lang.NON_EXISTING_WARP.getString());
                                    return true;
                                }
                                final Warp warp = warpsHashMap.get(warpName);
                                int price = warp.getPrice();
                                if (!Objects.equals(id, warp.getOwner())) {
                                    if (price != 0 && Lang.ALLOW_ACCEPT_TELEPORT_MENU.getBoolean()) {
                                        warpHandler.remove.put(id, warpName);
                                        warpHandler.openedFromCommand.add(player);
                                        guiManager.openTeleportAcceptMenu(player, price);
                                    } else {
                                        warpHandler.warp(player, warpName);
                                    }
                                } else {
                                    warpHandler.warp(player, warpName);
                                }
                            } else {
                                player.sendMessage(Lang.INSUFFICIENT_PERMS.getString());
                            }
                    }
                    break;
                case 2:
                    warpName = args[1];
                    if (!args[0].equalsIgnoreCase("create")) {
                        boolean valid = !warpHandler.checkWarp(warpName);
                        if (!valid) {
                            player.sendMessage(Lang.NON_EXISTING_WARP.getString());
                            return true;
                        }
                    }
                    final Warp warp = warpsHashMap.get(warpName);
                    boolean isAdmin = player.hasPermission("playerwarps.admin");
                    switch (args[0]) {
                        case "create":
                            warpHandler.createWarp(player, warpName);
                            break;
                        case "private":
                            warpHandler.makePrivate(player, warpName, true);
                            break;
                        case "disable":
                        case "freeze":
                            if (player.hasPermission("playerwarps.disable") || isAdmin){
                                warpHandler.disable(player, warpName);
                            } else player.sendMessage(Lang.INSUFFICIENT_PERMS.getString());
                            break;
                        case "text":
                        case "title":
                        case "lore":
                            if (player.hasPermission("playerwarps.lore") || isAdmin){
                                if (isAdmin || Objects.equals(id, warp.getOwner())) {
                                    //guiManager.getChat().put(id, warpName + ":lore:false");
                                    player.sendMessage(Lang.TITLE_WRITE_MSG.getString().replace("%warp%", warpName));
                                } else player.sendMessage(Lang.NOTOWNING.getString());
                            } else player.sendMessage(Lang.INSUFFICIENT_PERMS.getString());
                            break;
                        case "settings":
                        case "setup":
                            if (player.hasPermission("playerwarps.settings") || isAdmin) {
                                if (warpHandler.checkWarp(warpName)) {
                                    if (warpHandler.isOwner(id, warp) || player.hasPermission("playerwarps.admin")) {
                                        guiManager.openSetUpMenu(player, warpName);
                                    } else player.sendMessage(Lang.NOTOWNING.getString());
                                } else player.sendMessage(Lang.NON_EXISTING_WARP.getString());
                            } else player.sendMessage(Lang.INSUFFICIENT_PERMS.getString());
                            break;
                        case "delete":
                        case "remove":
                            warpHandler.remove.put(id, warpName + ":true");
                            guiManager.getActualPage().put(id, 0);
                            guiManager.openAcceptMenu(player, warpName);
                            break;
                        case "favorite":
                            warpHandler.favorite(player, warpName);
                            break;
                        case "unfavorite":
                            warpHandler.unfavored(player, warpName);
                            break;
                        default:
                            player.sendMessage(Lang.BAD_COMMAND_SYNTAX.getString());
                            break;
                    }
                    break;
                case 3:
                    String warpName2 = args[1];
                    if (warpHandler.checkWarp(warpName2)){
                        player.sendMessage(Lang.NON_EXISTING_WARP.getString());
                        return true;
                    }
                    String input = args[2];
                    switch (args[0]){
                        case "price":
                            warpHandler.setPrice(player, warpName2, input, false);
                            break;
                        case "settype":
                        case "type":
                            warpHandler.setType(player, warpName2, input);
                            break;
                        case "item":
                        case "setitem":
                            warpHandler.setItem(player, warpName2, input, false);
                            break;
                        case "rename":
                            warpHandler.rename(player, warpName2, input, false);
                            break;
                        case "transfer":
                            warpHandler.transferOwnership(player, Bukkit.getPlayer(input), warpName2, false);
                            break;
                        default:
                            player.sendMessage(Lang.BAD_COMMAND_SYNTAX.getString());
                            break;
                    }
                    break;
                default:
                    player.sendMessage(Lang.BAD_COMMAND_SYNTAX.getString());
            }
        }
        return true;
    }
}

