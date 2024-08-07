package dev.revivalo.playerwarps.commandmanager.subcommand;

import dev.revivalo.playerwarps.commandmanager.SubCommand;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.guimanager.menu.ManageMenu;
import dev.revivalo.playerwarps.user.DataSelectorType;
import dev.revivalo.playerwarps.user.User;
import dev.revivalo.playerwarps.user.UserHandler;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CancelCommand implements SubCommand {
    @Override
    public String getName() {
        return "cancel";
    }

    @Override
    public String getDescription() {
        return "Exits the input mode";
    }

    @Override
    public String getSyntax() {
        return "/pwarp cancel";
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.CREATE_WARP;
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

        final User user = UserHandler.getUser((Player) sender);

        user.addData(DataSelectorType.CURRENT_WARP_ACTION, null);
        user.getData(DataSelectorType.ACTUAL_PAGE);

        new ManageMenu((Warp) user.getData(DataSelectorType.SELECTED_WARP))
                .open(user.getPlayer());

        user.getPlayer().sendMessage(Lang.INPUT_CANCELLED.asColoredString());
    }
}
