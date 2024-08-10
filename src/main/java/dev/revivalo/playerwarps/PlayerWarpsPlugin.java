package dev.revivalo.playerwarps;

import com.tchristofferson.configupdater.ConfigUpdater;
import dev.revivalo.playerwarps.category.CategoryManager;
import dev.revivalo.playerwarps.commandmanager.command.PwarpMainCommand;
import dev.revivalo.playerwarps.configuration.Data;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.hook.Hook;
import dev.revivalo.playerwarps.listener.ChatSendListener;
import dev.revivalo.playerwarps.listener.PlayerJoinListener;
import dev.revivalo.playerwarps.updatechecker.UpdateChecker;
import dev.revivalo.playerwarps.updatechecker.UpdateNotificator;
import dev.revivalo.playerwarps.util.VersionUtil;
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
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class PlayerWarpsPlugin extends JavaPlugin {
    private static PlayerWarpsPlugin plugin;
    private static WarpHandler warpHandler;
    private static Data data;
    private static String latestVersion;

    @Override
    public void onEnable() {
        setPlugin(this);
        Config.reload();

        new Metrics(this, 12061);

        Hook.hook();

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
                VersionUtil.setLatestVersion(!isNewerVersion);
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
        setDataManager(new Data());

        setWarpHandler(new WarpHandler());
        warpHandler.loadWarps();

        if (Config.AUTOSAVE_ENABLED.asBoolean()) {
            long intervalInTicks = (Config.AUTOSAVE_INTERVAL.asLong() * 60) * 20;
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

    public BukkitTask runRepeating(Runnable runnable, long delay, long period) {
        return getScheduler().runTaskTimer(this, runnable, delay, period);
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

    public static Data getData() {
        return data;
    }

    public static void setWarpHandler(WarpHandler warpHandler) {
        PlayerWarpsPlugin.warpHandler = warpHandler;
    }

    public static void setDataManager(Data data) {
        PlayerWarpsPlugin.data = data;
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
