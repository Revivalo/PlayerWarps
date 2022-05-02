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
                List<String> warps = new ArrayList<>();
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
            Player p = (Player) sender;
            UUID id = p.getUniqueId();

            switch (args.length) {
                case 0:
                    if (p.hasPermission("playerwarps.use")) {
                        if (Lang.ENABLECATEGORIES.getBoolean()){
                            guiManager.openCategories(p);
                        } else {
                            guiManager.openWarpsMenu(p, "all", false);
                        }
                    } else {
                        p.sendMessage(Lang.INSUFFICIENTPERMS.content());
                    }
                    break;
                case 1:
                    String warpName;
                    switch (args[0]) {
                        case "reload":
                            warpHandler.reloadWarps(p);
                            break;
                        case "help":
                            for (String str : Lang.HELP.contentLore()){
                                p.sendMessage(str);
                            }
                            break;
                        default:
                            if (p.hasPermission("playerwarps.use")){
                                warpName = args[0];
                                boolean valid = !warpHandler.checkWarp(warpName);
                                if (!valid){
                                    p.sendMessage(Lang.NONEXISTINGWARP.content());
                                    return true;
                                }
                                Warp warp = warpsHashMap.get(warpName);
                                int price = warp.getPrice();
                                if (!Objects.equals(id, warp.getOwner())) {
                                    if (price != 0 && Lang.ALLOWACCEPTTELEMPORTMENU.getBoolean()) {
                                        warpHandler.remove.put(id, warpName);
                                        warpHandler.openedFromCommand.add(p);
                                        guiManager.openTeleportAcceptMenu(p, price);
                                    } else {
                                        warpHandler.warp(p, warpName);
                                    }
                                } else {
                                    warpHandler.warp(p, warpName);
                                }
                            } else {
                                p.sendMessage(Lang.INSUFFICIENTPERMS.content());
                            }
                    }
                    break;
                case 2:
                    warpName = args[1];
                    if (!args[0].equalsIgnoreCase("create")) {
                        boolean valid = !warpHandler.checkWarp(warpName);
                        if (!valid) {
                            p.sendMessage(Lang.NONEXISTINGWARP.content());
                            return true;
                        }
                    }
                    Warp warp = warpsHashMap.get(warpName);
                    boolean isAdmin = p.hasPermission("playerwarps.admin");
                    switch (args[0]) {
                        case "create":
                            warpHandler.createWarp(p, warpName);
                            break;
                        case "private":
                            warpHandler.makePrivate(p, warpName, true);
                            break;
                        case "disable":
                        case "freeze":
                            if (p.hasPermission("playerwarps.disable") || isAdmin){
                                warpHandler.disable(p, warpName);
                            } else p.sendMessage(Lang.INSUFFICIENTPERMS.content());
                            break;
                        case "text":
                        case "title":
                        case "lore":
                            if (p.hasPermission("playerwarps.lore") || isAdmin){
                                if (isAdmin || Objects.equals(id, warp.getOwner())) {
                                    //guiManager.getChat().put(id, warpName + ":lore:false");
                                    p.sendMessage(Lang.TITLEWRITEMSG.content().replace("%warp%", warpName));
                                } else p.sendMessage(Lang.NOTOWNING.content());
                            } else p.sendMessage(Lang.INSUFFICIENTPERMS.content());
                            break;
                        case "settings":
                        case "setup":
                            if (p.hasPermission("playerwarps.settings") || isAdmin) {
                                if (warpHandler.checkWarp(warpName)) {
                                    if (warpHandler.isOwner(id, warp) || p.hasPermission("playerwarps.admin")) {
                                        guiManager.openSetUpMenu(p, warpName);
                                    } else p.sendMessage(Lang.NOTOWNING.content());
                                } else p.sendMessage(Lang.NONEXISTINGWARP.content());
                            } else p.sendMessage(Lang.INSUFFICIENTPERMS.content());
                            break;
                        case "delete":
                        case "remove":
                            warpHandler.remove.put(id, warpName + ":true");
                            guiManager.getActualPage().put(id, 0);
                            guiManager.openAcceptMenu(p, warpName);
                            break;
                        case "favorite":
                            warpHandler.favorite(p, warpName);
                            break;
                        case "unfavorite":
                            warpHandler.unfavored(p, warpName);
                            break;
                        default:
                            p.sendMessage(Lang.BADCOMMANDSYNTAX.content());
                            break;
                    }
                    break;
                case 3:
                    String warpName2 = args[1];
                    if (warpHandler.checkWarp(warpName2)){
                        p.sendMessage(Lang.NONEXISTINGWARP.content());
                        return true;
                    }
                    String input = args[2];
                    switch (args[0]){
                        case "price":
                            warpHandler.setPrice(p, warpName2, input, false);
                            break;
                        case "settype":
                        case "type":
                            warpHandler.setType(p, warpName2, input);
                            break;
                        case "item":
                        case "setitem":
                            warpHandler.setItem(p, warpName2, input, false);
                            break;
                        case "rename":
                            warpHandler.rename(p, warpName2, input, false);
                            break;
                        case "transfer":
                            warpHandler.transferOwnership(p, Bukkit.getPlayer(input), warpName2, false);
                            break;
                        default:
                            p.sendMessage(Lang.BADCOMMANDSYNTAX.content());
                            break;
                    }
                    break;
                default:
                    p.sendMessage(Lang.BADCOMMANDSYNTAX.content());
            }
        }
        return true;
    }
}

