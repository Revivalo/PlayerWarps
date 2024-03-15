package dev.revivalo.playerwarps.commandmanager.commands;

import dev.revivalo.playerwarps.commandmanager.MainCommand;
import dev.revivalo.playerwarps.commandmanager.argumentmatchers.StartingWithStringArgumentMatcher;
import dev.revivalo.playerwarps.commandmanager.subcommands.*;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.guimanager.menu.CategoriesMenu;
import dev.revivalo.playerwarps.guimanager.menu.MenuType;
import dev.revivalo.playerwarps.guimanager.menu.WarpsMenu;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.utils.SortingUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PwarpMainCommand extends MainCommand {
    public PwarpMainCommand() {
        super(Lang.INSUFFICIENT_PERMS.asColoredString(), new StartingWithStringArgumentMatcher());
    }

    @Override
    protected void registerSubCommands() {
        subCommands.add(new HelpCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new CreateCommand());
        subCommands.add(new FavoriteCommand());
        subCommands.add(new ManageCommand());
        subCommands.add(new RemoveCommand());
        subCommands.add(new TeleportCommand());
    }

    @Override
    protected void perform(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[PlayerWarps] You can open list of warps menu only as a player!");
            return;
        }

        final Player player = (Player) sender;

        if (PermissionUtils.hasPermission(player, PermissionUtils.Permission.USE)) {
            if (Config.ENABLE_CATEGORIES.asBoolean()){
                new CategoriesMenu().open(player);//PlayerWarpsPlugin.getGuiManager().openCategories(player);
            } else {
                new WarpsMenu(MenuType.DEFAULT_LIST_MENU)
                        .setPage(1)
                        .open(player, "all", SortingUtils.SortType.LATEST); //PlayerWarpsPlugin.getGuiManager().openWarpsMenu(player, GUIManager.WarpMenuType.DEFAULT, "all", 1, SortingUtils.SortType.LATEST);
            }
        } else {
            player.sendMessage(Lang.INSUFFICIENT_PERMS.asColoredString());
        }
    }
}
