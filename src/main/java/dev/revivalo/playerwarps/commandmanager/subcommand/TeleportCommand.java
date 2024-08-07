package dev.revivalo.playerwarps.commandmanager.subcommand;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.commandmanager.SubCommand;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.PermissionUtil;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.action.PreTeleportToWarpAction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeleportCommand implements SubCommand {
    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getDescription() {
        return "Teleports to the warp";
    }

    @Override
    public String getSyntax() {
        return "/pwarp teleport [warpName]";
    }

    @Override
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.USE;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return PlayerWarpsPlugin.getWarpHandler().getWarps().stream().filter(Warp::isAccessible).map(Warp::getName).collect(Collectors.toList());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[PlayerWarps] Only in-game command!");
            return;
        }
        final Player player = (Player) sender;

        if (args.length > 0) {
            Optional<Warp> warpOptional = PlayerWarpsPlugin.getWarpHandler().getWarpFromName(args[0]);
            if (!warpOptional.isPresent()) {
                player.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
                return;
            }

            new PreTeleportToWarpAction().preExecute(player, warpOptional.get(), null, null);
        } else {
            player.sendMessage(Lang.BAD_COMMAND_SYNTAX.asColoredString().replace("%syntax%", getSyntax()));
        }
    }
}