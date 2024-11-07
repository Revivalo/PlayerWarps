package dev.revivalo.playerwarps.hook.register;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.hook.Hook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook implements Hook<Void> {
    private boolean isHooked;

    @Override
    public void register() {
        isHooked = isPluginEnabled("ItemsAdder");
        if (isHooked){
            PlayerWarpsPlugin.get().registerListeners(new ItemsAdderLoadDataListener());
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

    private static class ItemsAdderLoadDataListener implements Listener {
        @EventHandler
        public void onItemsAdderLoad(final ItemsAdderLoadDataEvent event){
            PlayerWarpsPlugin.getWarpHandler().loadWarps();
        }
    }
}
