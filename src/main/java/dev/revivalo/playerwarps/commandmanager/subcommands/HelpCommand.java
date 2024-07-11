package dev.revivalo.playerwarps.commandmanager.subcommands;

import dev.revivalo.playerwarps.commandmanager.SubCommand;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class HelpCommand implements SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Sends a list with a commands and instructions";
    }

    @Override
    public String getSyntax() {
        return "/pwarp help";
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.HELP;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Lang.HELP.asReplacedList(null, Collections.emptyMap()).forEach(line -> sender.sendMessage(String.valueOf(line)));
    }
}