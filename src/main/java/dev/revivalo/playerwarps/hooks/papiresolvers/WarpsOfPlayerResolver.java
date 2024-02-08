package dev.revivalo.playerwarps.hooks.papiresolvers;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import org.bukkit.entity.Player;

public class WarpsOfPlayerResolver implements PlaceholderResolver {
    @Override
    public boolean canResolve(String rawPlaceholder) {
        return rawPlaceholder.startsWith("total_warps");
    }

    @Override
    public String resolve(Player p, String rawPlaceholder) {
        return String.valueOf(PlayerWarpsPlugin.getWarpHandler().getWarps().size());
    }
}
