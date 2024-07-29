package dev.revivalo.playerwarps.commandmanager;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.actions.PreTeleportToWarpAction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public abstract class MainCommand implements TabExecutor {
    protected final Set<SubCommand> subCommands = new HashSet<>();

    protected final String noPermMessage;
    protected final ArgumentMatcher argumentMatcher;

    public MainCommand(String noPermissionMessage, ArgumentMatcher argumentMatcher) {
        this.noPermMessage = noPermissionMessage;
        this.argumentMatcher = argumentMatcher;

        registerSubCommands();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            perform(sender);
            return true;
        }

        SubCommand subCommand = subCommands.stream().filter(sc -> sc.getName().equalsIgnoreCase(args[0])).findAny().orElse(getDefaultSyntax());

        if (subCommand == null) {
            Optional<Warp> warpOptional = PlayerWarpsPlugin.getWarpHandler().getWarpFromName(args[0]);
            if (!warpOptional.isPresent()) {
                sender.sendMessage(Lang.NON_EXISTING_WARP.asColoredString());
                return true;
            }

            new PreTeleportToWarpAction().preExecute((Player) sender, warpOptional.get(), null, null);
            //sender.sendMessage(Lang.UNKNOWN_COMMAND.asColoredString());
            return true;
        }

        if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission().get())) {
            subCommand.perform(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            sender.sendMessage(noPermMessage);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 0) {
            return null;
        }

        if (args.length == 1) {
            List<String> subCommandsTC = subCommands.stream().filter(sc -> sc.getPermission() == null || sender.hasPermission(sc.getPermission().get())).map(SubCommand::getName).collect(Collectors.toList());

            List<String> suggestions = getMatchingStrings(subCommandsTC, args[args.length - 1], argumentMatcher);

            List<String> accessibleWarps = PlayerWarpsPlugin.getWarpHandler().getWarps().stream()
                    .filter(Warp::isAccessible)
                    .map(Warp::getName)
                    .collect(Collectors.toList());


            suggestions.addAll(accessibleWarps);
            return suggestions;
        }

        SubCommand subCommand = subCommands.stream().filter(sc -> sc.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);

        if (subCommand == null) {
            return null;
        }

        List<String> subCommandTB = subCommand.getTabCompletion(sender, args.length - 2, args);

        return getMatchingStrings(subCommandTB, args[args.length - 1], argumentMatcher);
    }

    private static List<String> getMatchingStrings(List<String> tabCompletions, String arg, ArgumentMatcher argumentMatcher) {
        if (tabCompletions == null || arg == null) {
            return null;
        }

        List<String> result = argumentMatcher.filter(tabCompletions, arg);

        Collections.sort(result);

        return result;
    }

    public void registerMainCommand(JavaPlugin main, String cmdName) {
        PluginCommand cmd = main.getCommand(cmdName);

        if (cmd == null) {
            PlayerWarpsPlugin.get().getLogger().info("Command " + cmdName + " isn't registered in plugin.yml!");
            return;
        }

        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
        cmd.setPermissionMessage(noPermMessage);
    }

    protected abstract void registerSubCommands();

    protected abstract void perform(CommandSender sender);

    protected SubCommand getDefaultSyntax() {
        return subCommands.stream().filter(sc -> sc.getName().equalsIgnoreCase("default")).findAny().orElse(null);
    }

    @SuppressWarnings("unused")
    public Set<SubCommand> getSubCommands() {
        return new HashSet<>(subCommands);
    }
}