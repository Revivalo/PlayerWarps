package dev.revivalo.playerwarps.commandmanager.subcommand;

import dev.revivalo.playerwarps.commandmanager.SubCommand;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.PermissionUtil;
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
    public PermissionUtil.Permission getPermission() {
        return PermissionUtil.Permission.HELP;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Lang.HELP_MESSAGE_LORE.asReplacedList(null, Collections.emptyMap()).forEach(line -> sender.sendMessage(String.valueOf(line)));
    }
}