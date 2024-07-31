package dev.revivalo.playerwarps.commandmanager.argumentmatchers;

import dev.revivalo.playerwarps.commandmanager.ArgumentMatcher;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class StartingWithStringArgumentMatcher implements ArgumentMatcher {
    @Override
    public List<String> filter(List<String> tabCompletions, String argument) {
        List<String> result = new ArrayList<>();

        StringUtil.copyPartialMatches(argument, tabCompletions, result);

        return result;
    }
}
