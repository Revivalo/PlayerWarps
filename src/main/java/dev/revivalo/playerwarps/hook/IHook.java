package dev.revivalo.playerwarps.hook;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public interface IHook<T> {
    default void preRegister() {
        register();
        if (isOn()) {
            PlayerWarpsPlugin.get().getLogger().log(Level.INFO, this.getClass().getSimpleName() + " has been registered.");
        }
    }

    default boolean isPluginEnabled(String name) {
        return PlayerWarpsPlugin.get().isPluginEnabled(name);
    }

    default Plugin getPlugin(String name) {
        return PlayerWarpsPlugin.get().getPlugin(name);
    }

    void register();
    boolean isOn();

    @Nullable
    T getApi();
}
