package dev.revivalo.playerwarps.configuration;

import dev.revivalo.playerwarps.utils.TextUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAwareReplacer implements StringReplacer {
    @Override
    public String replace(Player player, String string) {
        return PlaceholderAPI.setPlaceholders(player, TextUtils.color(string));
    }
}
