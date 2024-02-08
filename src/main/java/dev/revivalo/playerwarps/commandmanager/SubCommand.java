package dev.revivalo.playerwarps.commandmanager;

import dev.revivalo.playerwarps.utils.PermissionUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    String getName();

    String getDescription();

    String getSyntax();

    PermissionUtils.Permission getPermission();

    List<String> getTabCompletion(CommandSender sender, int index, String[] args);

    void perform(CommandSender sender, String[] args);
}
