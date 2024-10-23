package dev.revivalo.playerwarps.menu.sort;

import dev.revivalo.playerwarps.configuration.file.Lang;
import dev.revivalo.playerwarps.warp.Warp;

import java.util.List;

public interface Sortable {
    void sort(List<Warp> warps);
    Lang getName();
}
