package dev.revivalo.playerwarps.hook.register;

import dev.revivalo.playerwarps.PlayerWarpsPlugin;
import dev.revivalo.playerwarps.configuration.file.Config;
import dev.revivalo.playerwarps.hook.IHook;
import dev.revivalo.playerwarps.warp.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class DynmapHook implements IHook<DynmapAPI> {
    private DynmapAPI dynmapAPI;

    private MarkerAPI markerAPI;
    private MarkerSet markerSet;

    @Override
    public void register() {
        this.dynmapAPI = (DynmapAPI) getPlugin("dynmap");
        if (isOn()) {
            markerAPI = dynmapAPI.getMarkerAPI();
            if (markerAPI == null) {
                PlayerWarpsPlugin.get().getLogger().log(Level.WARNING, "Cannot invoke MarkerAPI from dynmap plugin.");
                return;
            }

            String markerSetId = "playerwarps_athelion";
            markerSet = markerAPI.getMarkerSet(markerSetId);
            if (markerSet == null) {
                markerSet = markerAPI.createMarkerSet(markerSetId, "PlayerWarp's Markers", null, false);
            }

            if (markerSet == null) {
                PlayerWarpsPlugin.get().getLogger().log(Level.WARNING, "You cannot create or obtain a marker set.");
            }
        }
    }

    @Override
    public boolean isOn() {
        return dynmapAPI != null;
    }

    @Override
    public @Nullable DynmapAPI getApi() {
        return dynmapAPI;
    }

    public void setMarker(Warp warp) {
        if (isOn()) {
            String markerId = warp.getWarpID().toString();
            String markerLabel = Config.DYNMAP_MARKER_LABEL.asString()
                    .replace("%warp%", warp.getName())
                    .replace("%owner%", Bukkit.getOfflinePlayer(warp.getOwner()).getName());

            Location location = warp.getLocation();
            Marker marker = markerSet.createMarker(
                    markerId,
                    markerLabel,
                    location.getWorld().getName(),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    markerAPI.getMarkerIcon(Config.DYNMAP_MARKER_ICON.asString()),
                    false);
            if (marker == null) {
                PlayerWarpsPlugin.get().getLogger().log(Level.WARNING, "Failed to create a marker.");
            }
        }
    }

    public void removeMarker(Warp warp) {
        if (isOn()) {
            String markerId = warp.getWarpID().toString();

            Marker marker = markerSet.findMarker(markerId);
            if (marker == null) {
                return;
            }

            marker.deleteMarker();
        }
    }
}
