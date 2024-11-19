package dev.revivalo.playerwarps.configuration;

import dev.revivalo.playerwarps.util.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAwareReplacer implements StringReplacer {
    @Override
    public String replace(Player player, String string) {
        return PlaceholderAPI.setPlaceholders(player, TextUtil.colorize(string));
    }
}
