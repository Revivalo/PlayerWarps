package dev.revivalo.playerwarps.commandmanager.command;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.commandmanager.MainCommand;
import dev.revivalo.playerwarps.commandmanager.argumentmatcher.StartingWithStringArgumentMatcher;
import dev.revivalo.playerwarps.commandmanager.subcommand.*;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.menu.CategoriesMenu;
import dev.revivalo.playerwarps.menu.MenuType;
import dev.revivalo.playerwarps.menu.WarpsMenu;
import dev.revivalo.playerwarps.util.PermissionUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PwarpMainCommand extends MainCommand {
    public PwarpMainCommand() {
        super(Lang.INSUFFICIENT_PERMISSIONS, new StartingWithStringArgumentMatcher());
    }

    @Override
    protected void registerSubCommands() {
        subCommands.add(new HelpCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new CreateCommand());
        subCommands.add(new ManageCommand());
        subCommands.add(new RemoveCommand());
        subCommands.add(new TeleportCommand());
        //subCommands.add(new ImportCommand());
    }

    @Override
    protected void perform(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[PlayerWarps] You can open list of warps menu only as a player!");
            return;
        }

        final Player player = (Player) sender;

        if (PermissionUtil.hasPermission(player, PermissionUtil.Permission.USE)) {
            if (Config.ENABLE_CATEGORIES.asBoolean()){
                new CategoriesMenu().open(player);
            } else {
                new WarpsMenu(MenuType.DEFAULT_LIST_MENU)
                        .setPage(1)
                        .open(player, "all", PlayerWarpsPlugin.getWarpHandler().getSortingManager().getDefaultSortType());
            }
        } else {
            player.sendMessage(Lang.INSUFFICIENT_PERMISSIONS.asColoredString().replace("%permission%", PermissionUtil.Permission.USE.get()));
        }
    }
}
