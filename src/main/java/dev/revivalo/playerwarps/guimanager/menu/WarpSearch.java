package dev.revivalo.playerwarps.guimanager.menu;

import dev.revivalo.playerwarps.warp.Warp;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class WarpSearch {
    private final List<Warp> warps;
    private final ExecutorService executor;

    public WarpSearch(List<Warp> warps) {
        this.warps = warps;
        this.executor = Executors.newCachedThreadPool();
    }

    public CompletableFuture<List<Warp>> searchAsync(String query) {
        return CompletableFuture.supplyAsync(() -> search(query), executor);
    }

    private List<Warp> search(String query) {
        String lowerCaseQuery = query.toLowerCase();
        return warps.stream()
                .filter(warp -> warp.getName().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    }

    public void shutdown() {
        executor.shutdown();
    }
}