package dev.revivalo.playerwarps.configuration;

import dev.revivalo.playerwarps.utils.TextUtils;
import org.bukkit.entity.Player;

public class ColorReplacer implements StringReplacer {
    @Override
    public String replace(Player player, String string) {
        return TextUtils.color(string);
    }
}
