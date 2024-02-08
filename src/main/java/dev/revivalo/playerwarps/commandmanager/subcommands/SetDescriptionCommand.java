package dev.revivalo.playerwarps.commandmanager.subcommands;

import dev.revivalo.playerwarps.commandmanager.SubCommand;
import dev.revivalo.playerwarps.guimanager.menu.MenuType;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.actions.SetDescriptionAction;
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
        return "/reward description [text]";
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.SET_DESCRIPTION;
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

        new SetDescriptionAction().preExecute(player, null, args[1], MenuType.SET_UP_MENU);
    }
}
