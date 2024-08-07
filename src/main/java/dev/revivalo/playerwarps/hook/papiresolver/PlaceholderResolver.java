package dev.revivalo.playerwarps.hook.papiresolver;

import org.bukkit.entity.Player;

public interface PlaceholderResolver {
    boolean canResolve(String rawPlaceholder);
    String resolve(Player p, String rawPlaceholder);
}
