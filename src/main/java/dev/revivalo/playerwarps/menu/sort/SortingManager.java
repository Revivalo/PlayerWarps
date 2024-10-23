package dev.revivalo.playerwarps.menu.sort;

import dev.revivalo.playerwarps.warp.Warp;

import java.util.List;

public class SortingManager {
    private final List<Sortable> sortTypes;

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
        sortType.sort(warps);
    }

    public List<Sortable> getSortTypes() {
        return sortTypes;
    }
}
