package dev.revivalo.playerwarps.guimanager.menu.sort;

import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.warp.Warp;

import java.util.Comparator;
import java.util.List;

public class LatestSort implements Sortable {
    @Override
    public void sort(List<Warp> warps) {
        warps.sort(Comparator.comparing(Warp::getDateCreated));
    }

    @Override
    public Lang getName() {
        return Lang.LATEST;
    }
}
