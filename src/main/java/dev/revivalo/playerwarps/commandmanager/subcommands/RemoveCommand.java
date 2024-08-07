package dev.revivalo.playerwarps.commandmanager.subcommands;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.commandmanager.SubCommand;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.guimanager.menu.ConfirmationMenu;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.actions.RemoveWarpAction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class RemoveCommand implements SubCommand {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Deletes new pwarp";
    }

    @Override
    public String getSyntax() {
        return "/pwarp remove [warpName]";
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.REMOVE_WARP;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        final Player player = (Player) sender;
        final Optional<Warp> warp = PlayerWarpsPlugin.getWarpHandler().getWarpFromName(args[0]);
        if (!warp.isPresent()) {
            sender.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
            return;
        }

        new ConfirmationMenu(warp.get()).open(player, new RemoveWarpAction());
    }
}