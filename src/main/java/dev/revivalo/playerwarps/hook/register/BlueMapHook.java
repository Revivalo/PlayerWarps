package dev.revivalo.playerwarps.hook.register;

import de.bluecolored.bluemap.api.BlueMapAPI;
import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.hook.Hook;
import dev.revivalo.playerwarps.warp.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class BlueMapHook implements Hook<BlueMapAPI> {
    BlueMapAPI blueMapAPI = null;
    @Override
    public void register() {
        this.blueMapAPI = (getPlugin("BlueMap") != null ? BlueMapAPI.getInstance().get() : null);
        if (isOn()) {

        }
    }

    @Override
    public boolean isOn() {
        return blueMapAPI != null;
    }

    @Override
    public @Nullable BlueMapAPI getApi() {
        return blueMapAPI;
    }

    public void setMarker(Warp warp) {
        if (isOn()) {
//            String markerId = warp.getWarpID().toString();
//            String markerLabel = Config.DYNMAP_MARKER_LABEL.asString()
//                    .replace("%warp%", warp.getName())
//                    .replace("%owner%", Bukkit.getOfflinePlayer(warp.getOwner()).getName());
//
//            Location location = warp.getLocation();
//            Marker marker = markerSet.createMarker(
//                    markerId,
//                    markerLabel,
//                    location.getWorld().getName(),
//                    location.getX(),
//                    location.getY(),
//                    location.getZ(),
//                    markerAPI.getMarkerIcon(Config.DYNMAP_MARKER_ICON.asString()),
//                    false);
//            if (marker == null) {
//                PlayerWarpsPlugin.get().getLogger().log(Level.WARNING, "Failed to create a marker.");
//            }
        }
    }

    public void removeMarker(Warp warp) {
        if (isOn()) {
//            String markerId = warp.getWarpID().toString();
//
//            Marker marker = markerSet.findMarker(markerId);
//            if (marker == null) {
//                return;
//            }
//
//            marker.deleteMarker();
//        }
        }
    }
}
