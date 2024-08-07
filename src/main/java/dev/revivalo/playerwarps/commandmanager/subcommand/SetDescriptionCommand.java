package dev.revivalo.playerwarps.commandmanager.subcommand;

import dev.revivalo.playerwarps.commandmanager.SubCommand;
import dev.revivalo.playerwarps.guimanager.menu.MenuType;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.action.SetDescriptionAction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetDescriptionCommand implements SubCommand {
    @Override
    public String getName() {
        return "description";
    }

    @Override
    public String getDescription() {
        return "Set description of warp";
    }

    @Override
    public String getSyntax() {
        return "/pwarp description [text]";
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.SET_DESCRIPTION;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[PlayerWarps] Only in-game command!");
            return;
        }

        final Player player = (Player) sender;

        new SetDescriptionAction().preExecute(player, null, args[1], MenuType.MANAGE_MENU, 0);
    }
}
