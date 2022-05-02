package cz.revivalo.playerwarps.updatechecker;

import cz.revivalo.playerwarps.PlayerWarps;
import cz.revivalo.playerwarps.lang.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Notification implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if (p.isOp()){
            if (Boolean.parseBoolean(Lang.UPDATECHECKER.content())) {
                if (!PlayerWarps.newestVersion) {
                    p.sendMessage("§f[§bPlayer§3Warps§f]§f There is a new version of plugin. Download:");
                    p.sendMessage("§f§nhttps://bit.ly/revivalo-playerwarps");
                }
            }
        }
    }
}
