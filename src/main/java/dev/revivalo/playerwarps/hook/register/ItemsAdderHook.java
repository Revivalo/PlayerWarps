package dev.revivalo.playerwarps.hook.register;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.hook.IHook;
import dev.revivalo.playerwarps.listener.ItemsAdderLoadDataListener;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook implements IHook<Void> {
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
