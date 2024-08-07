package dev.revivalo.playerwarps.commandmanager.subcommand;

import dev.revivalo.playerwarps.commandmanager.SubCommand;
import dev.revivalo.playerwarps.util.PermissionUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FavoriteCommand implements SubCommand {
    @Override
    public String getName() {
        return "favorite";
    }

    @Override
    public String getDescription() {
        return "Saves warp to favorite warps";
    }

    @Override
    public String getSyntax() {
        return "/pwarp favorite [warpName]";
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.FAVORITE_WARP;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

    }
}