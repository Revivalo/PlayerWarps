package dev.revivalo.playerwarps.commandmanager.subcommands;

import dev.revivalo.playerwarps.commandmanager.SubCommand;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.guimanager.menu.ConfirmationMenu;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.actions.CreateWarpAction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class CreateCommand implements SubCommand {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Creates new pwarp";
    }

    @Override
    public String getSyntax() {
        return "/pwarp create [warpName]";
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.CREATE_WARP;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[PlayerWarps] Warps can create only player!");
            return;
        }

        final Player player = (Player) sender;

        try {
            new ConfirmationMenu(new Warp(new HashMap<String, Object>(){{put("name", args[0]);}})).open(player, new CreateWarpAction(args[0]));
        } catch (ArrayIndexOutOfBoundsException ex) {
            player.sendMessage(Lang.BAD_COMMAND_SYNTAX.asColoredString().replace("%syntax%", getSyntax()));
        }
    }
}
