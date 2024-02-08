package dev.revivalo.playerwarps.commandmanager.subcommands;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.commandmanager.SubCommand;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ManageCommand implements SubCommand {
    @Override
    public String getName() {
        return "manage";
    }

    @Override
    public String getDescription() {
        return "Opens manage menu for stated warp";
    }

    @Override
    public String getSyntax() {
        return "/pwarp manage [warpName]";
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.MANAGE;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        final Player player = (Player) sender;

        return PlayerWarpsPlugin.getWarpHandler().getPlayerWarps(player).stream().map(Warp::getName).collect(Collectors.toList());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

    }
}
