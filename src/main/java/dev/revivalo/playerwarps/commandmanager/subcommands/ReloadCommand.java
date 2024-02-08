package dev.revivalo.playerwarps.commandmanager.subcommands;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.commandmanager.SubCommand;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand implements SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads a plugin's configuration";
    }

    @Override
    public String getSyntax() {
        return "/reward reload";
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.RELOAD_PLUGIN;
    }

    @Override
    public List<String> getTabCompletion(CommandSender commandSender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        PlayerWarpsPlugin.getWarpHandler().reloadWarps(sender);
    }
}
