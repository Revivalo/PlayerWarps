package dev.revivalo.playerwarps.hooks.papiresolvers;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class PAPIRegister extends PlaceholderExpansion {
    private final HashSet<PlaceholderResolver> resolvers = new HashSet<>();
    public PAPIRegister() {
        registerDefaultResolvers();
    }

    private void registerDefaultResolvers() {
        registerResolver(new TotalWarpsResolver());
        //registerResolver(new MostVisitedWarpsResolver());
    }

    public void registerResolver(PlaceholderResolver resolver) {
        resolvers.add(resolver);
    }

    @Override
    public String getIdentifier() {
        return "playerwarps";
    }

    @Override
    public String getAuthor() {
        return PlayerWarpsPlugin.get().getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return PlayerWarpsPlugin.get().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        String resolvedStr = null;

        for (PlaceholderResolver resolver : resolvers) {
            if (resolver.canResolve(identifier)) {
                resolvedStr = resolver.resolve(player, identifier);
                break;
            }
        }

        return resolvedStr;
    }
}
