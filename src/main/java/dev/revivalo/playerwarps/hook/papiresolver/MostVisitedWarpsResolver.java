package dev.revivalo.playerwarps.hook.papiresolver;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.util.SortingUtil;
import dev.revivalo.playerwarps.warp.Warp;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MostVisitedWarpsResolver implements PlaceholderResolver {
    @Override
    public boolean canResolve(String rawPlaceholder) {
        return rawPlaceholder.startsWith("most_visited");
    }

    @Override
    public String resolve(Player p, String rawPlaceholder) {
        final List<Warp> warps = PlayerWarpsPlugin.getWarpHandler().getWarps().stream()
                .filter(Warp::isAccessible).sorted(SortingUtil.SortType.VISITS.getComparator()).collect(Collectors.toList());

        StringBuilder list = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            try {
                list.append(warps.get(i).getName()).append("\n");
            } catch (IndexOutOfBoundsException ex) {
                list.append(Lang.NO_WARP_AT_POSITION.asColoredString()).append("\n");
            }
        }

        return list.toString();
    }
}
