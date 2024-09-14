package dev.revivalo.playerwarps.hook.register;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.hook.Hook;
import dev.revivalo.playerwarps.listener.ItemsAdderLoadDataListener;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook implements Hook<Void> {
    private boolean isHooked;

    @Override
    public void register() {
        isHooked = isPluginEnabled("ItemsAdder");
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
