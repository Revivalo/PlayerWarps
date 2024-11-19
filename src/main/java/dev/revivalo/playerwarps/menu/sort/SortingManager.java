package dev.revivalo.playerwarps.menu.sort;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.warp.Warp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortingManager {
    private final List<Sortable> sortTypes;
    private final Map<Sortable, List<Warp>> cachedSortedWarps = new HashMap<>();


    public SortingManager(List<Sortable> sortTypes) {
        this.sortTypes = sortTypes;
    }

    public Sortable getDefaultSortType() {
        return sortTypes.get(0);
    }

    public Sortable nextSortType(Sortable currentSortType) {
        int currentIndex = sortTypes.indexOf(currentSortType);

        if (currentIndex == -1) {
            throw new IllegalArgumentException("Invalid current sort type: " + currentSortType.getName());
        }

        int nextIndex = (currentIndex + 1) % sortTypes.size();
        return sortTypes.get(nextIndex);
    }

    public void sortWarps(List<Warp> warps, Sortable sortType) {
        if (!cachedSortedWarps.containsKey(sortType)) {
            List<Warp> sorted = new ArrayList<>(warps);
            sortType.sort(sorted); // Třídění se provede jen jednou
            cachedSortedWarps.put(sortType, sorted);
        }
        warps.clear();
        warps.addAll(cachedSortedWarps.get(sortType));
    }

    public void sortWarpsAsync(List<Warp> warps, Sortable sortType, Runnable callback) {
        PlayerWarpsPlugin.get().getScheduler().runTaskAsynchronously(PlayerWarpsPlugin.get(), () -> {
            List<Warp> sorted = new ArrayList<>(warps);
            sortType.sort(sorted);

            PlayerWarpsPlugin.get().getScheduler().runTask(PlayerWarpsPlugin.get(), () -> {
                warps.clear();
                warps.addAll(sorted);
                callback.run();
            });
        });
    }

    public void invalidateCache() {
        cachedSortedWarps.clear();
    }


    public List<Sortable> getSortTypes() {
        return sortTypes;
    }
}
