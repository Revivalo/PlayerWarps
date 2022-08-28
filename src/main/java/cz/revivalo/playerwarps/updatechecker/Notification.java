package cz.revivalo.playerwarps.updatechecker;

import cz.revivalo.playerwarps.PlayerWarps;
import cz.revivalo.playerwarps.lang.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Notification implements Listener {
    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent event){
        final Player player = event.getPlayer();
        if (player.isOp()){
            if (Boolean.parseBoolean(Lang.UPDATE_CHECKER.getString())) {
                if (!PlayerWarps.newestVersion) {
                    player.sendMessage(Lang.applyColor("&f[&bPlayer&3Warps&f]&f There is a new version of plugin. Download:"));
                    player.sendMessage(Lang.applyColor("&f&nhttps://bit.ly/revivalo-playerwarps"));
                }
            }
        }
    }
}
