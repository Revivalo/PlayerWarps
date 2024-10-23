package dev.revivalo.playerwarps.warp.checker;

import dev.revivalo.playerwarps.hook.HookManager;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.land.LandWorld;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class AngeschossenLandsChecker implements Checker {
    @Override
    public boolean validate(Player player) {
        LandWorld world = HookManager.getAngeschossenLands().getApi().getWorld(player.getWorld());
        if (world == null) {
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        Land land = world.getLandByChunk(chunk.getX(), chunk.getZ());
        if (land == null) {
            return true;
        }

        return land.isTrusted(player.getUniqueId());
    }
}
