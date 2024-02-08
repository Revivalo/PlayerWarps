package dev.revivalo.playerwarps.hooks.registers;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.hooks.Hook;
import dev.revivalo.playerwarps.listeners.ItemsAdderLoadDataListener;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook implements Hook<Void> {
    private final boolean isHooked;
    public ItemsAdderHook(){
        isHooked = PlayerWarpsPlugin.get().isPluginEnabled("ItemsAdder");
        if (isHooked){
            PlayerWarpsPlugin.get().registerEvents(new ItemsAdderLoadDataListener());
        }
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Override
    public @Nullable Void getApi() {
        return null;
    }
}
