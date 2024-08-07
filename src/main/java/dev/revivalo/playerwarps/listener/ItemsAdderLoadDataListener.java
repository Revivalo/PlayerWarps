package dev.revivalo.playerwarps.listener;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderLoadDataListener implements Listener {
    @EventHandler
    public void onItemsAdderLoad(final ItemsAdderLoadDataEvent event){
        PlayerWarpsPlugin.getWarpHandler().loadWarps();
    }
}
