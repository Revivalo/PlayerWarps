package dev.revivalo.playerwarps.warp.actions;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.guimanager.menu.MenuType;
import dev.revivalo.playerwarps.guimanager.menu.WarpSearch;
import dev.revivalo.playerwarps.guimanager.menu.WarpsMenu;
import dev.revivalo.playerwarps.utils.PermissionUtils;
import dev.revivalo.playerwarps.utils.SortingUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpAction;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SearchWarpAction implements WarpAction<String> {
    @Override
    public boolean execute(Player player, Warp warp, String input) {
        final List<Warp> warps = PlayerWarpsPlugin.getWarpHandler().getWarps().stream()
                .filter(Warp::isAccessible).collect(Collectors.toList());


        WarpSearch warpSearch = new WarpSearch(warps);
        CompletableFuture<List<Warp>> future = warpSearch.searchAsync(input);
        List<Warp> warpList = new ArrayList<>();

        future.thenAccept(warpList::addAll).thenRun(warpSearch::shutdown);

        try {
            PlayerWarpsPlugin.get().runSync(() ->
                    new WarpsMenu(MenuType.DEFAULT_LIST_MENU)
                    .open(player, "all", SortingUtils.SortType.LATEST, warpList));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public PermissionUtils.Permission getPermission() {
        return PermissionUtils.Permission.USE;
    }

    @Override
    public int getFee() {
        return 0;
    }

    @Override
    public Lang getInputText() {
        return Lang.ENTER_WARPS_NAME;
    }

    @Override
    public boolean isPublicAction() {
        return true;
    }
}
