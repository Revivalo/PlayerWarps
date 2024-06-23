package dev.revivalo.playerwarps;

import com.tchristofferson.configupdater.ConfigUpdater;
import dev.revivalo.playerwarps.categories.CategoryManager;
import dev.revivalo.playerwarps.commandmanager.commands.PwarpMainCommand;
import dev.revivalo.playerwarps.configuration.enums.Config;
import dev.revivalo.playerwarps.datamanager.DataManager;
import dev.revivalo.playerwarps.hooks.Hooks;
import dev.revivalo.playerwarps.listeners.ChatSendListener;
import dev.revivalo.playerwarps.listeners.PlayerJoinListener;
import dev.revivalo.playerwarps.updatechecker.UpdateChecker;
import dev.revivalo.playerwarps.updatechecker.UpdateNotificator;
import dev.revivalo.playerwarps.utils.VersionUtils;
import dev.revivalo.playerwarps.warp.Warp;
import dev.revivalo.playerwarps.warp.WarpHandler;
import io.github.g00fy2.versioncompare.Version;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class PlayerWarpsPlugin extends JavaPlugin {
    /*
    NEXT UPDATE:
     */

    private static WarpHandler warpHandler;
    private static DataManager dataManager;

    private static PlayerWarpsPlugin plugin;

    private static String latestVersion;

    @Override
    public void onEnable() {
        setPlugin(this);
        Config.reload();

        new Metrics(this, 12061);

        Hooks.hook();

        if (Config.UPDATE_CHECKER.asBoolean()) {
            new UpdateChecker(this, 79089).getVersion(pluginVersion -> {
                setLatestVersion(pluginVersion);

                final String actualVersion = getDescription().getVersion();
                Version version = new Version(pluginVersion);
                final boolean isNewerVersion = version.isHigherThan(actualVersion);
                final boolean isDevelopmentBuild = version.isLowerThan(actualVersion);

                if (isDevelopmentBuild) {
                    getLogger().info(String.format("You are running a development build (%s).", actualVersion));
                } else {

                    if (isNewerVersion) {
                        getLogger().info(String.format("There is a new v%s update available (You are running v%s).\n" +
                                "Outdated versions are no longer supported, get the latest one here: " +
                                "https://bit.ly/revivalo-playerwarps", pluginVersion, actualVersion));
                    } else {
                        getLogger().info(String.format("You are running the latest release (%s).", pluginVersion));
                    }
                }
                VersionUtils.setLatestVersion(!isNewerVersion);
            });
        }

        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(this, "config.yml", configFile, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        CategoryManager.loadCategories();

        reloadConfig();
        ConfigurationSerialization.registerClass(Warp.class);
        setDataManager(new DataManager());
        dataManager.setup();
        dataManager.saveData();

        setWarpHandler(new WarpHandler());
        warpHandler.loadWarps();

        if (Config.AUTO_SAVE_ENABLED.asBoolean()) {
            long intervalInTicks = (Config.AUTO_SAVE_INTERVAL.asLong() * 60) * 20;
            new BukkitRunnable() {
                @Override
                public void run() {
                    warpHandler.saveWarps();
                }
            }.runTaskTimerAsynchronously(this, intervalInTicks, intervalInTicks);
        }

        registerCommands();
        registerEvents(
                new UpdateNotificator(),
                new ChatSendListener(),
                new PlayerJoinListener()
        );
    }

    @Override
    public void onDisable() {warpHandler.saveWarps();}

    private void registerCommands(){
        new PwarpMainCommand().registerMainCommand(this, "pwarp");
    }

    public void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    @Nullable
    public Plugin getPlugin(String pluginName) {
        return getServer().getPluginManager().getPlugin(pluginName);
    }

    public boolean isPluginLoaded(String pluginName) {
        return getPlugin(pluginName) != null;
    }

    public boolean isPluginEnabled(String pluginName) {
        return getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    public BukkitScheduler getScheduler() {
        return getServer().getScheduler();
    }

    public void runAsync(Runnable runnable) {
        getScheduler().runTaskAsynchronously(this, runnable);
    }

    public void runSync(Runnable runnable) {
        getScheduler().runTask(this, runnable);
    }

    public void runDelayed(Runnable runnable, long delay) {
        getScheduler().runTaskLater(this, runnable, delay);
    }

    public <T> CompletableFuture<T> completableFuture(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runAsync(() -> {
            try {
                future.complete(callable.call());
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        });
        return future;
    }

    public static PlayerWarpsPlugin get(){
        return PlayerWarpsPlugin.plugin;
    }

    public static WarpHandler getWarpHandler() {
        return warpHandler;
    }

    public static DataManager getDataManager() {
        return dataManager;
    }

    public static void setWarpHandler(WarpHandler warpHandler) {
        PlayerWarpsPlugin.warpHandler = warpHandler;
    }

    public static void setDataManager(DataManager dataManager) {
        PlayerWarpsPlugin.dataManager = dataManager;
    }

    public static void setPlugin(PlayerWarpsPlugin plugin) {
        PlayerWarpsPlugin.plugin = plugin;
    }

    public static String getLatestVersion() {
        return latestVersion;
    }

    public static void setLatestVersion(String latestVersion) {
        PlayerWarpsPlugin.latestVersion = latestVersion;
    }
}
